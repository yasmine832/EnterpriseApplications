package entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the category of a product.")
public enum ProductCategory {
    LIGHTING,
    CABLES,
    CONTROL_PANELS,
}