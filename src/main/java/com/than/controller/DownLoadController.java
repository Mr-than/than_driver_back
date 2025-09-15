package com.than.controller;

import com.than.FileRecord;
import com.than.UrlSafeBase64Encoder;
import com.than.service.DownLoadService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
public class DownLoadController {

    private final DownLoadService downloadService;

    public DownLoadController(DownLoadService downloadService) {
        this.downloadService = downloadService;
    }

    @PostMapping("/download")
    public void downloadLocal(@RequestParam("path") String path, @RequestParam("file_name") String fileName, @RequestParam(value = "offset", required = false) Long offset, HttpServletResponse response) throws IOException {
        path = downloadService.tranPathName(path);
        //使用token鉴权

        File file = new File(DownLoadService.home + "/" + path + "/" + fileName);
        if (!file.exists()) {
            response.setStatus(300);
            return;
        }
        try (InputStream inputStream = new FileInputStream(file); ServletOutputStream outputStream = response.getOutputStream()) {
            response.reset();
            response.setContentType("application/octet-stream");
            String filename = file.getName();
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
            if (offset != null && offset > 0) {
                inputStream.skip(offset);
            }
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public void downloadFolder(){

    }

    @PostMapping("/delete_file")
    @ResponseBody
    public void deleteFile(String path){
        downloadService.deleteFile(path);
    }

    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("path") String path, @RequestParam("md5") String md5,@RequestParam("num") String num) {
        return downloadService.upload(file, md5, path, Integer.parseInt(num));
    }

    @ResponseBody
    @GetMapping("/get_map")
    public Map<String, String> getFileMap() {
        return downloadService.getFileMap();
    }

    @ResponseBody
    @PostMapping("/sync_file")
    public Map<String, String> syncFileMap(@RequestBody Map<String, String> map) {
        return downloadService.syncFileMap(map);
    }

    @ResponseBody
    @GetMapping("/get_delete")
    public Map<String, String> getDeleteMap() {
        return downloadService.getDeleteMap();
    }

    @ResponseBody
    @PostMapping("/get_file_versions")
    public List<String> searchFile(@RequestParam("path") String path) {
        return downloadService.searchFile(path);
    }

    @ResponseBody
    @GetMapping("/heart")
    public String heart() {
        return "ok";
    }
}
