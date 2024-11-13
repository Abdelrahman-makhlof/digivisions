package com.digivision.employee.management.controller;

import com.digivision.employee.management.model.Employee;
import com.digivision.employee.management.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testCreateEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Abdel");
        employee.setLastName("Rahman");
        employee.setEmail("abdel.rahman@gmail.com");
        employee.setDepartment("Finance");
        employee.setSalary(55000);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Abdel"))
                .andExpect(jsonPath("$.email").value("abdel.rahman@gmail.com"))
                .andExpect(jsonPath("$.salary").value("55000.0"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Abdel");
        employee.setLastName("Rahman");
        employee.setEmail("abdel.rahman@gmail.com");
        employee.setDepartment("Engineering");
        employee.setSalary(60000);

        Employee savedEmployee = employeeRepository.save(employee);

        mockMvc.perform(get("/api/employees/{id}", savedEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("abdel.rahman@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Abdel"))
                .andExpect(jsonPath("$.lastName").value("Rahman"))
                .andExpect(jsonPath("$.salary").value("60000.0"));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Abdel");
        employee.setLastName("Rahman");
        employee.setEmail("abdel.rahman@gmail.com");
        employee.setDepartment("Marketing");
        employee.setSalary(65000);
        Employee savedEmployee = employeeRepository.save(employee);

        savedEmployee.setSalary(70000);

        mockMvc.perform(put("/api/employees/{id}", savedEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salary").value(70000));
    }

    @Test
    void testUpdateEmployee_NotFound() throws Exception {
        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setFirstName("Abdel");
        employee.setLastName("Rahman");
        employee.setEmail("abdel.rahman@gmail.com");
        employee.setDepartment("Operations");
        employee.setSalary(50000);

        mockMvc.perform(put("/api/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }

    @Test
    void testUpdateEmployee_BadRequest() throws Exception {
        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());

        mockMvc.perform(put("/api/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Abdel");
        employee.setLastName("Rahman");
        employee.setEmail("abdel.rahman@gmail.com");
        employee.setDepartment("Operations");
        employee.setSalary(50000);
        Employee savedEmployee = employeeRepository.save(employee);

        mockMvc.perform(delete("/api/employees/{id}", savedEmployee.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/employees/{id}", randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }
}

