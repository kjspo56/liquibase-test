package com.example.liquibasetest.repository;

import com.example.liquibasetest.entity.DynamicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DynamicEntityRepository extends JpaRepository<DynamicEntity, Long>, JpaSpecificationExecutor<DynamicEntity>, DynamicQueryDslRepository, QuerydslPredicateExecutor<DynamicEntity> {


    @Query("SELECT d FROM DynamicEntity d WHERE d.dynamicTableName = :tableName")
    List<DynamicEntity> findByTableName(@Param("tableName") String tableName);



    /*@Modifying
    @Transactional
    @Query(value = "INSERT INTO :dynamicTableName (dynamic_column_name, dynamic_column_value) " +
            "VALUES (:#{#dynamicColumnName}, :#{#dynamicColumnValue})", nativeQuery = true)
    void insertDynamicEntity(@Param("dynamicTableName") String dynamicTableName,
                             @Param("dynamicColumnName") String dynamicColumnName,
                             @Param("dynamicColumnValue") String dynamicColumnValue);*/


}
