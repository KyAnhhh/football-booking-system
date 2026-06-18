package vn.phankyanh.shopbongda.holiday.mapper;
import org.mapstruct.Mapper;
import vn.phankyanh.shopbongda.holiday.domain.Holiday;
import vn.phankyanh.shopbongda.holiday.dto.request.HolidayRequest;
import vn.phankyanh.shopbongda.holiday.dto.response.HolidayResponse;

@Mapper(componentModel = "spring")
public interface HolidayMapper {

    Holiday toEntity(HolidayRequest request);

    HolidayResponse toResponse(Holiday holiday);

}
