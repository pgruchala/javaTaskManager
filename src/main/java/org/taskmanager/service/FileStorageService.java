package org.taskmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;
    private final List<String> allowedExtensions;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir,
                              @Value("${app.upload.allowed-extensions}") String allowedExtensions){
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.allowedExtensions = Arrays.asList(allowedExtensions.split(","));

        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException ex){
            throw new RuntimeException("Nie można utworzyć katalogu dla plików", ex);
        }
    }

    public String storeFile(MultipartFile file, String taskTitle){
        if (file.isEmpty()){
            throw new IllegalArgumentException("Nie można zapisać pustego pliku");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename==null){
            throw new IllegalArgumentException("Nie można zapisać pliku bez nazwy");
        }
        String extension = getFileExtension(originalFilename);
        if (!allowedExtensions.contains(extension.toLowerCase())){
            throw new IllegalArgumentException("Niedozwolone rozszerzenie pliku. Dozwolone: "+allowedExtensions);
        }

        String filename = generateUniqueFilename(taskTitle, extension);
        Path targetLocation = this.uploadPath.resolve(filename);

        try {
            Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e){
            throw new RuntimeException("Błąd podczas zapisu",e);
        }


    }
    public Path loadFile(String filename) {
        return uploadPath.resolve(filename).normalize();
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = uploadPath.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Błąd podczas usuwania pliku", ex);
        }
    }
    private String generateUniqueFilename(String taskTitle, String extension) {
        String cleanTitle = taskTitle.replaceAll("[^a-zA-Z0-9]", "_");
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return cleanTitle + "_" + uniqueId + "." + extension;
    }
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot + 1);
    }

}
