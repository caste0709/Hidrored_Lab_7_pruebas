package com.hidrored.aplicacion.reportes;

import com.hidrored.aplicacion.reportes.excepciones.StorageException;
import com.hidrored.dominio.reportes.modelo.ImagenAdjunta;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

@Service
public class FileStorageService {

  private static final String UPLOAD_DIR = "uploads/";

  public FileStorageService() {
    File dir = new File(UPLOAD_DIR);
    if (!dir.exists() && !dir.mkdirs()) {
      throw new StorageException("No se pudo inicializar el directorio.");
    }
  }

  public ImagenAdjunta store(MultipartFile file) {
    if (file.isEmpty()) {
      throw new StorageException("Archivo vacío.");
    }

    try {
      // ❌ ISSUE: dato controlado por el usuario
      String originalFilename = file.getOriginalFilename();

      if (originalFilename == null) {
        throw new StorageException("Nombre de archivo inválido.");
      }

      // ❌ ISSUE CLARO PARA SONARQUBE (Path Traversal)
      File destinationFile = new File(UPLOAD_DIR + originalFilename);

      Files.copy(file.getInputStream(), destinationFile.toPath());

      return new ImagenAdjunta(
          originalFilename,
          originalFilename,
          file.getContentType(),
          file.getSize(),
          LocalDateTime.now()
      );

    } catch (IOException e) {
      throw new StorageException("Error al guardar archivo.", e);
    }
  }
}
