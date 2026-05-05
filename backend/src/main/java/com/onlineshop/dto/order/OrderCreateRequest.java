package com.onlineshop.dto.order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OrderCreateRequest(
                @NotEmpty List<@Valid OrderItemRequest> items,

                @Valid ShippingAddressRequest shipping,

                @NotBlank @Email String contactEmail,

                @Size(max = 32) String contactPhone,

                @NotBlank @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a 3-letter uppercase ISO code") String currency) {
}
