package com.example.liquibasetest.repository;

import com.example.liquibasetest.entity.DynamicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DynamicEntityRepository extends JpaRepository<DynamicEntity, Long> {

    @Query("SELECT d FROM DynamicEntity d WHERE d.tableName = :tableName")
    List<DynamicEntity> findByTableName(@Param("tableName") String tableName);

}
