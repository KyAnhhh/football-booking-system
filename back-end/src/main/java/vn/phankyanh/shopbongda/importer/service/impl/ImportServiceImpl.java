package vn.phankyanh.shopbongda.importer.service.impl;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.field.domain.FieldStatus;
import vn.phankyanh.shopbongda.field.domain.FieldType;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.domain.FootballFieldImage;
import vn.phankyanh.shopbongda.field.repository.FootballFieldImageRepository;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.holiday.domain.Holiday;
import vn.phankyanh.shopbongda.holiday.repository.HolidayRepository;
import vn.phankyanh.shopbongda.importer.domain.ImportType;
import vn.phankyanh.shopbongda.importer.dto.ImportResultResponse;
import vn.phankyanh.shopbongda.importer.dto.ImportRowIssueResponse;
import vn.phankyanh.shopbongda.importer.service.ImportService;
import vn.phankyanh.shopbongda.pricing.domain.FieldPricingRule;
import vn.phankyanh.shopbongda.pricing.domain.HolidayPricingRule;
import vn.phankyanh.shopbongda.pricing.repository.FieldPricingRuleRepository;
import vn.phankyanh.shopbongda.pricing.repository.HolidayPricingRuleRepository;
import vn.phankyanh.shopbongda.product.domain.Brand;
import vn.phankyanh.shopbongda.product.domain.Category;
import vn.phankyanh.shopbongda.product.domain.Product;
import vn.phankyanh.shopbongda.product.domain.ProductImage;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.domain.Size;
import vn.phankyanh.shopbongda.product.repository.BrandRepository;
import vn.phankyanh.shopbongda.product.repository.CategoryRepository;
import vn.phankyanh.shopbongda.product.repository.ProductImageRepository;
import vn.phankyanh.shopbongda.product.repository.ProductRepository;
import vn.phankyanh.shopbongda.product.repository.ProductVariantRepository;
import vn.phankyanh.shopbongda.product.repository.SizeRepository;

