package com.example.liquibasetest.service;

import com.example.liquibasetest.dto.TableNameRequestDTO;
import com.example.liquibasetest.entity.DynamicEntity;
import com.example.liquibasetest.repository.DynamicEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;

@Service
public class DynamicEntityService {

    private final DynamicEntityRepository dynamicEntityRepository;

    public DynamicEntityService(DynamicEntityRepository dynamicEntityRepository) {
        this.dynamicEntityRepository = dynamicEntityRepository;
    }

    public DynamicEntity saveDynamicEntity(DynamicEntity dynamicEntity) {

        return dynamicEntityRepository.save(dynamicEntity);
    }

    public DynamicEntity getDynamicEntityById(Long seq) {
        return dynamicEntityRepository.findById(seq).orElse(null);
    }

    public List<DynamicEntity> getDynamicTableData(@RequestBody TableNameRequestDTO tableNameRequestDTO){
        String tableName = tableNameRequestDTO.getTableName();
        System.out.println(tableName);
        if(tableName == null){
            return Collections.emptyList();
        }
        return dynamicEntityRepository.findByTableName(tableName);
    }

}
