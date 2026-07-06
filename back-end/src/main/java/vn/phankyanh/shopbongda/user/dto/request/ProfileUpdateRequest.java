package vn.phankyanh.shopbongda.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {

    @Size(min = 3, max = 100)
    private String username;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String phone;
}
