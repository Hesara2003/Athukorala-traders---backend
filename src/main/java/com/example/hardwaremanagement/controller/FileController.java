package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/products/{filename:.+}")
    public ResponseEntity<Resource> getProductImage(@PathVariable("filename") String filename) {
        try {
            Path filePath = fileStorageService.getFilePath(filename);
            
            // If file doesn't exist, try to serve placeholder image
            if (!Files.exists(filePath)) {
                Path placeholderPath = fileStorageService.getFilePath("placeholder.svg");
                if (Files.exists(placeholderPath)) {
                    filePath = placeholderPath;
                    filename = "placeholder.svg";
                } else {
                    return ResponseEntity.notFound().build();
                }
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null || MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(contentType)) {
                // Fallback by file extension
                String lower = filename.toLowerCase();
                if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                    contentType = MediaType.IMAGE_JPEG_VALUE;
                } else if (lower.endsWith(".png")) {
                    contentType = MediaType.IMAGE_PNG_VALUE;
                } else if (lower.endsWith(".gif")) {
                    contentType = MediaType.IMAGE_GIF_VALUE;
                } else if (lower.endsWith(".webp")) {
                    contentType = "image/webp";
                } else if (lower.endsWith(".svg")) {
                    contentType = "image/svg+xml";
                } else {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
