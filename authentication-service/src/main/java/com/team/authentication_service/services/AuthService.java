package com.team.authentication_service.services;

import com.team.authentication_service.dtos.*;
import com.team.authentication_service.kafkaServices.KafkaProducer;
import com.team.authentication_service.models.Department;
import com.team.authentication_service.models.Role;
import com.team.authentication_service.models.User;
import com.team.authentication_service.models.UserRole;
import com.team.authentication_service.repositories.*;
import com.team.authentication_service.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
                       UserRoleRepository userRoleRepository, OtpRepository otpRepository, OtpService otpService, DepartmentRepository departmentRepository) {
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

    public ResponseEntity<String> register(RegisterRequestDto registerRequestDto) throws Exception {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent())
            throw new RuntimeException("Email already exists");

        Optional<Department> department = departmentRepository.findById(registerRequestDto.getDepartment());

        if (department.isEmpty())
            throw new Exception("Ensure you've provided a valid department id");

        Arrays.stream(registerRequestDto.getRoles()).forEach(role -> {
                    Optional<Role> possibleRoleInDb = roleRepository.findByName(role.getName());

                    if (possibleRoleInDb.isEmpty()) {
                        try {
                            throw new Exception("No role of given Id");
                        } catch (Exception e) {
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
            List<Role> roles = List.of(registerRequestDto.getRoles());
            userRepository.save(user);
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("roles", roles);
//            send email
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
            return ResponseEntity.ok("Look into your email inbox to setup your account.");
        } catch (Exception e) {
            userRepository.delete(user);
            throw new Exception(e.getMessage());
        }
    }

    public User login(LoginRequestDto loginRequestDto) throws Exception {
        try {
            System.out.println(loginRequestDto);
            Optional<User> user = userRepository.findByEmail(loginRequestDto.getEmail());

            if (user.isEmpty()) throw new Exception("No account matches the given credentials");

            if (user.get().getPassword() == null)
                throw new Exception("First visit your email to setup you're account.");

            List<GrantedAuthority> authorities = new ArrayList<>(user.get().getAuthorities());

            if (authorities == null) {
                authorities = new ArrayList<>();
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
            );
            System.out.println("Authentication Successful");
            return userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow();
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
    }

    public AuthenticatedUserHeaders validateToken(String token) throws Exception {
        Claims claims = jwtUtil.extractAllClaims(token);

        Optional<User> user = userRepository.findByEmail(claims.getSubject());

        if (user.isEmpty()) throw new Exception("Unauthorized token");

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.get().getId());

        return AuthenticatedUserHeaders
                .builder()
                .email(user.get().email)
                .position(
                        user.get()
                                .getPosition()
                                .toString()
                )
                .roles(
                        userRoles
                                .stream()
                                .map(
                                        userRole -> userRole
                                                .getRole()
                                                .getName()
                                ).toList()
                ).build();
    }

    public void createPassword(CreatePasswordReqDto passwordReqDto) throws Exception {
        if (!passwordReqDto.getPassword().equals(passwordReqDto.getConfirmPassword()))
            throw new Exception("Ensure matching passwords are provided");

        User user = userRepository.findByEmail(passwordReqDto.getEmail()).orElseThrow();

        if (user.getPassword() != null) throw new Exception("You've already set the password");

        user.setPassword(passwordEncoder.encode(passwordReqDto.getPassword()));

        userRepository.save(user);
    }

    public void resetPassword(@Valid CreatePasswordReqDto passwordReqDto) throws Exception {
        System.out.println(passwordReqDto);
        if (!passwordReqDto.getConfirmPassword().equals(passwordReqDto.getPassword()))
            throw new Exception("Confirm password does not match");

        Optional<User> user = userRepository.findByEmail(passwordReqDto.getEmail());

        if (user.isEmpty()) throw new Exception("User not found");

        boolean otp = otpService.hasOtpAndIsVerfied(user.get().email);

        if (!otp) throw new Exception("OTP not verified. Generate another OTP");

        user.get().setPassword(passwordEncoder.encode(passwordReqDto.getPassword()));
        userRepository.save(user.get());
    }
}
