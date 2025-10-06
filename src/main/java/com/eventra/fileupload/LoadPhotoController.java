package com.eventra.fileupload;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/load-photo")
public class LoadPhotoController {

    private final LocalFileUploadService localFileUploadService;

    public LoadPhotoController(LocalFileUploadService localFileUploadService) {
        this.localFileUploadService = localFileUploadService;
    }

    /**
     * 取得圖片檔案。
     * 範例請求：
     * GET /load-photo/photo_portrait/abc123.jpg
     */
    @GetMapping("/{category}/{filename}")
    public ResponseEntity<Resource> getPhoto(
            @PathVariable String category,    // 例如 profile_pic, photo_landscape, photo_portrait
            @PathVariable String filename) {  // 例如 12345.jpg

        // 組出相對路徑
        String relativePath = Paths.get(category, filename).toString();

        // 讀取實體檔案
        Resource file = localFileUploadService.load(relativePath);

        // 根據副檔名決定 MIME Type
        MediaType mediaType = detectMediaType(filename);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(file);
    }

    /** 根據檔名副檔名決定 MIME Type */
    private MediaType detectMediaType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        return MediaType.APPLICATION_OCTET_STREAM; // fallback
    }
}
