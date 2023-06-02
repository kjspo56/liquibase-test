package com.example.liquibasetest.controller;

import com.example.liquibasetest.dto.DynamicTableDTO;
import com.example.liquibasetest.service.DynamicTableService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/table")
public class DynamicTableController {

    private final DynamicTableService dynamicTableService;

    public DynamicTableController(DynamicTableService dynamicTableService) {
        this.dynamicTableService = dynamicTableService;
    }

    @PostMapping("/create")
    public DynamicTableDTO create(@RequestBody DynamicTableDTO dynamicTableDTO) {
        return dynamicTableService.create(dynamicTableDTO);
    }

    @PostMapping("/update")
    public DynamicTableDTO update(@RequestBody DynamicTableDTO dynamicTableDTO) {
        return dynamicTableService.update(dynamicTableDTO);
    }
}
