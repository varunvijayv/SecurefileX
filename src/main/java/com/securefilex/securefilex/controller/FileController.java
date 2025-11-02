package com.securefilex.securefilex.controller;

import com.securefilex.securefilex.model.FileRecord;
import com.securefilex.securefilex.repository.FileRepository;
import com.securefilex.securefilex.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepo;

    @GetMapping("/upload")
    public String showUploadPage() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file, Authentication authentication, Model model) {
        try {
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                fileService.uploadPlainFile(file, username);
            } else {
                fileService.uploadEncryptedFile(file, username);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Upload failed: " + e.getMessage());
            return "upload";
        }
        return "redirect:/udashboard";
    }

    // stream decrypted bytes to client with filename
    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable("id") Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            // adminAllowed = isAdmin
            byte[] data = fileService.downloadDecryptedFile(id, username, isAdmin);

            FileRecord record = fileRepo.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
            ByteArrayResource resource = new ByteArrayResource(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().filename(record.getFilename()).build());
            headers.setContentLength(data.length);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
