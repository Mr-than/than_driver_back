package com.than;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FileRecord implements ApplicationListener<ContextClosedEvent> {



    private Map<String, String> records = new HashMap<>();
    private Map<String, String> deletedRecord = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DATA_FILE = "file_records.json";
    private static final String DELETED_FILE = "file_deleted.json";

    public void addFile(String fileName,String md5) {
        records.put(fileName, md5);
    }

    public void deleteFile(String fileName) {
        records.remove(fileName);
    }

    private void writeFile(File file, Map<String, String> map) {
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                Map<String, String> loadedData = objectMapper.readValue(
                        reader,
                        new TypeReference<>() {}
                );
                map.putAll(loadedData);
                System.out.println("成功从文件加载数据，共" + loadedData.size() + "条记录");
            } catch (IOException e) {
                System.err.println("加载数据失败：" + e.getMessage());
            }
        } else {
            System.out.println("数据文件不存在或为空，使用空Map初始化");
        }
    }



    @PostConstruct
    public void loadDataOnStartup() {
        File file = new File(DATA_FILE);
        writeFile(file,records);
        file = new File(DELETED_FILE);
        writeFile(file,deletedRecord);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            String jsonRecord = objectMapper.writeValueAsString(records);
            String jsonDelete = objectMapper.writeValueAsString(deletedRecord);

            try (FileWriter writer = new FileWriter(DATA_FILE)) {
                writer.write(jsonRecord);
            }

            try (FileWriter writer = new FileWriter(DELETED_FILE)) {
                writer.write(jsonDelete);
            }

            System.out.println("Map数据已以JSON格式持久化");
        } catch (JsonProcessingException e) {
            System.err.println("JSON序列化失败：" + e.getMessage());
        } catch (IOException e) {
            System.err.println("文件写入失败：" + e.getMessage());
        }
    }

    public Map<String, String> syncFileMap(Map<String, String> map){
        for (String s : map.keySet()) {
            records.remove(s);
            deletedRecord.put(s,map.get(s));
        }
        return records;
    }

    public boolean isNeedUpload(String fileName,String md5) {
        if (deletedRecord.containsKey(fileName)) {
            return !deletedRecord.get(fileName).equals(md5);
        }
        return true;
    }

    public void removeFromDelete(String fileName) {
        deletedRecord.remove(fileName);
    }

    public Map<String, String> getRecords() {
        return records;
    }

    public Map<String, String> getDeletedRecord() {
        return deletedRecord;
    }
}
