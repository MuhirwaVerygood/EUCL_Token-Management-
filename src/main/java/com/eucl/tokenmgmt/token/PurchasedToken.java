package com.eucl.tokenmgmt.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchased_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 6)
    private String meterNumber;

    @Column(length = 16)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenStatus tokenStatus;

    private int tokenValueDays;

    private LocalDateTime purchasedDate;

    private int amount;
}