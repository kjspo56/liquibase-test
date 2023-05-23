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

    @Column(name = "dynamic_table_name")
    private String dynamicTableName;

    public DynamicEntity(Long seq, String dynamicColumnName, String dynamicColumnValue, String dynamicTableName){
        this.seq = seq;
        this.dynamicColumnName = dynamicColumnName;
        this.dynamicColumnValue = dynamicColumnValue;
        this.dynamicTableName = dynamicTableName;
    }

    public DynamicEntity() {

    }

    public void updateDynamic(String dynamicColumnName, String dynamicColumnValue, String dynamicTableName){
        this.dynamicColumnName = dynamicColumnName;
        this.dynamicColumnValue = dynamicColumnValue;
        this.dynamicTableName = dynamicTableName;
    }

}
