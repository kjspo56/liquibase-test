package com.example.liquibasetest;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import liquibase.Liquibase;

import org.springframework.jdbc.core.JdbcTemplate;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
class LiquibaseTestApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testDynamicTableCreation() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            JdbcConnection jdbcConnection = new JdbcConnection(connection);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);

            Liquibase liquibase = new Liquibase("/db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        }

        // 테스트 후에 추가로 동적으로 테이블을 생성하거나 삭제 가능
        try (Connection connection = dataSource.getConnection()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            // 예시: 동적으로 테이블 생성
            jdbcTemplate.execute("CREATE TABLE dynamic_table (id BIGINT, name VARCHAR(255))");

            // 예시: 동적으로 테이블 삭제
            jdbcTemplate.execute("DROP TABLE dynamic_table");
        }

        /*try{
            Database database = // 데이터베이스 연결 설정
                    Liquibase liquibase = new Liquibase(null, new JdbcConnection(database.getConnection()));
            liquibase.createTable("dynamic_table")
                    .addColumn("id", "BIGINT", null, new ColumnConstraint[]{new PrimaryKeyConstraint()})
                    .addColumn("name", "VARCHAR(255)", null, new ColumnConstraint[]{new NotNullConstraint()});
            liquibase.update("");
        }*/

    }

    @Test
    void createTableUsingLiquibase(String tableName, List<String> columnNames) {
        try {
            // Liquibase XML 파일 경로
            String changeLogPath = "/db/changelog/db.changelog-master.xml";
            Connection connection = dataSource.getConnection();
            JdbcConnection jdbcConnection = new JdbcConnection(connection);

            // Liquibase 설정
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);;// 데이터베이스 연결 설정
            Liquibase liquibase = new Liquibase(changeLogPath, new FileSystemResourceAccessor(), database);

            // 테이블 생성에 필요한 정보를 기반으로 XML 파일 수정
            addChangeSetToChangeLog(changeLogPath, tableName, columnNames);

            // 변경 작업 수행
            liquibase.update("");
        } catch (LiquibaseException e) {
            // 예외 처리
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addChangeSetToChangeLog(String changeLogPath, String tableName, List<String> columnNames) {
        try {

            changeLogPath = "src/main/resources/db/changelog/test.xml";

            File changeLogFile = new File(changeLogPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(changeLogFile);

            // 테이블 생성에 필요한 정보를 기반으로 XML 파일에 changeSet 추가
            Element rootElement = doc.getDocumentElement();
            Element changeSetElement = doc.createElement("changeSet");
            changeSetElement.setAttribute("id", "changelog-dynamic");
            changeSetElement.setAttribute("author", "dynamic");

            Element createTableElement = doc.createElement("createTable");
            createTableElement.setAttribute("tableName", tableName);

            for (String columnName : columnNames) {
                Element columnElement = doc.createElement("column");
                columnElement.setAttribute("name", columnName);
                // 컬럼 유형, 제약 조건 등 추가 설정 가능
                // ...

                createTableElement.appendChild(columnElement);
            }

            changeSetElement.appendChild(createTableElement);
            rootElement.appendChild(changeSetElement);

            // 수정된 XML 파일 저장
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(changeLogFile));
        } catch (Exception e) {
            // 예외 처리
        }
    }

}
