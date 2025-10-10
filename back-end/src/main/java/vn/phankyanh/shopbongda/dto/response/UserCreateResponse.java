package vn.phankyanh.shopbongda.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserCreateResponse {
    private Long id;
    private String username;
    private String email;
    private String message;
}
