package com.example.liquibasetest.service;

import com.example.liquibasetest.dto.DynamicTableDTO;
import com.example.liquibasetest.repository.DynamicTableRepository;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
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
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DynamicTableService {

    private final DataSource dataSource;
    private final DynamicTableRepository dynamicTableRepository;

    public DynamicTableService(DataSource dataSource, DynamicTableRepository dynamicTableRepository) {
        this.dataSource = dataSource;
        this.dynamicTableRepository = dynamicTableRepository;
    }

    public DynamicTableDTO create(DynamicTableDTO dynamicTableDTO) {
        log.debug("dynamicTableDTO", dynamicTableDTO);

        try {
            // Liquibase XML 파일 경로
            ClassPathResource changeLogFileResource = new ClassPathResource("db/changelog/db.changelog-master.xml");
            InputStream changeLogInputStream = changeLogFileResource.getInputStream();
            String changeLogPath = changeLogFileResource.getFile().getAbsolutePath();

            Connection connection = dataSource.getConnection();
            JdbcConnection jdbcConnection = new JdbcConnection(connection);

            // Liquibase 설정
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
            ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            Liquibase liquibase = new Liquibase(changeLogPath, resourceAccessor, database);

            addChangeSetToChangeLog(changeLogPath, dynamicTableDTO.getTableName(), dynamicTableDTO.getTableColumnNames());

            // 변경 작업 수행
            liquibase.update("");

        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException(e);
        } catch (LiquibaseException | IOException e) {
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

            LocalDateTime now = LocalDateTime.now();

            String changeSetIdValue = "changelog-dynamic" + now;

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

}
