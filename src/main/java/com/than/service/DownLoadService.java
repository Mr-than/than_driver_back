package com.than.service;

import com.than.FileRecord;
import com.than.UrlSafeBase64Encoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class DownLoadService {

    private final FileRecord record;
    public static final String home = System.getProperty("user.dir");

    public DownLoadService(FileRecord record) {
        this.record = record;
    }

    public String upload(MultipartFile file, String md5, String path, int num) {


        String originalFilename = file.getOriginalFilename();
        String originalPath = path;

        if (!record.isNeedUpload(originalPath, md5)) {
            record.addFile(originalPath, md5);
            record.removeFromDelete(originalPath);
            return "{\"msg\":\"true\"}";
        }
        path = tranPathName(path);
        File uploadParentFile = new File(home + "/" + path);

        if (!(uploadParentFile.exists() && uploadParentFile.isDirectory())) {
            uploadParentFile.mkdirs();
        }
        String name = "";
        if (originalFilename != null) {
            if (originalFilename.contains(".")) {
                name = originalFilename.substring(0, originalFilename.lastIndexOf(".")) + System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                name = originalFilename + System.currentTimeMillis();
            }
        }
        File uploadFile = new File(home + "/" + path + "/" + name);
        try {
            file.transferTo(uploadFile);
        } catch (IOException e) {
            deleteFailFail(uploadFile);
        }
        record.addFile(originalPath, md5);
        record.removeFromDelete(originalPath);
        File[] files = uploadFile.getParentFile().listFiles();
        if (files.length > num) {
            Optional<File> min = Arrays.stream(files).min(Comparator.comparingLong(File::lastModified));
            min.get().delete();
        }
        return "{\"msg\":\"true\"}";
    }


    public Map<String, String> getFileMap() {
        return record.getRecords();
    }

    public Map<String, String> syncFileMap(Map<String, String> map) {
        return record.syncFileMap(map);
    }

    public Map<String, String> getDeleteMap() {
        return record.getDeletedRecord();
    }

    public List<String> searchFile(String path) {
        File file = new File(home + "/" + tranPathName(path));
        List<String> list = new ArrayList<>();
        if (!file.exists() || !file.isDirectory()) {
            return list;
        }
        File[] files = file.listFiles();
        for (File f : files) {
            String name = f.getName();
            if (!f.isDirectory()) {
                list.add(name);
            }
        }
        return list;
    }

    /**
     * 这个函数是为了上传中发生意外情况，上传失败后的善后
     */
    private void deleteFailFail(File path) {
        if (path == null) {
            return;
        }
        if (path.exists()) {
            if (path.isDirectory()) {
                if (path.listFiles().length == 0) {
                    path.delete();
                }
            } else {
                path.delete();
            }
        }
        deleteFailFail(path.getParentFile());
    }

    /**
     * 这是为了将文件名和路径转为url安全的base64编码，为了确保服务器可以存储这个名称
     *
     * @param path 路径名
     * @return 转换后的路径
     */
    public String tranPathName(String path) {
        String[] s = path.trim().split("[\\\\/]");
        StringBuilder tranPath = new StringBuilder();
        for (String string : s) {
            tranPath.append(UrlSafeBase64Encoder.encode(string));
            tranPath.append("/");
        }
        return tranPath.toString();
    }


    public String deleteFile(String path) {
        String p=tranPathName(path);
        if (p == null) {
            return "未找到文件";
        }
        File file = new File(p);
        if (file.exists()) {
            if (file.isDirectory()) {
                try {
                    if (!deleteDir(file)) {
                        return "未知错误,查看服务器日志";
                    }
                    return "完成";
                }catch (Exception e) {
                    return e.getMessage();
                }
            }else {
                return "路径错误,查看服务器日志";
            }
        }
        return "未知错误,查看服务器日志";
    }


    public boolean deleteDir(File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                return false;
            }
        }

        for (File file : files) {
            file.delete();
        }
        dir.delete();
        return true;
    }

}
