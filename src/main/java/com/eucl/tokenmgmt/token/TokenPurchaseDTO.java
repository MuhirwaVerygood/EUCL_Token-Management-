package com.eucl.tokenmgmt.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenPurchaseDTO {
    @NotBlank(message = "Meter number is mandatory")
    @Pattern(regexp = "^\\d{6}$", message = "Meter number must be exactly 6 digits")
    private String meterNumber;

    @NotNull(message = "Amount is mandatory")
    @Min(value = 100, message = "Amount must be at least 100 RWF")
    @Positive(message = "Amount must be positive")
    private int amount;
}
