package vn.phankyanh.shopbongda.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // field nào null thì nó sẽ k trả ra Json
public class ApiResponse<T> {
    private int code = 1000;
    private String message = "success";
    private T data;

}
