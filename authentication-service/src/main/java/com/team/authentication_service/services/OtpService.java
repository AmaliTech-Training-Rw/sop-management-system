package com.team.authentication_service.services;

import com.team.authentication_service.dtos.SendEmailRequestDto;
import com.team.authentication_service.kafkaServices.KafkaProducer;
import com.team.authentication_service.models.Otp;
import com.team.authentication_service.models.User;
import com.team.authentication_service.repositories.OtpRepository;
import com.team.authentication_service.repositories.UserRepository;
import com.team.authentication_service.utils.JwtUtil;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class OtpService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private OtpRepository otpRepository;
    private final KafkaProducer kafkaProducer;

    public OtpService(OtpRepository otpRepository, UserRepository userRepository, KafkaProducer kafkaProducer, JwtUtil jwtUtil) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
        this.jwtUtil = jwtUtil;
    }

    public void generateOtp(String email) throws ReturnableException {
//       Find whether there is a use rassociated to the given email and if not throw an exception
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new ReturnableException("No user of given email", HttpStatus.NOT_FOUND);


//      Check whether a password has already been set and if not throw an exception
        if (user.get().getPassword() == null)
            throw new ReturnableException("First visit your email to setup you're account.", HttpStatus.BAD_REQUEST);

//      Generate an otp and save it to the db
        String otpCode = generateOTP(6);
        otpRepository.save(
                Otp
                        .builder()
                        .code(otpCode)
                        .user(user.get())
                        .build()
        );


//      send email containing the otp to the user's email
        SendEmailRequestDto emailRequestDto = SendEmailRequestDto.builder()
                .recipient(email)
                .subject("SOP System Account Setup")
                .body(
                        "<html><body>"
                                + "<h1>Reset Password</h1>"
                                + "<p>Here is an OTP code to reset your password <b>"
                                + otpCode
                                + "</b></p>"
                                + "</body></html>"
                )
                .build();

        kafkaProducer.sendMessage("email-service-send", emailRequestDto);
    }

    public boolean validateOtp(String code) throws Exception {
//      Query the db for the otp
        Otp otp = otpRepository.findByCode(code);

//      Validations to the otp and comparison with the one provided
        if (otp == null)
            throw new ReturnableException("Invalid OTP. Please generate one", HttpStatus.BAD_REQUEST);
        if (!otp.getCode().equals(code))
            throw new ReturnableException("Invalid OTP. Please generate a new one", HttpStatus.BAD_REQUEST);
        if (isOtpExpired(otp))
            throw new ReturnableException("OTP Expired. Please generate a new one", HttpStatus.BAD_REQUEST);
        if (otp.isVerified())
            throw new ReturnableException("OTP already verified. Please generate a new one", HttpStatus.BAD_REQUEST);

//        Set the otp as verified, persist the change to the db, and return true
        otp.setVerified(true);
        otpRepository.save(otp);
        return true;
    }

    private String generateOTP(int length) {
//      Generate an otp
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public boolean isOtpExpired(Otp otp) {
        return otp.isExpired(300);
    }

    public boolean hasOtpAndIsVerfied(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) return false;

        Optional<Otp> otp = Optional.ofNullable(otpRepository.findByUser(user.get()));
        System.out.println(otp.get());
        return otp.isPresent() && otp.get().isVerified();
    }
}