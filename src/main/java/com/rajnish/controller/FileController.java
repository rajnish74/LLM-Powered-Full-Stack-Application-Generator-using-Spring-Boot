package com.rajnish.controller;

import com.rajnish.dto.file.FileContentResponse;
import com.rajnish.dto.file.FileNode;
import com.rajnish.service.FIleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/files")
public class FileController {

    private final FIleService  fileService;

    @GetMapping
    public ResponseEntity<List<FileNode>> getFileTree(@PathVariable Long projectId) {
        Long userId=1L;
        return ResponseEntity.ok(fileService.getFileTree(userId, projectId));
    }

    @GetMapping("/{*path}")
    public ResponseEntity<FileContentResponse> getFileContent(@PathVariable Long projectId,
                                                              @PathVariable String path) {
        Long userId=1L;
        return ResponseEntity.ok(fileService.getFileContent(userId, projectId, path));
    }
}
