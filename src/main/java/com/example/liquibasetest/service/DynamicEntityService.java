package com.example.liquibasetest.service;

import com.example.liquibasetest.dto.DynamicDTO;
import com.example.liquibasetest.dto.TableNameRequestDTO;
import com.example.liquibasetest.entity.DynamicEntity;
import com.example.liquibasetest.repository.DynamicEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicEntityService {

    private final DynamicEntityRepository dynamicEntityRepository;

    @Transactional
    public DynamicEntity saveDynamicEntity(DynamicDTO dynamicDTO) {
        log.debug("create: {}" , dynamicDTO);
        DynamicEntity dynamicEntity = new DynamicEntity();
        dynamicEntity.setDynamicColumnName(dynamicEntity.getDynamicColumnName());
        dynamicEntity.setDynamicColumnValue(dynamicEntity.getDynamicColumnValue());
        dynamicEntity.setDynamicTableName(dynamicEntity.getDynamicTableName());
        //dynamicEntityRepository.save(dynamicEntity);
        dynamicEntityRepository.insertDynamicEntity(dynamicDTO.getDynamicColumnName(), dynamicDTO.getDynamicColumnValue(), dynamicDTO.getDynamicTableName());
        return dynamicEntity;
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
