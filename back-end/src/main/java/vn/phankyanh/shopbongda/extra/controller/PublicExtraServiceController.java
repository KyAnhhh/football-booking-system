package vn.phankyanh.shopbongda.extra.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import vn.phankyanh.shopbongda.extra.dto.response.ExtraServiceResponse;
import vn.phankyanh.shopbongda.extra.service.ExtraServiceService;

@RestController
@RequestMapping("/api/public/extra-services")
@RequiredArgsConstructor
public class PublicExtraServiceController {

    private final ExtraServiceService extraServiceService;

    @GetMapping
    public List<ExtraServiceResponse> getAll() {

        return extraServiceService.getAll();
    }

    @GetMapping("/{id}")
    public ExtraServiceResponse getById(
            @PathVariable Long id
    ) {

        return extraServiceService.getById(id);
    }
}