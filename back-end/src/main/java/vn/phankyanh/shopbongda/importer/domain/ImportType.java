package vn.phankyanh.shopbongda.importer.domain;

public enum ImportType {
    SIZES("sizes"),
    PRODUCTS("products"),
    PRODUCT_VARIANTS("product-variants"),
    FOOTBALL_FIELDS("football-fields"),
    PRODUCT_IMAGES("product-images"),
    FOOTBALL_FIELD_IMAGES("football-field-images"),
    FIELD_PRICING_RULES("field-pricing-rules"),
    HOLIDAYS("holidays"),
    HOLIDAY_PRICING_RULES("holiday-pricing-rules");

    private final String path;

    ImportType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static ImportType fromPath(String value) {
        for (ImportType type : values()) {
            if (type.path.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported import type: " + value);
    }
}
