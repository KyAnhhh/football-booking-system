package vn.phankyanh.shopbongda.catalog.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import vn.phankyanh.shopbongda.catalog.domain.PublicationStatus;

@Data
@Builder
public class PublicationCheckResponse {
    private Long id;
    private String catalogType;
    private PublicationStatus currentStatus;
    private boolean publishable;
    private List<String> reasons;
}
