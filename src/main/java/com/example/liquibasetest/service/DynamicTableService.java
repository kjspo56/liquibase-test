package com.example.liquibasetest.service;

import com.example.liquibasetest.dto.DynamicTableDTO;
import com.example.liquibasetest.repository.DynamicTableRepository;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.change.AddColumnConfig;
import liquibase.change.ColumnConfig;
import liquibase.change.core.CreateTableChange;
import liquibase.change.core.DropColumnChange;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class DynamicTableService {

    private final DataSource dataSource;
    private final DynamicTableRepository dynamicTableRepository;
    private final LiquibaseProperties liquibaseProperties;

    public DynamicTableService(DataSource dataSource, DynamicTableRepository dynamicTableRepository, LiquibaseProperties liquibaseProperties) {
        this.dataSource = dataSource;
        this.dynamicTableRepository = dynamicTableRepository;
        this.liquibaseProperties = liquibaseProperties;
    }

    public DynamicTableDTO create(DynamicTableDTO dynamicTableDTO) {
        log.debug("dynamicTableDTO", dynamicTableDTO);

        try {
            // Liquibase XML 파일 경로
            ClassPathResource changeLogFileResource = new ClassPathResource("/out/production/resources/db/changelog/db.changelog-master.xml");
//            ClassPathResource changeLogFileResource = new ClassPathResource("db/changelog/db.changelog-master.xml");
            //String newFilePath = "C:\\Users\\kjspo\\IdeaProjects\\liquibase-test\\src\\main\\resources\\db\\changelog\\db.changelog-master.xml";
            String changeLogPath = changeLogFileResource.getPath();

            File changeLogFile = new File(changeLogPath);

            System.out.println(changeLogFile.exists());

            if(!changeLogFile.exists()){
                System.out.println("File을 새로 만들어야 합니다.");
                //ToDO : file이 있는지 없는지 create method에서 check 할 필요가 있나..?
            }

            Connection connection = dataSource.getConnection();
            JdbcConnection jdbcConnection = new JdbcConnection(connection);

            // Liquibase 설정
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
            ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml", resourceAccessor, database);

            addChangeSetToChangeLog(changeLogPath, dynamicTableDTO.getTableName(), dynamicTableDTO.getTableColumnNames());

            // 변경 작업 수행
            liquibase.update("");

        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException(e);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
        return dynamicTableDTO;
    }


    private void addChangeSetToChangeLog(String changeLogPath, String tableName, List<String> columnNames){

        try {
            File changeLogFile = new File(changeLogPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(changeLogFile);

            String changeSetIdValue = "changelog-dynamic" + UUID.randomUUID();

            System.out.println(changeSetIdValue);

            //테이블 생성에 필요한 정보를 기반으로 XML 파일에 changeSet 추가
            Element rootElement = doc.getDocumentElement();
            Element changeSetElement = doc.createElement("changeSet");
            changeSetElement.setAttribute("id", changeSetIdValue);
            changeSetElement.setAttribute("author", "tedkim");

            Element createTableElement = doc.createElement("createTable");
            createTableElement.setAttribute("tableName", tableName);

            Element seqColumnElement = doc.createElement("column");
            seqColumnElement.setAttribute("name", "seq");
            seqColumnElement.setAttribute("type", "BIGINT");
            seqColumnElement.setAttribute("autoIncrement", "true");

            Element seqConstraintsElement = doc.createElement("constraints");
            seqConstraintsElement.setAttribute("nullable", "false");
            seqConstraintsElement.setAttribute("primaryKey", "true");

            seqColumnElement.appendChild(seqConstraintsElement);
            createTableElement.appendChild(seqColumnElement);

            // 컬럼 유형, 제약 조건 등 추가 설정
            for(String columnName : columnNames) {
                Element columnElement = doc.createElement("column");
                columnElement.setAttribute("name", columnName);
                columnElement.setAttribute("type", "VARCHAR(255)");
                createTableElement.appendChild(columnElement);
            }

            changeSetElement.appendChild(createTableElement);
            rootElement.appendChild(changeSetElement);

            //수정된 XML 파일 저장
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(changeLogFile));

        } catch (Exception e){
            //예외처리
            e.printStackTrace();
        }
    }


    public DynamicTableDTO update(DynamicTableDTO dynamicTableDTO){

        return dynamicTableDTO;
    }

    public void updateTable(String tableName, List<String> columnsToAdd, List<String> columnsToDelete) {
        try {
            Connection connection = dataSource.getConnection();
            JdbcConnection jdbcConnection = new JdbcConnection(connection);

            // Liquibase 설정
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(getClass().getClassLoader()), database);

            // 변경 작업 수행을 위한 changeSet 생성
            ChangeSet changeSet = new ChangeSet(UUID.randomUUID().toString(), "tedkim", false, false, null, null, null, null);
            CreateTableChange createTableChange = new CreateTableChange();
            createTableChange.setTableName(tableName);

            // 컬럼 추가
            for (String columnToAdd : columnsToAdd) {
                ColumnConfig columnConfig = new ColumnConfig();
                columnConfig.setName(columnToAdd);
                columnConfig.setType("VARCHAR(255)");
                createTableChange.addColumn(columnConfig);
            }

            // 컬럼 삭제
            for (String columnToDelete : columnsToDelete) {
                DropColumnChange dropColumnChange = new DropColumnChange();
                dropColumnChange.setTableName(tableName);
                dropColumnChange.setColumnName(columnToDelete);
                changeSet.addChange(dropColumnChange);
            }

            changeSet.addChange(createTableChange);

            // 변경 작업 수행
            liquibase.update(new Contexts(), new LabelExpression());

            connection.close();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }


    private void createChangeLogFile(File changeLogFile) throws IOException {
        // 새로운 XML 파일 생성 로직. 예시로 기본적인 XML 내용을 작성하거나, 기존 XML 파일을 복사하여 사용할 수 있습니다.
        // 필요한 경우 XML 내용을 동적으로 생성하거나 수정하는 로직이 필요 할 수도 있을것 같다.

        // 새로운 XML 파일 생성
        if (changeLogFile.createNewFile()) {
            // XML 파일 생성 후 초기 내용 추가
            FileWriter writer = new FileWriter(changeLogFile);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
            writer.write("<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"\n");
            writer.write("                   xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\"\n");
            writer.write("                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            writer.write("                   xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog\n");
            writer.write("                   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd\n");
            writer.write("                   http://www.liquibase.org/xml/ns/dbchangelog-ext\n");
            writer.write("                   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd\">\n");
            writer.write("\n");
            writer.write("    <!-- Add your database changes here! -->\n");
            writer.write("\n");
            writer.write("</databaseChangeLog>\n");

            writer.close();
        }
    }



    public DynamicTableDTO delete(DynamicTableDTO dynamicTableDTO){

        return dynamicTableDTO;
    }



}
