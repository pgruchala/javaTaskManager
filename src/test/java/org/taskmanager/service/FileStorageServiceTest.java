package org.taskmanager.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {
    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp(){
        fileStorageService = new FileStorageService(tempDir.toString(), "jpg,pdf,txt");
    }
    @Test
    @DisplayName("Zapis pliku - poprawne rozszerzenie")
    void testShouldStoreFileSuccessfully() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        String taskTitle = "My Task";

        String storedFilename = fileStorageService.storeFile(file, taskTitle);

        assertAll("Zapis pliku",
                () -> assertNotNull(storedFilename),
                () -> assertTrue(storedFilename.endsWith(".txt")),
                () -> assertTrue(storedFilename.startsWith("My_Task_")),
                () -> assertTrue(Files.exists(tempDir.resolve(storedFilename)))
        );
    }

    @Test
    @DisplayName("Zapis pliku - niedozwolone rozszerzenie")
    void testShouldThrowExceptionForInvalidExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "malware.exe", "application/octet-stream", "nuh-uh".getBytes());

        assertThrows(IllegalArgumentException.class, () ->
                fileStorageService.storeFile(file, "Task")
        );
    }

    @Test
    @DisplayName("Usuwanie pliku")
    void testShouldDeleteFile() throws IOException {
        Path file = tempDir.resolve("todelete.txt");
        Files.createFile(file);

        fileStorageService.deleteFile("todelete.txt");

        assertFalse(Files.exists(file));
    }

    @Test
    @DisplayName("Ładowanie ścieżki pliku")
    void testShouldLoadFile() {
        Path path = fileStorageService.loadFile("somefile.txt");
        assertEquals(tempDir.resolve("somefile.txt").toAbsolutePath(), path.toAbsolutePath());
    }
}
