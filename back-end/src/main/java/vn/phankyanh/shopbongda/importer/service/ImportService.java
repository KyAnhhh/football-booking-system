package vn.phankyanh.shopbongda.importer.service;

import org.springframework.web.multipart.MultipartFile;

import vn.phankyanh.shopbongda.importer.domain.ImportType;
import vn.phankyanh.shopbongda.importer.dto.ImportResultResponse;

public interface ImportService {
    String getTemplate(ImportType type);

    ImportResultResponse importCsv(ImportType type, MultipartFile file);
}
