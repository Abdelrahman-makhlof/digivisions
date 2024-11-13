package com.digivision.employee.management.service;

import com.digivision.employee.management.exception.ThirdPartyException;
import com.digivision.employee.management.thirdparty.DepartmentVerificationResponse;
import com.digivision.employee.management.thirdparty.EmailValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
class DepartmentValidationServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @Autowired
    private DepartmentValidationService departmentValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsValidDepartment_Success() throws ThirdPartyException {

        DepartmentVerificationResponse mockResponse = new DepartmentVerificationResponse();
        mockResponse.setValid(true);

        when(restTemplate.getForObject(anyString(), eq(DepartmentVerificationResponse.class))).thenReturn(mockResponse);

        // Act
        boolean isValid = departmentValidationService.isValidDepartment("HR");

        assertTrue(isValid);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(DepartmentVerificationResponse.class));
    }

    @Test
    void testIsValidDepartment_InvalidDepartment() throws ThirdPartyException {

        DepartmentVerificationResponse mockResponse = new DepartmentVerificationResponse();
        mockResponse.setValid(false);

        when(restTemplate.getForObject(anyString(), eq(DepartmentVerificationResponse.class))).thenReturn(mockResponse);

        // Act
        boolean isValid = departmentValidationService.isValidDepartment("HR");

        // Assert
        assertFalse(isValid);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(DepartmentVerificationResponse.class));
    }

    @Test
    void testIsValidDepartment_NullResponse() throws ThirdPartyException {

        when(restTemplate.getForObject(anyString(), eq(DepartmentVerificationResponse.class))).thenReturn(null);

        // Act
        boolean isValid = departmentValidationService.isValidDepartment("HR");

        // Assert
        assertFalse(isValid);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(DepartmentVerificationResponse.class));
    }
}

