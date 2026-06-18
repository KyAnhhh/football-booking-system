package vn.phankyanh.shopbongda.extra.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import vn.phankyanh.shopbongda.extra.dto.request.ExtraServiceRequest;
import vn.phankyanh.shopbongda.extra.dto.response.ExtraServiceResponse;
import vn.phankyanh.shopbongda.extra.service.ExtraServiceService;

@RestController
@RequestMapping("/api/admin/extra-services")
@RequiredArgsConstructor
public class AdminExtraServiceController {

    private final ExtraServiceService extraServiceService;

    @PostMapping
    public ExtraServiceResponse create(
            @RequestBody ExtraServiceRequest request
    ) {

        return extraServiceService.create(request);
    }

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

    @PutMapping("/{id}")
    public ExtraServiceResponse update(
            @PathVariable Long id,
            @RequestBody ExtraServiceRequest request
    ) {

        return extraServiceService.update(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {

        extraServiceService.delete(id);
    }
}