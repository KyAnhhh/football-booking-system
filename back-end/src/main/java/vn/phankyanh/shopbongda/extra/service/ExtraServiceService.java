package vn.phankyanh.shopbongda.extra.service;

import java.util.List;
import vn.phankyanh.shopbongda.extra.dto.request.ExtraServiceRequest;
import vn.phankyanh.shopbongda.extra.dto.response.ExtraServiceResponse;

public interface ExtraServiceService {

    ExtraServiceResponse create(
            ExtraServiceRequest request
    );

    List<ExtraServiceResponse> getAll();

    ExtraServiceResponse getById(
            Long id
    );

    ExtraServiceResponse update(
            Long id,
            ExtraServiceRequest request
    );

    void delete(
            Long id
    );
}
