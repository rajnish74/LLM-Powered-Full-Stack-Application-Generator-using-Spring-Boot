package com.rajnish.service.impl;

import com.rajnish.dto.file.FileContentResponse;
import com.rajnish.dto.file.FileNode;
import com.rajnish.service.FIleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FIleService {
    @Override
    public List<FileNode> getFileTree(Long userId, Long projectId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long userId, Long projectId, String path) {
        return null;
    }
}
