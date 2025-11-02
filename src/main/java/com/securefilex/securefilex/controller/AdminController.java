package com.securefilex.securefilex.controller;

import com.securefilex.securefilex.model.FileRecord;
import com.securefilex.securefilex.repository.FileRepository;
import com.securefilex.securefilex.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;

    // ✅ Admin dashboard: show all uploaded files
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        List<FileRecord> files = (List<FileRecord>) fileRepository.findAll();
        model.addAttribute("files", files);
        model.addAttribute("username", authentication.getName());
        return "admin/dashboard";
    }

    // ✅ Admin can download any file (without restriction)
    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable("id") Long id) {
        try {
            byte[] data = fileService.downloadDecryptedFile(id, "admin", true);

            FileRecord record = fileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            ByteArrayResource resource = new ByteArrayResource(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename(record.getFilename())
                            .build()
            );
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(data.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
