package com.eucl.tokenmgmt.meter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterDTO {
    @NotBlank(message = "Meter number is mandatory")
    @Pattern(regexp = "^[0-9A-Za-z]{6}$", message = "Meter number must be exactly 6 alphanumeric characters")
    private String meterNumber;

    @NotNull(message = "User ID is mandatory")
    @Positive(message = "User ID must be positive")
    private Long userId;
}