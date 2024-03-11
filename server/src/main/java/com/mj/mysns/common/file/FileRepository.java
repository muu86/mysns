package com.mj.mysns.common.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileRepository {

    String saveFile(MultipartFile file);
    
}