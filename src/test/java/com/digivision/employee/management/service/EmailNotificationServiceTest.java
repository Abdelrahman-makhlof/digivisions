package com.digivision.employee.management.service;

import com.digivision.employee.management.exception.ThirdPartyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    private String toEmail;
    private String employeeName;

    @BeforeEach
    public void setUp() {
        toEmail = "abdelrahan@gmail.com";
        employeeName = "Abdelrahman";
    }

    @Test
    public void testSendEmployeeCreationNotification_Success() throws ThirdPartyException {
        // Arrange
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Employee Created Successfully");
        message.setText("Dear " + employeeName + ",\n\nYour employee profile has been created successfully.");

        doNothing().when(mailSender).send(any(SimpleMailMessage.class)); // Mocking mailSender to do nothing

        emailNotificationService.sendEmployeeCreationNotification(toEmail, employeeName);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class)); // Verifying mailSender's send method was called
    }

    @Test
    public void testSendEmployeeCreationNotification_Failure() {
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class)); // Simulating failure

        assertThrows(ThirdPartyException.class, () ->
                emailNotificationService.sendEmployeeCreationNotification(toEmail, employeeName)
        );
    }
}
