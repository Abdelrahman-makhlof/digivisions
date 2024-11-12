package com.digivision.employee.management.service;

import com.digivision.employee.management.exception.ThirdPartyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    @Async
    public void sendEmployeeCreationNotification(String toEmail, String employeeName) throws ThirdPartyException {
       long startTime =System.currentTimeMillis();
        logger.info("Start to send employee notification email");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Employee Created Successfully");
        message.setText("Dear " + employeeName + ",\n\nYour employee profile has been created successfully.");

        logger.debug("Email details: {}", message);
        try {
            mailSender.send(message);
        } catch (RuntimeException ex) {
            logger.error("Failed to send email notification",ex);
            throw new ThirdPartyException("Failed to send email notification");
        }
        logger.info("Sending employee notification email ended, elapsedTime: {}",(System.currentTimeMillis()-startTime));
    }
}