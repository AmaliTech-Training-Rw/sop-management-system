package com.team.authentication_service.services;

import com.team.authentication_service.dtos.SendEmailRequestDto;
import com.team.authentication_service.kafkaServices.KafkaProducer;
import com.team.authentication_service.models.Otp;
import com.team.authentication_service.models.User;
import com.team.authentication_service.repositories.OtpRepository;
import com.team.authentication_service.repositories.UserRepository;
import com.team.authentication_service.utils.JwtUtil;
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

    public void generateOtp(String email) throws Exception {
        try {
            Optional<User> user = userRepository.findByEmail(email);


            if (user.isEmpty()) throw new Exception("No user of given email");

            if (user.get().getPassword() == null)
                throw new Exception("First visit your email to setup you're account.");

            String otpCode = generateOTP(6);

            System.out.println("Email");

            otpRepository.save(
                    Otp
                            .builder()
                            .code(otpCode)
                            .user(user.get())
                            .build()
            );


//            send email
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
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean validateOtp(String code) throws Exception {
        try {
            Otp otp = otpRepository.findByCode(code);

            if (otp == null) throw new Exception("Invalid OTP. Please generate one");

            if (!otp.getCode().equals(code)) throw new Exception("Invalid OTP. Please generate a new one");

            if (isOtpExpired(otp)) throw new Exception("OTP Expired. Please generate a new one");

            if (otp.isVerified()) throw new Exception("OTP already verified. Please generate a new one");

            otp.setVerified(true);

            otpRepository.save(otp);

            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    private String generateOTP(int length) {
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

//    @Scheduled(fixedRate = 60000)
//    public void deleteExpiredOtps() {
//        List<Otp> otps = otpRepository.findAll();
//        otps.forEach(otp -> {
//            if (this.isOtpExpired(otp)) {
//                otpRepository.delete(otp);
//            }
//        });
//    }
}