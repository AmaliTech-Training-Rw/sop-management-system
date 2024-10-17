package com.team.authentication_service.services;

import com.team.authentication_service.dtos.*;
import com.team.authentication_service.kafkaServices.KafkaProducer;
import com.team.authentication_service.models.Department;
import com.team.authentication_service.models.Role;
import com.team.authentication_service.models.User;
import com.team.authentication_service.models.UserRole;
import com.team.authentication_service.repositories.*;
import com.team.authentication_service.utils.JwtUtil;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final OtpRepository otpRepository;
    private final OtpService otpService;
    private final DepartmentRepository departmentRepository;
    UserRepository userRepository;
    AuthenticationManager authenticationManager;
    PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaProducer kafkaProducer;
    private UserRoleService userRoleService;

    public AuthService(UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       KafkaProducer kafkaProducer,
                       RoleRepository roleRepository,
                       UserRoleService userRoleService,
                       UserRoleRepository userRoleRepository,
                       OtpRepository otpRepository,
                       OtpService otpService,
                       DepartmentRepository departmentRepository
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.kafkaProducer = kafkaProducer;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.otpRepository = otpRepository;
        this.otpService = otpService;
        this.departmentRepository = departmentRepository;
    }

    public void register(RegisterRequestDto registerRequestDto) throws Exception {
//      Check whether there is a user already associated to the email
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent())
            throw new ReturnableException("Email already exists", HttpStatus.BAD_REQUEST);

//      Find and check whether the department specified is existent
        Optional<Department> department = departmentRepository.findById(registerRequestDto.getDepartment());
        if (department.isEmpty())
            throw new ReturnableException("Ensure you've provided a valid department id", HttpStatus.BAD_REQUEST);


//      Loop through the roles in the request to be assigned to the user and
//      check whether they are existent
        Arrays.stream(registerRequestDto.getRoles()).forEach(role -> {
                    Optional<Role> possibleRoleInDb = roleRepository.findByName(role.getName());

                    if (possibleRoleInDb.isEmpty()) {
                        try {
                            throw new ReturnableException("No role of given Id", HttpStatus.NOT_FOUND);
                        } catch (ReturnableException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );

        User user = User
                .builder()
                .name(registerRequestDto.getName())
                .email(registerRequestDto.getEmail())
                .position(registerRequestDto.getPosition())
                .department(department.get())
                .build();


        try {
//          Save the user object to the database
            userRepository.save(user);

//          make a list of roles
            List<Role> roles = List.of(registerRequestDto.getRoles());

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("roles", roles);

//          Create an email, embed and embed it into an email account
//          then send it to the user's email through which they'll set there account password
            SendEmailRequestDto emailRequestDto = SendEmailRequestDto.builder()
                    .recipient(registerRequestDto.getEmail())
                    .subject("SOP System Account Setup")
                    .body(
                            "<html><body>"
                                    + "<h1>Welcome!</h1>"
                                    + "<p>Please click <a href='https://yourdomain.com/auth/create-password?token=+"
                                    + jwtUtil.generateToken(extraClaims,
                                    user) + ">here</a> to set up your account password.</p>"
                                    + "</body></html>"
                    )
                    .build();

            kafkaProducer.sendMessage("email-service-send", emailRequestDto);
        } catch (Exception e) {
            userRepository.delete(user);
            throw new Exception(e.getMessage());
        }
    }

    public User login(LoginRequestDto loginRequestDto) throws ReturnableException {
//      Check whether the given email exists in the database and if not return an exception
        Optional<User> user = userRepository.findByEmail(loginRequestDto.getEmail());

        if (user.isEmpty())
            throw new ReturnableException("No account matches the given credentials", HttpStatus.FORBIDDEN);

//      Check whether the password is already set to determine whethe their user is already activated
        if (user.get().getPassword() == null)
            throw new ReturnableException("First visit your email to setup you're account.", HttpStatus.FORBIDDEN);

        try {
//          Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new ReturnableException("Error authenticating", HttpStatus.FORBIDDEN);
        }

//      return the user object
        return user.get();
    }

    public AuthenticatedUserHeaders validateToken(String token) throws Exception {
//      Validate token and extract claims
        Claims claims = jwtUtil.extractAllClaims(token);

//      Find whether the claims within the token match a given user
        Optional<User> user = userRepository.findByEmail(claims.getSubject());
        if (user.isEmpty())
            throw new ReturnableException("Unauthorized token", HttpStatus.FORBIDDEN);


//      Find the user's user roles
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.get().getId());


//      Build a response object and return it
        AuthenticatedUserHeaders userHeaders = AuthenticatedUserHeaders
                .builder()
                .email(user.get().email)
                .position(user.get().getPosition().toString())
                .roles(userRoles.stream().map(userRole -> userRole.getRole().getName()).toList())
                .build();

        return userHeaders;
    }

    public void createPassword(CreatePasswordReqDto passwordReqDto) throws Exception {
//      Check whether the passwords entered match
        if (!passwordReqDto.getPassword().equals(passwordReqDto.getConfirmPassword()))
            throw new ReturnableException("Ensure matching passwords are provided", HttpStatus.FORBIDDEN);

//      Check whether the email has a user associated to it
        User user = userRepository.findByEmail(passwordReqDto.getEmail()).orElseThrow();

//      Check whether a password has already been set and return an exception if so
        if (user.getPassword() != null)
            throw new ReturnableException("You've already set the password", HttpStatus.BAD_REQUEST);

//      Encode the password, set it and persist it to the db
        user.setPassword(passwordEncoder.encode(passwordReqDto.getPassword()));
        userRepository.save(user);
    }

    public void resetPassword(@Valid CreatePasswordReqDto passwordReqDto) throws Exception {
//      Check whether the passwords match
        if (!passwordReqDto.getConfirmPassword().equals(passwordReqDto.getPassword()))
            throw new ReturnableException("Confirm password does not match", HttpStatus.FORBIDDEN);

//      Check whether the email given has an associated account and if not return an exception
        Optional<User> user = userRepository.findByEmail(passwordReqDto.getEmail());
        if (user.isEmpty()) throw new ReturnableException("User not found", HttpStatus.NOT_FOUND);

//      Check whether there is an otp and whether it was verified and throw an exception if not
        boolean otp = otpService.hasOtpAndIsVerfied(user.get().email);
        if (!otp) throw new ReturnableException("OTP not verified. Generate another OTP", HttpStatus.FORBIDDEN);

//      Reset the password and persist it to the database
        user.get().setPassword(passwordEncoder.encode(passwordReqDto.getPassword()));
        userRepository.save(user.get());
    }
}
