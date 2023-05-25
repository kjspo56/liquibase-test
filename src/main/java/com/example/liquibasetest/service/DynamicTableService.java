package com.example.liquibasetest.service;

import com.example.liquibasetest.dto.DynamicTableDTO;
import com.example.liquibasetest.repository.DynamicTableRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Collections;
import java.util.List;

@Service
public class DynamicTableService {

    private final DynamicTableRepository dynamicTableRepository;

    public DynamicTableService(DynamicTableRepository dynamicTableRepository) {
        this.dynamicTableRepository = dynamicTableRepository;
    }

    public DynamicTableDTO create(DynamicTableDTO dynamicTableDTO) {
        System.out.println("Hello Dynamic!");
        addChangeSetToChangeLog(dynamicTableDTO.getTableName(), dynamicTableDTO.getTableColumnNames());
        return dynamicTableDTO;
    }

    private void addChangeSetToChangeLog(String tableName, List<String> columnNames){

        try {
            String changeLogPath = "src/main/resources/db/changelog/db.changelog-master.xml";  //xml 파일 생성 될 경로

            File changeLogFile = new File(changeLogPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(changeLogFile);

            //테이블 생성에 필요한 정보를 기반으로 XML 파일에 changeSet 추가
            Element rootElement = doc.getDocumentElement();
            Element changeSetElement = doc.createElement("changeSet");
            changeSetElement.setAttribute("id", "changelog-dynamic");
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
