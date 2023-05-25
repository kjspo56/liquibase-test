package com.example.liquibasetest.repository;

import com.example.liquibasetest.entity.DynamicTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicTableRepository extends JpaRepository<DynamicTable, Long> {
}
