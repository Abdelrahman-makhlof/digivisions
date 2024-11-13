package com.digivision.employee.management.service;

import com.digivision.employee.management.exception.ThirdPartyException;
import com.digivision.employee.management.thirdparty.DepartmentVerificationResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class DepartmentValidationServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private DepartmentValidationService departmentValidationService;

    @Test
    void testIsValidDepartment_SuccessfulValidation() throws ThirdPartyException {

        DepartmentVerificationResponse response = new DepartmentVerificationResponse();
        response.setValid(true);
        when(restTemplate.getForObject(anyString(), eq(DepartmentVerificationResponse.class))).thenReturn(response);

        boolean isValid = departmentValidationService.isValidDepartment("Engineering");

        assertTrue(isValid, "Expected department to be valid");
        verify(restTemplate, times(1)).getForObject(anyString(), eq(DepartmentVerificationResponse.class));
    }

    @Test
    void testIsValidDepartment_InvalidDepartment() throws ThirdPartyException {
        DepartmentVerificationResponse response = new DepartmentVerificationResponse();
        response.setValid(false);
        when(restTemplate.getForObject(anyString(), eq(DepartmentVerificationResponse.class))).thenReturn(response);

        boolean isValid = departmentValidationService.isValidDepartment("InvalidDepartment");

        assertFalse(isValid, "Expected department to be invalid");
        verify(restTemplate, times(1)).getForObject(anyString(), eq(DepartmentVerificationResponse.class));
    }

    @Test
    void testIsValidDepartment_ThrowsThirdPartyException() {
        when(restTemplate.getForObject(anyString(), eq(DepartmentVerificationResponse.class))).thenThrow(new RuntimeException("Service unavailable"));

        ThirdPartyException exception = assertThrows(ThirdPartyException.class, () -> departmentValidationService.isValidDepartment("Engineering"));

        assertEquals("Failed to validate email", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(DepartmentVerificationResponse.class));
    }
}
