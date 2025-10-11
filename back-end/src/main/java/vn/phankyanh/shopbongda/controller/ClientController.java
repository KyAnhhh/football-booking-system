package vn.phankyanh.shopbongda.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public String profile() {
        return "Xin chào CLIENT 👤 — đây là trang cá nhân của bạn!";
    }

    @GetMapping("/cart")
    @PreAuthorize("hasRole('CLIENT')")
    public String cart() {
        return "CLIENT đang xem giỏ hàng 🛒";
    }
}
