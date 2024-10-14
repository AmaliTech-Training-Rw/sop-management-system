package com.team.upload_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

@Service
public class FileUploadService {

    private final Cloudinary cloudinary;

    @Autowired
    public FileUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    public String uploadFile(MultipartFile file) {
        validateFileType(file);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    private void validateFileType(MultipartFile file) {
        String fileType = file.getContentType();
        if (!ALLOWED_FILE_TYPES.contains(fileType)) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, and PDF files are allowed.");
        }
    }
}

