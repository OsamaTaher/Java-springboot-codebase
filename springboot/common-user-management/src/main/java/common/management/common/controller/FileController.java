package common.management.common.controller;

import common.management.common.service.StorageService;
import common.management.common.util.OperationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final StorageService storageService;
    private final OperationStatus operationStatus;

    @GetMapping("/{fileName}")
    @ResponseBody
    public ResponseEntity<?> getFileByName(@PathVariable(name = "fileName") String name){
        var resource = storageService.loadAsResource(name);
        if(resource==null) return operationStatus.handle(OperationStatus.OP_STATUS_FAILED);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .body(resource);
    }
}
