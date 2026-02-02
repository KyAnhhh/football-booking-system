package vn.phankyanh.shopbongda.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.service.OrderService;
import vn.phankyanh.shopbongda.service.QrCodeService;

@RestController
@RequiredArgsConstructor
public class QrCodeController {
    private final QrCodeService qrCodeService;
    private final OrderService orderService;
    
    @GetMapping(value = "/api/public/order/{id}/generate-qr",
        produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQrCode(@PathVariable("id") Long id) {
       OrderResponse order = orderService.getOrderForQr(id);
       String qrContent = 
            "NGAN_HANG: VCB\n" +
            "STK: 1040763843\n" +
            "TEN: PHAN KY ANH\n" +
            "SO_TIEN: " + order.getTotalAmount() + "\n" +
            "NOI_DUNG: DonHang " + order.getId();
       return qrCodeService.generateQrCode(qrContent);
    }
}