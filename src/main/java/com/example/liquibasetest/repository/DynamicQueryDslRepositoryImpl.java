package com.example.liquibasetest.repository;


import com.example.liquibasetest.entity.DynamicEntity;
import com.example.liquibasetest.entity.QDynamicEntity;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DynamicQueryDslRepositoryImpl implements DynamicQueryDslRepository{

    private final JPAQueryFactory jpaQueryFactory;

    /*@Override
    public DynamicEntity insertDynamicEntity(String dynamicColumnName, String dynamicColumnValue, String dynamicTableName){
        QDynamicEntity qDynamicEntity = QDynamicEntity.dynamicEntity;

        String query = "INSERT INTO " + dynamicTableName + " (dynamicTableName, dynamicColumnValue) VALUES (?, ?)";
        Query nativeQuery = entityManager.createNativeQuery(query);
        nativeQuery.setParameter(1, dynamicColumnName);
        nativeQuery.setParameter(2, dynamicColumnValue);
        nativeQuery.executeUpdate();
        return null;
    }*/

    @Override
    public void insertDynamicEntity(String dynamicColumnName, String dynamicColumnValue, String dynamicTableName){
        QDynamicEntity qDynamicEntity = QDynamicEntity.dynamicEntity;

        jpaQueryFactory.insert((EntityPath<?>) qDynamicEntity.dynamicTableName.getRoot()).columns(qDynamicEntity.dynamicColumnValue, qDynamicEntity.dynamicColumnName)
                .execute();

    }

}
