package com.example.liquibasetest.dto;

import lombok.Data;

@Data
public class TableNameRequestDTO {
    private String tableName;

    public TableNameRequestDTO(String tableName){
        this.tableName = tableName;
    }

    public TableNameRequestDTO(){

    }
}
