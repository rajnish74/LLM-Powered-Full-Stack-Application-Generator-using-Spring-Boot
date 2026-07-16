package com.rajnish.service;

import com.rajnish.dto.file.FileContentResponse;
import com.rajnish.dto.file.FileNode;

import java.util.List;

public interface FIleService {
    List<FileNode> getFileTree(Long userId, Long projectId);

    FileContentResponse getFileContent(Long userId, Long projectId, String path);
}
