package ro.kutaba.finance.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 40, message = "Category name cannot exceed 40 characters")
        String name
) {
}