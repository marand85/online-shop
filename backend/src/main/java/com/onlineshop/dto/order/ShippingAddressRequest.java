package com.onlineshop.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShippingAddressRequest(
        @NotBlank @Size(max = 200) String name,

        @NotBlank @Size(max = 200) String line1,

        @Size(max = 200) String line2,

        @NotBlank @Size(max = 120) String city,

        @Size(max = 120) String state,

        @NotBlank @Size(max = 32) String postal,

        @NotBlank @Size(min = 2, max = 2) String country

) {
}
