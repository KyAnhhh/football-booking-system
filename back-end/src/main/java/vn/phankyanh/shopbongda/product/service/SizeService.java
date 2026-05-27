package vn.phankyanh.shopbongda.product.service;

import java.util.List;

import vn.phankyanh.shopbongda.product.dto.request.SizeRequest;
import vn.phankyanh.shopbongda.product.dto.response.SizeResponse;

public interface SizeService {

    SizeResponse createSize(SizeRequest request);

    List<SizeResponse> getAllSize();

    SizeResponse getSizeById(Long id);

    SizeResponse updateSize(Long id, SizeRequest request);

    void deleteSize(Long id);
}