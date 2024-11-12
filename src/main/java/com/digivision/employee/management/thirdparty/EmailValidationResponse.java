package com.digivision.employee.management.thirdparty;

import lombok.Data;

@Data
public class EmailValidationResponse {
    private Data data;
   @lombok.Data
   public static class Data {
        private String status;
        private String email;
    }
}
