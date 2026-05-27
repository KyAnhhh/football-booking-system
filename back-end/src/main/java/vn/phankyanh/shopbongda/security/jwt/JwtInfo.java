package vn.phankyanh.shopbongda.security.jwt;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtInfo {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;

    private String jwtId;
    private Date issueTime;
    private Date expiredTime;

}
