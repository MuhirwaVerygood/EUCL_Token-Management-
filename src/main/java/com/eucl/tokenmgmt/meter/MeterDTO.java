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
    @Pattern(regexp = "^\\d{6}$", message = "Meter number must be exactly 6 digits")
    private String meterNumber;

    @NotNull(message = "User ID is mandatory")
    @Positive(message = "User ID must be positive")
    private Long userId;
}
