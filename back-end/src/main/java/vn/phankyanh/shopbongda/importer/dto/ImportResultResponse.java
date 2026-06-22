package vn.phankyanh.shopbongda.importer.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImportResultResponse {
    private String type;
    private int totalRows;
    private int successRows;
    private int failedRows;
    private int warningRows;

    @Builder.Default
    private List<Long> createdIds = new ArrayList<>();

    @Builder.Default
    private List<ImportRowIssueResponse> errors = new ArrayList<>();

    @Builder.Default
    private List<ImportRowIssueResponse> warnings = new ArrayList<>();
}
