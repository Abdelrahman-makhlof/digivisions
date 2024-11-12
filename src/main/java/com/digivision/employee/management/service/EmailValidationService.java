package com.digivision.employee.management.service;

import com.digivision.employee.management.thirdparty.EmailValidationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EmailValidationService {

    @Value("${email.verification.api.url}")
    private String emailVerificationApiUrl;
    @Value("${email.verification.api.key}")
    private String apiKey;
    @Autowired
    private RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(EmailValidationService.class);

    @RateLimiter(name = "emailValidationService")
    @CircuitBreaker(name = "emailValidationService", fallbackMethod = "emailValidationFallback")
    public boolean isValidEmail(String email) {
        logger.info("Starting validation for email: {}", email);
        long startTime = System.currentTimeMillis();
        try {

            String url = UriComponentsBuilder.fromHttpUrl(emailVerificationApiUrl)
                    .queryParam("email", email)
                    .queryParam("api_key", apiKey)
                    .toUriString();

            logger.debug("Validation request: {}", url);

            EmailValidationResponse response = restTemplate.getForObject(url, EmailValidationResponse.class);

            boolean valid = "valid".equals(response.getData().getStatus());
            logger.info("End validation for email: {}, Status: {}, ElapsedTime {}", email, valid, (System.currentTimeMillis() - startTime));
            return valid;

        } catch (Exception exception) {
            logger.error("Failed to validate request", exception);
            throw exception;
        }
    }

    public boolean emailValidationFallback(String email, Throwable throwable) {
        logger.error("Fallback triggered for email validation for email {}", email, throwable);
        return false;
    }
}
