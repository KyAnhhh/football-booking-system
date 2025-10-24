package vn.phankyanh.shopbongda.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // dùng cho loại exception k có
    UNCATEGORIED_EXCEPTION( 999,"Don't have category exception"),
    USER_EXISTED( 1001,"UserName already existed"),
    EMAIL_EXISTED( 1002,"Email already existed"),
    USERNAME_INVALID( 1003,"UserName invalid"),
    EMAIL_INVALID( 1004,"Email không hợp lệ, vui lòng nhập đúng định dạng (ví dụ: ten@gmail.com)"),
    PASSWORD_INVALID( 1005,"Mật khẩu phải có ít nhất 6 ký tự"),
    PHONE_INVALID( 1006,"Số điện thoại không hợp lệ, vui lòng nhập đúng định dạng (VD: 0912345678)"),
    INVALID_KEY( 1007,"Invalid message key"),
    ID_INVALID(1008, "Id not found"),
    USERNAME_EXISTED(1009, "UserName existed"),
    EMAIL_NOT_EXISTED(1010, "Email not exists")
    ;
    private final int code;
    private final String message;

}
