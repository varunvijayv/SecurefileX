package com.securefilex.securefilex.service;

import com.securefilex.securefilex.model.FileRecord;
import com.securefilex.securefilex.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final Path uploadDir = Paths.get("uploads");

    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int IV_LENGTH = 12; // bytes
    private final SecretKeySpec aesKeySpec;
    private final SecureRandom secureRandom = new SecureRandom();

    public FileService(FileRepository fileRepository, @Value("${app.file.encryption.key}") String base64Key) {
        this.fileRepository = fileRepository;
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        this.aesKeySpec = new SecretKeySpec(keyBytes, "AES");
    }

    // For regular users: encrypt and save
    public FileRecord uploadEncryptedFile(MultipartFile multipartFile, String owner) throws Exception {
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

        String originalFilename = multipartFile.getOriginalFilename();
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFilename + ".enc";
        Path filePath = uploadDir.resolve(uniqueFileName);

        // generate IV
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        // encrypt
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKeySpec, gcmSpec);
        byte[] cipherText = cipher.doFinal(multipartFile.getBytes());

        Files.write(filePath, cipherText, StandardOpenOption.CREATE_NEW);

        FileRecord record = new FileRecord();
        record.setFilename(originalFilename);
        record.setFilepath(filePath.toString());
        record.setOwner(owner);
        record.setUploadTime(LocalDateTime.now());
        record.setIv(Base64.getEncoder().encodeToString(iv));
        record.setEncrypted(true);
        fileRepository.save(record);
        return record;
    }

    // For admins: store plain (unencrypted) file
    public FileRecord uploadPlainFile(MultipartFile multipartFile, String owner) throws IOException {
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

        String originalFilename = multipartFile.getOriginalFilename();
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = uploadDir.resolve(uniqueFileName);

        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        FileRecord record = new FileRecord();
        record.setFilename(originalFilename);
        record.setFilepath(filePath.toString());
        record.setOwner(owner);
        record.setUploadTime(LocalDateTime.now());
        record.setEncrypted(false);
        record.setIv(null);
        fileRepository.save(record);
        return record;
    }

    // Decrypt and return file bytes. If adminAllowed==true, admin may download any file.
    public byte[] downloadDecryptedFile(Long fileId, String requesterUsername, boolean adminAllowed) throws Exception {
        Optional<FileRecord> opt = fileRepository.findById(fileId);
        if (!opt.isPresent()) throw new RuntimeException("File not found");
        FileRecord record = opt.get();

        // permission check
        if (!adminAllowed && !record.getOwner().equals(requesterUsername)) {
            throw new RuntimeException("Access denied");
        }

        Path path = Paths.get(record.getFilepath());
        byte[] fileBytes = Files.readAllBytes(path);

        if (!record.isEncrypted()) {
            return fileBytes; // plain file
        }

        byte[] iv = Base64.getDecoder().decode(record.getIv());
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, aesKeySpec, gcmSpec);
        return cipher.doFinal(fileBytes);
    }
}
