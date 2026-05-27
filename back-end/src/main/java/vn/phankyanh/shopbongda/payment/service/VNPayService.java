package vn.phankyanh.shopbongda.payment.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.payment.config.VNPayConfig;

@Service
@RequiredArgsConstructor
public class VNPayService {
        private final VNPayConfig vnPayConfig;

    public String createPaymentUrl(
            Long bookingId,
            BigDecimal amount,
            HttpServletRequest request
    ) {

        Map<String, String> params = new TreeMap<>();

        params.put("vnp_Version", "2.1.0");

        params.put("vnp_Command", "pay");

        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());

        params.put("vnp_Amount",
                amount.multiply(BigDecimal.valueOf(100)).toString());

        params.put("vnp_CurrCode", "VND");

        params.put("vnp_TxnRef", bookingId.toString());

        params.put("vnp_OrderInfo",
                "Thanh toan booking " + bookingId);

        params.put("vnp_OrderType", "other");

        params.put("vnp_Locale", "vn");

        params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());

        params.put("vnp_IpAddr", request.getRemoteAddr());

        params.put("vnp_CreateDate",
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {

            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));

            query.append("=");

            query.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));

            query.append("&");
        }

        query.deleteCharAt(query.length() - 1);

        return vnPayConfig.getPayUrl() + "?" + query;
    }
}
