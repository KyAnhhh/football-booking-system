package vn.phankyanh.shopbongda.importer.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.importer.domain.ImportType;
import vn.phankyanh.shopbongda.importer.dto.ImportResultResponse;
import vn.phankyanh.shopbongda.importer.service.ImportService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/imports")
@RequiredArgsConstructor
public class AdminImportController {

    private final ImportService importService;

    @GetMapping(value = "/templates/{type}", produces = "text/csv")
    public ResponseEntity<String> downloadTemplate(@PathVariable String type) {
        ImportType importType = ImportType.fromPath(type);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + importType.getPath() + "-template.csv\""
                )
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(importService.getTemplate(importType));
    }

    @PostMapping(value = "/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImportResultResponse> importCsv(
            @PathVariable String type,
            @RequestParam("file") MultipartFile file
    ) {
        ImportResultResponse result = importService.importCsv(ImportType.fromPath(type), file);
        return new ApiResponse<>(1000, "Import dữ liệu hoàn tất", result);
    }
}
