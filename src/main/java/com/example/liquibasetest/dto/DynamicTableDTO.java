package com.example.liquibasetest.dto;

import lombok.Data;

import java.util.List;

@Data
public class DynamicTableDTO {
    private Long seq;
    private String tableName;
    private List<String> tableColumnNames;
}
