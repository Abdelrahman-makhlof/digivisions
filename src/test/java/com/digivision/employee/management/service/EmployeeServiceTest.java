package com.digivision.employee.management.service;

import com.digivision.employee.management.exception.EmployeeNotFoundException;
import com.digivision.employee.management.exception.InvalidInputException;
import com.digivision.employee.management.exception.ThirdPartyException;
import com.digivision.employee.management.model.Employee;
import com.digivision.employee.management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmailValidationService emailValidationService;

    @Mock
    private DepartmentValidationService departmentValidationService;

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private UUID employeeId;

    @BeforeEach
    public void setUp() {
        employeeId = UUID.randomUUID();
        employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("abdelrahman@gmail.com");
        employee.setDepartment("Engineering");
        employee.setSalary(50000.0);
    }

    @Test
    public void testCreateEmployee_Success() throws ThirdPartyException {
        // Arrange
        when(emailValidationService.isValidEmail(employee.getEmail())).thenReturn(true);
        when(departmentValidationService.isValidDepartment(employee.getDepartment())).thenReturn(true);
        when(employeeRepository.save(employee)).thenReturn(employee);

        // Act
        Employee createdEmployee = employeeService.createEmployee(employee);

        // Assert
        assertNotNull(createdEmployee);
        assertEquals(employee.getId(), createdEmployee.getId());
        verify(employeeRepository, times(1)).save(employee);
        verify(emailNotificationService, times(1)).sendEmployeeCreationNotification(anyString(), anyString());
    }

    @Test
    public void testCreateEmployee_InvalidEmail() throws ThirdPartyException {
        // Arrange
        when(emailValidationService.isValidEmail(employee.getEmail())).thenReturn(false);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                employeeService.createEmployee(employee)
        );
        assertEquals("Invalid email address.", exception.getMessage());
        verify(employeeRepository, times(0)).save(employee);
    }

    @Test
    public void testGetEmployeeById_Found() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        Employee foundEmployee = employeeService.getEmployeeById(employeeId);

        // Assert
        assertNotNull(foundEmployee);
        assertEquals(employeeId, foundEmployee.getId());
    }

    @Test
    public void testGetEmployeeById_NotFound() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () ->
                employeeService.getEmployeeById(employeeId)
        );
        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    public void testUpdateEmployee_Success() {
        // Arrange
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(UUID.randomUUID());
        updatedEmployee.setFirstName("Abdelrahman");
        updatedEmployee.setLastName("Ahmed");
        updatedEmployee.setEmail("abdelrahman.ahmed@gmail.com");
        updatedEmployee.setDepartment("HR");
        updatedEmployee.setSalary(55000.0);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // Act
        Employee result = employeeService.updateEmployee(employeeId, updatedEmployee);

        // Assert
        assertNotNull(result);
        assertEquals("Abdelrahman", result.getFirstName());
        assertEquals("HR", result.getDepartment());
        assertEquals(55000.0, result.getSalary());
        verify(employeeRepository, times(1)).findById(any(UUID.class));
        verify(employeeRepository, times(1)).save(any());
    }

    @Test
    public void testDeleteEmployee_Success() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(employeeId);

        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    public void testDeleteEmployee_NotFound() {

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());


        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () ->
                employeeService.deleteEmployee(employeeId)
        );
        assertEquals("Employee not found", exception.getMessage());
        verify(employeeRepository, times(0)).delete(any(Employee.class));
    }

    @Test
    public void testGetAllEmployees() {

        when(employeeRepository.findAll()).thenReturn(List.of(employee));


        List<Employee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        verify(employeeRepository, times(1)).findAll();
    }
}
