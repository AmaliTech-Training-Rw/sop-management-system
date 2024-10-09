//package email_service;
//
//import com.icegreen.greenmail.util.GreenMailUtil;
//import com.team.email_service.service.EmailService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.mail.internet.MimeMessage;
//
//import com.icegreen.greenmail.configuration.GreenMailConfiguration;
//import com.icegreen.greenmail.junit5.GreenMailExtension;
//import com.icegreen.greenmail.util.ServerSetupTest;
//import org.junit.jupiter.api.extension.RegisterExtension;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class EmailServiceIntegrationTest {
//
//    @RegisterExtension
//    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
//            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@example.com", "password"))
//            .withPerMethodLifecycle(false);
//
//    private final EmailService emailService;
//
//
//    @Autowired
//    public EmailServiceIntegrationTest( EmailService emailService) {
//        this.emailService = emailService;
//    }
//
//    private static final String TEST_EMAIL = "jogeci8218@adambra.com";
//
//    @BeforeEach
//    void setUp() {
//        greenMail.reset();
//    }
//
//    @Test
//    void testSendVerificationEmail() throws Exception {
//        String token = "testToken123";
//        emailService.sendVerificationEmail(TEST_EMAIL, token);
//
//        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
//        assertEquals(1, receivedMessages.length);
//
//        MimeMessage receivedMessage = receivedMessages[0];
//        assertEquals(TEST_EMAIL, receivedMessage.getAllRecipients()[0].toString());
//        assertEquals("Email Verification", receivedMessage.getSubject());
//        assertTrue(GreenMailUtil.getBody(receivedMessage).contains(token));
//        assertTrue(GreenMailUtil.getBody(receivedMessage).contains("http://localhost:7080/auth/verify?token="));
//    }
//
//    @Test
//    void testSendPasswordResetEmail() throws Exception {
//        String token = "resetToken456";
//        emailService.sendPasswordResetEmail(TEST_EMAIL, token);
//
//        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
//        assertEquals(1, receivedMessages.length);
//
//        MimeMessage receivedMessage = receivedMessages[0];
//        assertEquals(TEST_EMAIL, receivedMessage.getAllRecipients()[0].toString());
//        assertEquals("Password Reset", receivedMessage.getSubject());
//        assertTrue(GreenMailUtil.getBody(receivedMessage).contains(token));
//        assertTrue(GreenMailUtil.getBody(receivedMessage).contains("http://localhost:7080/auth/reset-password?token="));
//    }
//
//    @Test
//    void testSendOtpEmail() throws Exception {
//        String otp = "123456";
//        emailService.sendOtpEmail(TEST_EMAIL, otp);
//
//        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
//        assertEquals(1, receivedMessages.length);
//
//        MimeMessage receivedMessage = receivedMessages[0];
//        assertEquals(TEST_EMAIL, receivedMessage.getAllRecipients()[0].toString());
//        assertEquals("One-Time Password for Password Reset", receivedMessage.getSubject());
//        assertTrue(GreenMailUtil.getBody(receivedMessage).contains(otp));
//    }
//
//    // Add more tests for other email sending methods...
//}