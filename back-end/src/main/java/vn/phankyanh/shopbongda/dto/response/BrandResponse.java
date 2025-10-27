package vn.phankyanh.shopbongda.dto.response;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BrandResponse {
    private Long id;

    private String name;

    private String description;

    private String logoUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
