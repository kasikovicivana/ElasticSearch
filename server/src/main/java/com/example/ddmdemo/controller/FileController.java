package com.example.ddmdemo.controller;

import com.example.ddmdemo.service.interfaces.FileService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/file")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {

    private final FileService fileService;

    @GetMapping("/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        log.info("STATISTIC-LOG serveFile -> " + filename);

        var minioResponse = fileService.loadAsResource(filename);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                minioResponse.headers().get("Content-Disposition"))
            .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Path.of(filename)))
            .body(new InputStreamResource(minioResponse));
    }
}
