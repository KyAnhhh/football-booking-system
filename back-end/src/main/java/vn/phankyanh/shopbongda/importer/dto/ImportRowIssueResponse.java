package vn.phankyanh.shopbongda.importer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImportRowIssueResponse {
    private int rowNumber;
    private String field;
    private String value;
    private String message;
}
