package vn.phankyanh.shopbongda.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class CategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    private String description;
}
