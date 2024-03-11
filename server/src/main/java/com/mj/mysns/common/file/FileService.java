package com.mj.mysns.common.file;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileRepository fileRepository;

    public Optional<String> saveFile(MultipartFile file) {
        String objectKey = fileRepository.saveFile(file);
        return Optional.of(objectKey);
    }
}
