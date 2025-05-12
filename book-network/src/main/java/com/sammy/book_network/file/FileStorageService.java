package com.sammy.book_network.file;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

// a service that handles file uploads(book cover)
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${file.uploads.photos-output-path}")
    // create a path to be used on different environments. (store on yaml file)
    private String fileUploadPath;

    public String saveFile(
            @NonNull MultipartFile sourceFile,
            @NonNull Integer userId) {

        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    public String uploadFile(
            @NotNull MultipartFile sourceFile,
            @NotNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);

        // check if target folder exist if not create it.
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create target folder:: {}", targetFolder.getAbsolutePath());
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = finalUploadPath + separator + currentTimeMillis() + "." + fileExtension;
        // example -> file path ./upload/users/1/2333322.jpg
        Path targetPath = Paths.get(targetFilePath);
        // handle IO Exception
        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("Successfully uploaded file:: {}", targetFilePath);

        } catch (Exception e) {
            log.error("Failed to upload file:: {}", targetFilePath, e);

        }
        return null;
    }

    // get file extension if jpg, ext , e.t.c
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        // something.jpg4
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return "";
        }
        return fileName.substring(lastIndex + 1).toLowerCase(Locale.ROOT);
    }
}