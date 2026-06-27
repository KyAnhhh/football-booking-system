package vn.phankyanh.shopbongda.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierRequest {

    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String name;

    private String phone;

    private String email;

    private String address;

    private Boolean active;
}
