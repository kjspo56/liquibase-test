package com.example.liquibasetest.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Table(name = "dynamic_entity")
public class DynamicEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "dynamic_column_name")
    private String dynamicColumnName;

    @Column(name = "dynamic_colmn_value")
    private String dynamicColumnValue;

    @Column(name = "table_name")
    private String tableName;

}
