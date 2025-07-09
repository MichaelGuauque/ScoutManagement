package com.scoutmanagement.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UploadServiceTest {

    @InjectMocks
    private UploadFileService uploadFileService;

    @Mock
    private MultipartFile multipartFile;
    private final String folderPath = "images//";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
        uploadFileService = new UploadFileService(); // Instancia de la clase a probar
    }

    @Test
    void testSaveImage_whenFileIsNotEmpty() throws IOException {
        // Simulamos un archivo que no está vacío
        String fileName = "test-image.jpg";
        byte[] content = "dummy content".getBytes();

        // Configuración del mock del MultipartFile
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(content);
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);

        // Ejecutamos el método
        String result = uploadFileService.saveImage(multipartFile);

        // Verificamos que el resultado sea el nombre del archivo
        assertEquals(fileName, result);

        // Verificar que el archivo haya sido escrito en el directorio correcto (esto solo a nivel de prueba)
        Path path = Paths.get("images//" + fileName);
        assertTrue(Files.exists(path)); // Aseguramos que el archivo se haya creado
    }

    @Test
    void testSaveImage_whenFileIsEmpty() throws IOException {
        // Simulamos un archivo vacío
        when(multipartFile.isEmpty()).thenReturn(true);

        // Ejecutamos el método
        String result = uploadFileService.saveImage(multipartFile);

        // Verificamos que el nombre por defecto "miembro.svg" se haya retornado
        assertEquals("miembro.svg", result);
    }

    @Test
    void deleteImage_existingFile_fileIsDeleted() throws IOException {
        // Arrange: crear un archivo ficticio
        String fileName = "test-image-to-delete.jpg";
        File file = new File(folderPath + fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("contenido de prueba");
        }

        assertTrue(file.exists(), "El archivo debe existir antes de borrar");

        // Act
        uploadFileService.deleteImage(fileName);

        // Assert
        assertFalse(file.exists(), "El archivo debe haberse eliminado");
    }

}
