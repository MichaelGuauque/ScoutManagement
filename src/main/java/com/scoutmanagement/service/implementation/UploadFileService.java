package com.scoutmanagement.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadFileService {

    private static final Logger logger = LoggerFactory.getLogger(UploadFileService.class);
    private String folder="images//";

    public String saveImage(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            byte [] bytes=file.getBytes();
            Path path = Paths.get(folder+file.getOriginalFilename());
            Files.write(path, bytes);
            return file.getOriginalFilename();
        }
        return "miembro.svg";
    }

    public void deleteImage(String nombre) {
        String ruta="images//";
        Path path = Paths.get(ruta + nombre);
        try{
            Files.delete(path);
        }catch(IOException e){
            logger.error(e.getMessage());
        }
    }
}
