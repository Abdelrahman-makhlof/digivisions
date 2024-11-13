package com.digivision.employee.management.service;

import com.digivision.employee.management.exception.ThirdPartyException;
import com.digivision.employee.management.thirdparty.EmailValidationResponse;
import com.digivision.employee.management.thirdparty.EmailValidationResponse.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class EmailValidationServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @Autowired
    private EmailValidationService emailValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsValidEmail_Success() throws ThirdPartyException {
        // Arrange
        String email = "abdelrahman@gmail.com";
        EmailValidationResponse response = new EmailValidationResponse();
        Data data = new Data();
        data.setStatus("valid");
        response.setData(data);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(response);

        // Act
        boolean isValid = emailValidationService.isValidEmail(email);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsValidEmail_InvalidEmail() throws ThirdPartyException {

        String email = "invalid@example.com";
        EmailValidationResponse response = new EmailValidationResponse();
        Data data = new Data();
        data.setStatus("invalid");
        response.setData(data);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(response);

        boolean isValid = emailValidationService.isValidEmail(email);
        assertFalse(isValid);
    }

    @Test
    void testIsValidEmail_ExceptionHandling() {

        String email = "error@example.com";
        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class)))
                .thenThrow(new RuntimeException("API error"));

        assertThrows(ThirdPartyException.class, () -> emailValidationService.isValidEmail(anyString()));
    }
}

