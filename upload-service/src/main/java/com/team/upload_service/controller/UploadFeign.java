package com.team.upload_service.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

@FeignClient(name = "upload-service", url = "http://localhost:8090")
public interface UploadFeign {

    @PostMapping("/api/upload")
    ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file);
}
