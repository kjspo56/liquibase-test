package com.example.liquibasetest.dto;

import lombok.Data;

@Data
public class DynamicDTO {
    private Long seq;
    private String dynamicColumnName;
    private String dynamicColumnValue;
    private String tableName;
}
