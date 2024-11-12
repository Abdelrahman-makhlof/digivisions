package com.digivision.employee.management.service;

import com.digivision.employee.management.thirdparty.DepartmentVerificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DepartmentValidationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DepartmentValidationService departmentValidationService;

    @Value("${department.verification.api.url}")
    private String departmentVerificationApiUrl = "https://mock.api/department-verification";

    @Value("${department.verification.api.key}")
    private String apiKey = "mock-api-key";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsValidDepartment_Success() {
        // Arrange
        String department = "HR";
        DepartmentVerificationResponse mockResponse = new DepartmentVerificationResponse();
        mockResponse.setValid(true);

        String url = String.format("%s?department=%s&ap_ikey=%s", departmentVerificationApiUrl, department, apiKey);
        when(restTemplate.getForObject(url, DepartmentVerificationResponse.class)).thenReturn(mockResponse);

        // Act
        boolean isValid = departmentValidationService.isValidDepartment(department);

        // Assert
        assertTrue(isValid);
        verify(restTemplate, times(1)).getForObject(url, DepartmentVerificationResponse.class);
    }

    @Test
    void testIsValidDepartment_InvalidDepartment() {
        // Arrange
        String department = "UnknownDept";
        DepartmentVerificationResponse mockResponse = new DepartmentVerificationResponse();
        mockResponse.setValid(false);

        String url = String.format("%s?department=%s&apikey=%s", departmentVerificationApiUrl, department, apiKey);
        when(restTemplate.getForObject(url, DepartmentVerificationResponse.class)).thenReturn(mockResponse);

        // Act
        boolean isValid = departmentValidationService.isValidDepartment(department);

        // Assert
        assertFalse(isValid);
        verify(restTemplate, times(1)).getForObject(url, DepartmentVerificationResponse.class);
    }

    @Test
    void testIsValidDepartment_NullResponse() {
        // Arrange
        String department = "Finance";
        String url = String.format("%s?department=%s&apikey=%s", departmentVerificationApiUrl, department, apiKey);
        when(restTemplate.getForObject(url, DepartmentVerificationResponse.class)).thenReturn(null);

        // Act
        boolean isValid = departmentValidationService.isValidDepartment(department);

        // Assert
        assertFalse(isValid);
        verify(restTemplate, times(1)).getForObject(url, DepartmentVerificationResponse.class);
    }
}

