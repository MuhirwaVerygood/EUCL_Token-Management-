package com.eucl.tokenmgmt.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {
    private Long id;
    private String meterNumber;
    private String token;
    private TokenStatus tokenStatus;
    private int tokenValueDays;
    private LocalDateTime purchasedDate;
    private int amount;
}