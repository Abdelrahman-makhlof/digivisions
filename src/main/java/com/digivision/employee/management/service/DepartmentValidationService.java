package com.digivision.employee.management.service;

import com.digivision.employee.management.exception.ThirdPartyException;
import com.digivision.employee.management.thirdparty.DepartmentVerificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class DepartmentValidationService {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${department.verification.api.url}")
    private String departmentVerificationApiUrl;

    @Value("${department.verification.api.key}")
    private String apiKey;

    private static final Logger logger = LoggerFactory.getLogger(DepartmentValidationService.class);

    public boolean isValidDepartment(String department) throws ThirdPartyException {
        logger.info("Start department validation");

       try {
           String url = UriComponentsBuilder.fromHttpUrl(departmentVerificationApiUrl)
                   .queryParam("department", department)
                   .queryParam("api_key", apiKey) // Use your API key here
                   .toUriString();

        logger.debug("Request: {}", url);
        DepartmentVerificationResponse response = restTemplate.getForObject(url, DepartmentVerificationResponse.class);
        logger.debug("Response: {}", response);
        boolean valid =response != null && response.isValid();
        logger.info("Valid department status: {}", valid);

        return valid;
       } catch (Exception exception) {
           logger.error("Failed to validate department", exception);
           throw new ThirdPartyException("Failed to validate email");
       }
    }
}