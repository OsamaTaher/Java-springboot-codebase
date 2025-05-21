package common.management.common.service.impl;

import common.management.common.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static common.management.common.util.DateTimeHelper.getCurrentDateTimeString;
import static common.management.common.util.OperationStatus.*;

@Service
@Slf4j
public class FileSystemStorageService implements StorageService {

    @Value("${files.store.root}")
    private String rootLocation;
    private Path rootPath;

    @Value("${image.thumb.width:100}")
    private int thumbWidth;

    private final Pattern fileNamePattern = Pattern.compile("^[a-zA-Z0-9.-]+$");

    private final HashSet<String> allowedFileType = new HashSet<>(List.of(
            "image/jpeg","image/jpg","image/png"
            ,"video/mp4"
            ,"application/pdf"
            ,"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ,"application/msword"
    ));

    private final HashSet<String> imageType = new HashSet<>(List.of("jpeg","jpg","png","gif"));
    private final HashSet<String> allowedFilesExt = new HashSet<>(List.of("pdf","mp4", "jpeg","jpg","png","gif"));

    @Override
    public boolean isImage(MultipartFile file){
        return isImage(FilenameUtils.getExtension(file.getOriginalFilename()));
    }

    private boolean isImage(String ext){
        return imageType.contains(ext);
    }

    public boolean isAllowedFileType(MultipartFile file) throws IOException {
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());
        return allowedFilesExt.contains(FilenameUtils.getExtension(file.getOriginalFilename()))
        && allowedFileType.contains(mimeType);
    }


    @PostConstruct
    public void init() {
        try {
            rootPath = Paths.get(rootLocation);
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public int store(MultipartFile file, String nameAndExt) {
        Path location = Paths.get(rootLocation+"/");

        try {
            Files.createDirectories(location);
            if (file.isEmpty()) {
                return OP_STATUS_FILE_EMPTY;
            }
            if (nameAndExt.contains("..") || !fileNamePattern.matcher(nameAndExt).matches()) {
                // This is a security check
               return OP_STATUS_INVALID_FILE_NAME;
            }

            if(!isAllowedFileType(file)) return OP_STATUS_FILE_TYPE_NOT_ALLOWED;

            try (InputStream inputStream = file.getInputStream()) {

                Files.copy(inputStream, location.resolve(nameAndExt),
                        StandardCopyOption.REPLACE_EXISTING);

                if(isImage(FilenameUtils.getExtension(file.getOriginalFilename()))){
                    createThumbnail(file,thumbWidth,nameAndExt);
                }
            }
            return OP_STATUS_SUCCESS;
        }
        catch (IOException e) {
            log.error("[EXCEPTION] store : {},{}",e.getMessage(),e.getCause());
            return OP_STATUS_FAILED;
        }
    }

    private void createThumbnail(MultipartFile file, Integer width,String nameAndExt) throws IOException{
        Path location = Paths.get(rootLocation+"/");
        BufferedImage thumbImg = null;
        BufferedImage img = ImageIO.read(file.getInputStream());
        thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, Scalr.OP_ANTIALIAS);
        ImageIO.write(thumbImg, FilenameUtils.getExtension(file.getOriginalFilename()) ,new File(rootLocation,"thumb_"+nameAndExt));
    }


    public Path load(String filename,Path location) {
        return location.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            if(!fileNamePattern.matcher(filename).matches()){
                log.error("[ERROR] loadAsResource: invalid file name");
                return null;
            }
            Path location = Paths.get(rootLocation+"/");
            Path file = load(filename,location);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
              return null;
            }
        } catch (MalformedURLException e) {
           log.error("[EXCEPTION] loadAsResource: {},{}",e.getMessage(),e.getCause());
           return null;
        }
    }

    @Override
    public String generateFullFileName(String prefix, MultipartFile file){
        return prefix +
                getCurrentDateTimeString() +
                "-" +
                UUID.randomUUID() +
                "." +
                FilenameUtils.getExtension(file.getOriginalFilename());
    }


}
