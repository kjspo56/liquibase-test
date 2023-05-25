package com.example.liquibasetest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "dynamic_table")
public class DynamicTable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dynamicTable_seq")
    private Long seq;

    @Column(name = "dynamic_name")
    private String dynamicName;
}
