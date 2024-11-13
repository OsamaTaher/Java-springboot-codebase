package common.management.common.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    boolean isImage(MultipartFile file);

    int store(MultipartFile file, String nameAndExt);

    Resource loadAsResource(String filename);

    String generateFullFileName(String prefix, MultipartFile file);
}