@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final SizeRepository sizeRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final FootballFieldRepository footballFieldRepository;
    private final FootballFieldImageRepository footballFieldImageRepository;
    private final FieldPricingRuleRepository fieldPricingRuleRepository;
    private final HolidayRepository holidayRepository;
    private final HolidayPricingRuleRepository holidayPricingRuleRepository;

    @Override
    public String getTemplate(ImportType type) {
        return switch (type) {
            case SIZES -> "code,name,description\n"
                    + "S,Size S,Ao size S\n"
                    + "M,Size M,Ao size M\n";
            case PRODUCTS -> "name,description,discount,categoryId,brandId\n"
                    + "Ao Real Madrid,Ao san nha mua moi,10,1,1\n"
                    + "Giay Nike Mercurial,Giay san co nhan tao,15,2,1\n";
            case PRODUCT_VARIANTS -> "productId,sizeId,price,stock\n"
                    + "1,1,250000,20\n"
                    + "1,2,250000,15\n";
            case FOOTBALL_FIELDS -> "name,type,imageUrl,pricePerHour,status,address,description\n"
                    + "San bong Mini A,FIVE_A_SIDE,https://example.com/fields/san-a.jpg,250000,AVAILABLE,Quan 1 TP HCM,San co nhan tao 5 nguoi\n"
                    + "San bong 7 nguoi B,SEVEN_A_SIDE,https://example.com/fields/san-b.jpg,400000,AVAILABLE,Quan 7 TP HCM,San 7 nguoi co den chieu sang\n";
            case PRODUCT_IMAGES -> "productId,imageUrl,isPrimary,sortOrder\n"
                    + "1,https://example.com/products/ao-real-1.jpg,true,1\n"
                    + "1,https://example.com/products/ao-real-2.jpg,false,2\n";
            case FOOTBALL_FIELD_IMAGES -> "fieldId,imageUrl,isPrimary,sortOrder\n"
                    + "1,https://example.com/fields/san-a-1.jpg,true,1\n"
                    + "1,https://example.com/fields/san-a-2.jpg,false,2\n";
            case FIELD_PRICING_RULES -> "fieldId,dayOfWeek,startTime,endTime,pricePerSlot,slotDuration,isActive\n"
                    + "1,MONDAY,07:00,17:00,250000,90,true\n"
                    + "1,MONDAY,17:00,23:45,450000,90,true\n";
            case HOLIDAYS -> "name,holidayDate,description,isActive\n"
                    + "Quoc khanh,2026-09-02,Ngay Quoc khanh Viet Nam,true\n"
                    + "Tet Duong lich,2027-01-01,Ngay dau nam moi,true\n";
            case HOLIDAY_PRICING_RULES -> "fieldId,holidayId,startTime,endTime,pricePerSlot,slotDuration,isActive\n"
                    + "1,1,07:00,17:00,400000,90,true\n"
                    + "1,1,17:00,23:45,600000,90,true\n";
        };
    }

    @Override
    @Transactional
    public ImportResultResponse importCsv(ImportType type, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File CSV không được để trống");
        }

        ImportResultResponse result = ImportResultResponse.builder()
                .type(type.name())
                .build();

        try (
                InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreEmptyLines(true)
                        .setTrim(true)
                        .build()
                        .parse(reader)
        ) {
            for (CSVRecord record : parser) {
                int rowNumber = Math.toIntExact(record.getRecordNumber() + 1);
                result.setTotalRows(result.getTotalRows() + 1);

                try {
                    Long createdId = importRow(type, record, rowNumber, result);
                    if (createdId == null) {
                        result.setFailedRows(result.getFailedRows() + 1);
                    } else {
                        result.setSuccessRows(result.getSuccessRows() + 1);
                        result.getCreatedIds().add(createdId);
                    }
                } catch (RuntimeException ex) {
                    result.setFailedRows(result.getFailedRows() + 1);
                    result.getErrors().add(issue(rowNumber, "row", "", ex.getMessage()));
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Không thể đọc file CSV: " + ex.getMessage());
        }

        result.setWarningRows(result.getWarnings().size());
        return result;
    }

    private Long importRow(ImportType type, CSVRecord record, int rowNumber, ImportResultResponse result) {
        return switch (type) {
            case SIZES -> importSize(record, rowNumber, result);
            case PRODUCTS -> importProduct(record, rowNumber, result);
            case PRODUCT_VARIANTS -> importProductVariant(record, rowNumber, result);
            case FOOTBALL_FIELDS -> importFootballField(record, rowNumber, result);
            case PRODUCT_IMAGES -> importProductImage(record, rowNumber, result);
            case FOOTBALL_FIELD_IMAGES -> importFootballFieldImage(record, rowNumber, result);
            case FIELD_PRICING_RULES -> importFieldPricingRule(record, rowNumber, result);
            case HOLIDAYS -> importHoliday(record, rowNumber, result);
            case HOLIDAY_PRICING_RULES -> importHolidayPricingRule(record, rowNumber, result);
        };
    }

    private Long importSize(CSVRecord record, int rowNumber, ImportResultResponse result) {
        String code = value(record, "code");
        String name = value(record, "name");
        String description = value(record, "description");

        if (isBlank(code)) {
            result.getErrors().add(issue(rowNumber, "code", code, "Mã size không được để trống"));
            return null;
        }
        if (isBlank(name)) {
            result.getErrors().add(issue(rowNumber, "name", name, "Tên size không được để trống"));
            return null;
        }
        if (sizeRepository.existsByCode(code)) {
            result.getErrors().add(issue(rowNumber, "code", code, "Mã size đã tồn tại"));
            return null;
        }

        Size size = Size.builder()
                .code(code)
                .name(name)
                .description(description)
                .build();

        return sizeRepository.save(size).getId();
    }

    private Long importProduct(CSVRecord record, int rowNumber, ImportResultResponse result) {
        String name = value(record, "name");
        String description = value(record, "description");
        String discountValue = value(record, "discount");
        String categoryIdValue = value(record, "categoryId");
        String brandIdValue = value(record, "brandId");

        if (isBlank(name)) {
            result.getErrors().add(issue(rowNumber, "name", name, "Tên sản phẩm không được để trống"));
            return null;
        }
        if (isBlank(description)) {
            result.getErrors().add(issue(rowNumber, "description", description, "Mô tả sản phẩm không được để trống"));
            return null;
        }
        if (productRepository.existsByName(name)) {
            result.getErrors().add(issue(rowNumber, "name", name, "Tên sản phẩm đã tồn tại"));
            return null;
        }

        Integer discount = parseInteger(discountValue, "discount");
        if (discount < 0 || discount > 100) {
            result.getErrors().add(issue(rowNumber, "discount", discountValue, "Giảm giá phải nằm trong khoảng 0-100"));
            return null;
        }

        Long categoryId = parseLong(categoryIdValue, "categoryId");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục id " + categoryId));

        Long brandId = parseLong(brandIdValue, "brandId");
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thương hiệu id " + brandId));

        Product product = Product.builder()
                .name(name)
                .description(description)
                .discount(discount)
                .category(category)
                .brand(brand)
                .variants(new ArrayList<>())
                .images(new ArrayList<>())
                .build();

        Product savedProduct = productRepository.save(product);
        result.getWarnings().add(issue(
                rowNumber,
                "variant",
                savedProduct.getId().toString(),
                "Sản phẩm đã được tạo nhưng cần import Product Variant để có giá và tồn kho"
        ));
        return savedProduct.getId();
    }

    private Long importProductVariant(CSVRecord record, int rowNumber, ImportResultResponse result) {
        String productIdValue = value(record, "productId");
        String sizeIdValue = value(record, "sizeId");
        String priceValue = value(record, "price");
        String stockValue = value(record, "stock");

        Long productId = parseLong(productIdValue, "productId");
        Long sizeId = parseLong(sizeIdValue, "sizeId");
        BigDecimal price = parseDecimal(priceValue, "price");
        Integer stock = parseInteger(stockValue, "stock");

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            result.getErrors().add(issue(rowNumber, "price", priceValue, "Giá bán phải lớn hơn 0"));
            return null;
        }
        if (stock < 0) {
            result.getErrors().add(issue(rowNumber, "stock", stockValue, "Tồn kho không được âm"));
            return null;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm id " + productId));
        Size size = sizeRepository.findById(sizeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy size id " + sizeId));

        if (productVariantRepository.existsByProductIdAndSizeId(productId, sizeId)) {
            result.getErrors().add(issue(
                    rowNumber,
                    "productId,sizeId",
                    productId + "," + sizeId,
                    "Biến thể sản phẩm theo size này đã tồn tại"
            ));
            return null;
        }

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .size(size)
                .price(price)
                .stock(stock)
                .soldQuantity(0)
                .build();

        return productVariantRepository.save(variant).getId();
    }

    private Long importFootballField(CSVRecord record, int rowNumber, ImportResultResponse result) {
        String name = value(record, "name");
        FieldType type = parseFieldType(value(record, "type"), "type");
        String imageUrl = value(record, "imageUrl");
        BigDecimal pricePerHour = parseDecimal(value(record, "pricePerHour"), "pricePerHour");
        FieldStatus status = parseFieldStatus(value(record, "status"), "status");
        String address = value(record, "address");
        String description = value(record, "description");

        if (isBlank(name)) {
            result.getErrors().add(issue(rowNumber, "name", name, "Field name is required"));
            return null;
        }
        if (footballFieldRepository.existsByName(name)) {
            result.getErrors().add(issue(rowNumber, "name", name, "Football field name already exists"));
            return null;
        }
        if (pricePerHour.compareTo(BigDecimal.ZERO) <= 0) {
            result.getErrors().add(issue(rowNumber, "pricePerHour", pricePerHour.toPlainString(), "Price per hour must be greater than 0"));
            return null;
        }

        FootballField field = FootballField.builder()
                .name(name)
                .type(type)
                .imageUrl(isBlank(imageUrl) ? "default-field.jpg" : imageUrl)
                .pricePerHour(pricePerHour)
                .status(status)
                .address(address)
                .description(description)
                .build();

        return footballFieldRepository.save(field).getId();
    }

    private Long importProductImage(CSVRecord record, int rowNumber, ImportResultResponse result) {
        String productIdValue = value(record, "productId");
        String imageUrl = value(record, "imageUrl");
        String isPrimaryValue = value(record, "isPrimary");
        String sortOrderValue = value(record, "sortOrder");

        Long productId = parseLong(productIdValue, "productId");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product id " + productId + " not found"));

        if (isBlank(imageUrl)) {
            result.getErrors().add(issue(rowNumber, "imageUrl", imageUrl, "Image URL is required"));
            return null;
        }
        if (productImageRepository.existsByProductIdAndImageUrl(productId, imageUrl)) {
            result.getErrors().add(issue(rowNumber, "imageUrl", imageUrl, "Product image URL already exists"));
            return null;
        }

        boolean isPrimary = parseBoolean(isPrimaryValue, "isPrimary");
        Integer sortOrder = parseInteger(sortOrderValue, "sortOrder");
        if (sortOrder < 0) {
            result.getErrors().add(issue(rowNumber, "sortOrder", sortOrderValue, "Sort order must not be negative"));
            return null;
        }

        if (isPrimary) {
            productImageRepository.resetPrimary(productId);
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .isPrimary(isPrimary)
                .sortOrder(sortOrder)
                .build();

        return productImageRepository.save(image).getId();
    }

    private Long importFootballFieldImage(CSVRecord record, int rowNumber, ImportResultResponse result) {
        String fieldIdValue = value(record, "fieldId");
        String imageUrl = value(record, "imageUrl");
        String isPrimaryValue = value(record, "isPrimary");
        String sortOrderValue = value(record, "sortOrder");

        Long fieldId = parseLong(fieldIdValue, "fieldId");
        FootballField field = footballFieldRepository.findById(fieldId)
                .orElseThrow(() -> new IllegalArgumentException("Football field id " + fieldId + " not found"));

        if (isBlank(imageUrl)) {
            result.getErrors().add(issue(rowNumber, "imageUrl", imageUrl, "Image URL is required"));
            return null;
        }
        if (footballFieldImageRepository.existsByFieldIdAndImageUrl(fieldId, imageUrl)) {
            result.getErrors().add(issue(rowNumber, "imageUrl", imageUrl, "Football field image URL already exists"));
            return null;
        }

        boolean isPrimary = parseBoolean(isPrimaryValue, "isPrimary");
        Integer sortOrder = parseInteger(sortOrderValue, "sortOrder");
        if (sortOrder < 0) {
            result.getErrors().add(issue(rowNumber, "sortOrder", sortOrderValue, "Sort order must not be negative"));
            return null;
        }

        if (isPrimary) {
            footballFieldImageRepository.resetPrimary(fieldId);
            field.setImageUrl(imageUrl);
        }

        FootballFieldImage image = FootballFieldImage.builder()
                .field(field)
                .imageUrl(imageUrl)
                .isPrimary(isPrimary)
                .sortOrder(sortOrder)
                .build();

        FootballFieldImage savedImage = footballFieldImageRepository.save(image);
        if (isPrimary) {
            footballFieldRepository.save(field);
        }
        return savedImage.getId();
    }

    private Long importFieldPricingRule(CSVRecord record, int rowNumber, ImportResultResponse result) {
        Long fieldId = parseLong(value(record, "fieldId"), "fieldId");
        DayOfWeek dayOfWeek = parseDayOfWeek(value(record, "dayOfWeek"), "dayOfWeek");
        LocalTime startTime = parseTime(value(record, "startTime"), "startTime");
        LocalTime endTime = parseTime(value(record, "endTime"), "endTime");
        BigDecimal pricePerSlot = parseDecimal(value(record, "pricePerSlot"), "pricePerSlot");
        Integer slotDuration = parseInteger(value(record, "slotDuration"), "slotDuration");
        boolean isActive = parseBoolean(value(record, "isActive"), "isActive");

        FootballField field = footballFieldRepository.findById(fieldId)
                .orElseThrow(() -> new IllegalArgumentException("Football field id " + fieldId + " not found"));

        if (!endTime.isAfter(startTime)) {
            result.getErrors().add(issue(rowNumber, "endTime", endTime.toString(), "End time must be after start time"));
            return null;
        }
        if (pricePerSlot.compareTo(BigDecimal.ZERO) <= 0) {
            result.getErrors().add(issue(rowNumber, "pricePerSlot", pricePerSlot.toPlainString(), "Price per slot must be greater than 0"));
            return null;
        }
        if (slotDuration <= 0) {
            result.getErrors().add(issue(rowNumber, "slotDuration", slotDuration.toString(), "Slot duration must be greater than 0"));
            return null;
        }
        if (isActive && !fieldPricingRuleRepository
                .findByFieldIdAndDayOfWeekAndTimeOverlaps(fieldId, dayOfWeek, startTime, endTime)
                .isEmpty()) {
            result.getErrors().add(issue(rowNumber, "time", startTime + "-" + endTime, "Pricing rule overlaps with an existing active rule"));
            return null;
        }

        FieldPricingRule rule = FieldPricingRule.builder()
                .field(field)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .pricePerSlot(pricePerSlot)
                .slotDuration(slotDuration)
                .isActive(isActive)
                .build();

        return fieldPricingRuleRepository.save(rule).getId();
    }

    private Long importHoliday(CSVRecord record, int rowNumber, ImportResultResponse result) {
        String name = value(record, "name");
        LocalDate holidayDate = parseDate(value(record, "holidayDate"), "holidayDate");
        String description = value(record, "description");
        boolean isActive = parseBoolean(value(record, "isActive"), "isActive");

        if (isBlank(name)) {
            result.getErrors().add(issue(rowNumber, "name", name, "Holiday name is required"));
            return null;
        }
        if (holidayRepository.existsByHolidayDate(holidayDate)) {
            result.getErrors().add(issue(rowNumber, "holidayDate", holidayDate.toString(), "Holiday date already exists"));
            return null;
        }

        Holiday holiday = Holiday.builder()
                .name(name)
                .holidayDate(holidayDate)
                .description(description)
                .isActive(isActive)
                .build();

        return holidayRepository.save(holiday).getId();
    }

    private Long importHolidayPricingRule(CSVRecord record, int rowNumber, ImportResultResponse result) {
        Long fieldId = parseLong(value(record, "fieldId"), "fieldId");
        Long holidayId = parseLong(value(record, "holidayId"), "holidayId");
        LocalTime startTime = parseTime(value(record, "startTime"), "startTime");
        LocalTime endTime = parseTime(value(record, "endTime"), "endTime");
        BigDecimal pricePerSlot = parseDecimal(value(record, "pricePerSlot"), "pricePerSlot");
        Integer slotDuration = parseInteger(value(record, "slotDuration"), "slotDuration");
        boolean isActive = parseBoolean(value(record, "isActive"), "isActive");

        FootballField field = footballFieldRepository.findById(fieldId)
                .orElseThrow(() -> new IllegalArgumentException("Football field id " + fieldId + " not found"));
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new IllegalArgumentException("Holiday id " + holidayId + " not found"));

        if (!endTime.isAfter(startTime)) {
            result.getErrors().add(issue(rowNumber, "endTime", endTime.toString(), "End time must be after start time"));
            return null;
        }
        if (pricePerSlot.compareTo(BigDecimal.ZERO) <= 0) {
            result.getErrors().add(issue(rowNumber, "pricePerSlot", pricePerSlot.toPlainString(), "Price per slot must be greater than 0"));
            return null;
        }
        if (slotDuration <= 0) {
            result.getErrors().add(issue(rowNumber, "slotDuration", slotDuration.toString(), "Slot duration must be greater than 0"));
            return null;
        }
        if (isActive && !holidayPricingRuleRepository
                .findActiveOverlaps(fieldId, holidayId, startTime, endTime)
                .isEmpty()) {
            result.getErrors().add(issue(rowNumber, "time", startTime + "-" + endTime, "Holiday pricing rule overlaps with an existing active rule"));
            return null;
        }

        HolidayPricingRule rule = HolidayPricingRule.builder()
                .field(field)
                .holiday(holiday)
                .startTime(startTime)
                .endTime(endTime)
                .pricePerSlot(pricePerSlot)
                .slotDuration(slotDuration)
                .isActive(isActive)
                .build();

        return holidayPricingRuleRepository.save(rule).getId();
    }

    private String value(CSVRecord record, String field) {
        if (!record.isMapped(field)) {
            throw new IllegalArgumentException("Thiếu cột bắt buộc: " + field);
        }
        return record.get(field) == null ? "" : record.get(field).trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Long parseLong(String value, String field) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Cột " + field + " không được để trống");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Cột " + field + " phải là số nguyên");
        }
    }

    private Integer parseInteger(String value, String field) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Cột " + field + " không được để trống");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Cột " + field + " phải là số nguyên");
        }
    }

    private BigDecimal parseDecimal(String value, String field) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Cột " + field + " không được để trống");
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Cột " + field + " phải là số");
        }
    }

    private boolean parseBoolean(String value, String field) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Column " + field + " is required");
        }
        if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
            return false;
        }
        throw new IllegalArgumentException("Column " + field + " must be true/false");
    }

    private LocalTime parseTime(String value, String field) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Column " + field + " is required");
        }
        try {
            return LocalTime.parse(value);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Column " + field + " must use HH:mm format");
        }
    }

    private LocalDate parseDate(String value, String field) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Column " + field + " is required");
        }
        try {
            return LocalDate.parse(value);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Column " + field + " must use yyyy-MM-dd format");
        }
    }

    private DayOfWeek parseDayOfWeek(String value, String field) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Column " + field + " is required");
        }
        try {
            return DayOfWeek.valueOf(value.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Column " + field + " must be MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, or SUNDAY");
        }
    }

    private FieldType parseFieldType(String value, String field) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Column " + field + " is required");
        }
        try {
            return FieldType.valueOf(value.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Column " + field + " must be FIVE_A_SIDE, SEVEN_A_SIDE, or ELEVEN_A_SIDE");
        }
    }

    private FieldStatus parseFieldStatus(String value, String field) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Column " + field + " is required");
        }
        try {
            return FieldStatus.valueOf(value.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Column " + field + " must be AVAILABLE or UNAVAILABLE");
        }
    }

    private ImportRowIssueResponse issue(int rowNumber, String field, String value, String message) {
        return ImportRowIssueResponse.builder()
                .rowNumber(rowNumber)
                .field(field)
                .value(value)
                .message(message)
                .build();
    }
}
