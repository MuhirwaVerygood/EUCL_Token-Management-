package com.eucl.tokenmgmt.meter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterResponseDTO {
    private Long id;
    private String meterNumber;
    private Long userId;
    private String userName;
}