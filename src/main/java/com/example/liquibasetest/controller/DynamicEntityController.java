package com.example.liquibasetest.controller;

import com.example.liquibasetest.dto.TableNameRequestDTO;
import com.example.liquibasetest.entity.DynamicEntity;
import com.example.liquibasetest.service.DynamicEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dynamic-entities")
public class DynamicEntityController {

    private final DynamicEntityService dynamicEntityService;

    public DynamicEntityController(DynamicEntityService dynamicEntityService) {
        this.dynamicEntityService = dynamicEntityService;
    }

    @PostMapping
    public ResponseEntity<DynamicEntity> createDynamicEntity(@RequestBody DynamicEntity dynamicEntity) {
        DynamicEntity createdEntity = dynamicEntityService.saveDynamicEntity(dynamicEntity);
        return ResponseEntity.ok(createdEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DynamicEntity> getDynamicEntityById(@PathVariable Long id) {
        DynamicEntity dynamicEntity = dynamicEntityService.getDynamicEntityById(id);
        if (dynamicEntity != null) {
            return ResponseEntity.ok(dynamicEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/list")
    public List<DynamicEntity> getDynamicTableData(@RequestBody TableNameRequestDTO tableNameRequestDTO){
        return dynamicEntityService.getDynamicTableData(tableNameRequestDTO);
    }
}
