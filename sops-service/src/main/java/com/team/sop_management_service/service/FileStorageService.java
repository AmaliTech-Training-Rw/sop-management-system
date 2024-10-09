package com.team.sop_management_service.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // Directory where files will be stored (can be configured through properties)
    private final Path fileStorageLocation = Paths.get("file-storage").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create directory where files will be stored.", ex);
        }
    }

    /**
     * Store a file in the file system.
     *
     * @param file The file to be stored
     * @return The name of the stored file
     * @throws IOException If there is any issue during file storage
     */
    public String storeFile(MultipartFile file) throws IOException {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Check for invalid characters
        if (originalFileName.contains("..")) {
            throw new IOException("Filename contains invalid path sequence: " + originalFileName);
        }

        // Generate a unique file name
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // Resolve the file storage path
        Path targetLocation = this.fileStorageLocation.resolve(fileName);

        // Copy file to the target location (overwrites existing file with the same name)
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    /**
     * Load a file as a Path.
     *
     * @param fileName The name of the file
     * @return The file path
     */
    public Path loadFileAsPath(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }
}
