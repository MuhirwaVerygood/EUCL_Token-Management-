package com.eucl.tokenmgmt.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<PurchasedToken, Long> {
    List<PurchasedToken> findByMeterNumber(String meterNumber);
    Optional<PurchasedToken> findByToken(String token);
}