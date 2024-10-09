//package email_service;
//
//import com.team.email_service.service.EmailService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class EmailServiceTest {
//
//    private EmailService emailService;
//
//    @Mock
//    private JavaMailSender mockMailSender;
//
//    @Captor
//    private ArgumentCaptor<SimpleMailMessage> messageCaptor;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        emailService = new EmailService("localhost", 587, "username", "password", "http://localhost:7080");
//        // Use reflection to set the mocked JavaMailSender
//        try {
//            java.lang.reflect.Field mailSenderField = EmailService.class.getDeclaredField("mailSender");
//            mailSenderField.setAccessible(true);
//            mailSenderField.set(emailService, mockMailSender);
//        } catch (Exception e) {
//            fail("Failed to set mocked JavaMailSender: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testSendVerificationEmail() {
//        String to = "jogeci8218@adambra.com";
//        String token = "verificationToken123";
//
//        emailService.sendVerificationEmail(to, token);
//
//        verify(mockMailSender).send(messageCaptor.capture());
//        SimpleMailMessage capturedMessage = messageCaptor.getValue();
//
//        assertEquals(to, capturedMessage.getTo()[0]);
//        assertEquals("Email Verification", capturedMessage.getSubject());
//        assertTrue(capturedMessage.getText().contains(token));
//        assertTrue(capturedMessage.getText().contains("http://localhost:7080/auth/verify?token="));
//    }
//
//    @Test
//    void testSendPasswordResetEmail() {
//        String to = "user@example.com";
//        String token = "resetToken456";
//
//        emailService.sendPasswordResetEmail(to, token);
//
//        verify(mockMailSender).send(messageCaptor.capture());
//        SimpleMailMessage capturedMessage = messageCaptor.getValue();
//
//        assertEquals(to, capturedMessage.getTo()[0]);
//        assertEquals("Password Reset", capturedMessage.getSubject());
//        assertTrue(capturedMessage.getText().contains(token));
//        assertTrue(capturedMessage.getText().contains("http://localhost:8080/auth/reset-password?token="));
//    }
//
//    @Test
//    void testSendOtpEmail() {
//        String to = "user@example.com";
//        String otp = "123456";
//
//        emailService.sendOtpEmail(to, otp);
//
//        verify(mockMailSender).send(messageCaptor.capture());
//        SimpleMailMessage capturedMessage = messageCaptor.getValue();
//
//        assertEquals(to, capturedMessage.getTo()[0]);
//        assertEquals("One-Time Password for Password Reset", capturedMessage.getSubject());
//        assertTrue(capturedMessage.getText().contains(otp));
//    }
//
//    @Test
//    void testSendTaskNotification() {
//        String to = "author@example.com";
//        String taskDescription = "New task description";
//
//        emailService.sendTaskNotification(to, taskDescription);
//
//        verify(mockMailSender).send(messageCaptor.capture());
//        SimpleMailMessage capturedMessage = messageCaptor.getValue();
//
//        assertEquals(to, capturedMessage.getTo()[0]);
//        assertEquals("New Task Assignment", capturedMessage.getSubject());
//        assertTrue(capturedMessage.getText().contains(taskDescription));
//    }
//
//    @Test
//    void testSendSopReturnNotification() {
//        String to = "author@example.com";
//        String sopTitle = "SOP Title";
//
//        emailService.sendSopReturnNotification(to, sopTitle);
//
//        verify(mockMailSender).send(messageCaptor.capture());
//        SimpleMailMessage capturedMessage = messageCaptor.getValue();
//
//        assertEquals(to, capturedMessage.getTo()[0]);
//        assertEquals("SOP Returned", capturedMessage.getSubject());
//        assertTrue(capturedMessage.getText().contains(sopTitle));
//        assertTrue(capturedMessage.getText().contains("returned for revision"));
//    }
//
//    @Test
//    void testSendSopStatusNotification() {
//        String to = "staff@example.com";
//        String sopTitle = "SOP Title";
//        boolean approved = true;
//
//        emailService.sendSopStatusNotification(to, sopTitle, approved);
//
//        verify(mockMailSender).send(messageCaptor.capture());
//        SimpleMailMessage capturedMessage = messageCaptor.getValue();
//
//        assertEquals(to, capturedMessage.getTo()[0]);
//        assertEquals("SOP Status Update", capturedMessage.getSubject());
//        assertTrue(capturedMessage.getText().contains(sopTitle));
//        assertTrue(capturedMessage.getText().contains("approved"));
//
//        // Test for rejection scenario
//        reset(mockMailSender);
//        emailService.sendSopStatusNotification(to, sopTitle, false);
//
//        verify(mockMailSender).send(messageCaptor.capture());
//        capturedMessage = messageCaptor.getValue();
//
//        assertTrue(capturedMessage.getText().contains("rejected"));
//    }
//
//    @Test
//    void testSendNewSopNotification() {
//        String to = "user@example.com";
//        String sopTitle = "New SOP Title";
//
//        emailService.sendNewSopNotification(to, sopTitle);
//
//        verify(mockMailSender).send(messageCaptor.capture());
//        SimpleMailMessage capturedMessage = messageCaptor.getValue();
//
//        assertEquals(to, capturedMessage.getTo()[0]);
//        assertEquals("New SOP Published", capturedMessage.getSubject());
//        assertTrue(capturedMessage.getText().contains(sopTitle));
//        assertTrue(capturedMessage.getText().contains("has been published"));
//    }
//
//    @Test
//    void testGenerateOtp() {
//        String otp = emailService.generateOtp();
//
//        assertNotNull(otp);
//        assertEquals(6, otp.length());
//        assertTrue(otp.matches("\\d{6}"));
//    }
//}