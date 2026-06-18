package vn.phankyanh.shopbongda.voucher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.voucher.domain.Voucher;
import vn.phankyanh.shopbongda.voucher.dto.request.VoucherRequest;
import vn.phankyanh.shopbongda.voucher.dto.response.VoucherResponse;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    Voucher toEntity(
            VoucherRequest request
    );

    VoucherResponse toResponse(
            Voucher entity
    );

    void updateEntity(
            VoucherRequest request,
            @MappingTarget Voucher entity
    );
}
