package com.example.liquibasetest.repository;

import com.example.liquibasetest.entity.DynamicEntity;

public interface DynamicQueryDslRepository {
    void insertDynamicEntity(String dynamicTableName, String dynamicColumnName, String dynamicColumnValue);
}
