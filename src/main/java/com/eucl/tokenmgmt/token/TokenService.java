package com.eucl.tokenmgmt.token;

import com.eucl.tokenmgmt.meter.MeterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final MeterRepository meterRepository;

    public ResponseEntity<?> purchaseToken(String meterNumber, int amount) {
        if (!meterNumber.matches("^\\d{6}$")) {
            return new ResponseEntity<>("Invalid meter number format. Meter number must be exactly 6 digits", HttpStatus.BAD_REQUEST);
        }
        if (amount < 100 || amount % 100 != 0) {
            return new ResponseEntity<>("Amount must be a multiple of 100 RWF", HttpStatus.BAD_REQUEST);
        }
        int days = amount / 100;
        if (days > 5 * 365) {
            return new ResponseEntity<>("Token duration cannot exceed 5 years", HttpStatus.BAD_REQUEST);
        }
        if (!meterRepository.findByMeterNumber(meterNumber).isPresent()) {
            return new ResponseEntity<>("Meter number not found", HttpStatus.NOT_FOUND);
        }

        PurchasedToken token = new PurchasedToken();
        token.setMeterNumber(meterNumber);
        token.setToken(generateToken());
        token.setTokenStatus(TokenStatus.NEW);
        token.setTokenValueDays(days);
        token.setPurchasedDate(LocalDateTime.now());
        token.setAmount(amount);
        tokenRepository.save(token);
        return new ResponseEntity<>(formatToken(token.getToken()) + " (Valid for " + token.getTokenValueDays() + " days)", HttpStatus.CREATED);
    }

    public ResponseEntity<?> validateToken(String token) {
        if (!token.matches("^\\d{16}$")) {
            return new ResponseEntity<>("Invalid token format", HttpStatus.BAD_REQUEST);
        }
        var purchasedToken = tokenRepository.findByToken(token);
        if (!purchasedToken.isPresent()) {
            return new ResponseEntity<>("Invalid token", HttpStatus.NOT_FOUND);
        }
        if (purchasedToken.get().getTokenStatus() == TokenStatus.EXPIRED) {
            return new ResponseEntity<>("Token has expired", HttpStatus.GONE);
        }
        return new ResponseEntity<>("Token " + formatToken(token) + " is valid for " + purchasedToken.get().getTokenValueDays() + " days", HttpStatus.OK);
    }

    public ResponseEntity<?> getTokensByMeterNumber(String meterNumber) {
        if (!meterNumber.matches("^\\d{6}$")) {
            return new ResponseEntity<>("Invalid meter number format. Meter number must be exactly 6 digits", HttpStatus.BAD_REQUEST);
        }
        List<PurchasedToken> tokens = tokenRepository.findByMeterNumber(meterNumber);
        List<String> formattedTokens = tokens.stream()
                .map(token -> formatToken(token.getToken()) + " (" + token.getTokenStatus() + ")")
                .collect(Collectors.toList());
        return new ResponseEntity<>(formattedTokens, HttpStatus.OK);
    }

    public List<PurchasedToken> getAllTokens() {
        return tokenRepository.findAll();
    }

    public Optional<PurchasedToken> getTokenById(Long id) {
        return tokenRepository.findById(id);
    }

    public ResponseEntity<String> updateTokenStatus(Long id, TokenStatus newStatus) {
        Optional<PurchasedToken> tokenOpt = tokenRepository.findById(id);
        if (tokenOpt.isEmpty()) {
            return new ResponseEntity<>("Token not found", HttpStatus.NOT_FOUND);
        }

        PurchasedToken token = tokenOpt.get();
        token.setTokenStatus(newStatus);
        tokenRepository.save(token);

        return new ResponseEntity<>("Token status updated successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> deleteToken(Long id) {
        if (!tokenRepository.existsById(id)) {
            return new ResponseEntity<>("Token not found", HttpStatus.NOT_FOUND);
        }

        tokenRepository.deleteById(id);
        return new ResponseEntity<>("Token deleted successfully", HttpStatus.OK);
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();
        String chars = "0123456789";
        for (int i = 0; i < 16; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }

    private String formatToken(String token) {
        return token.substring(0, 4) + "-" + token.substring(4, 8) + "-" +
                token.substring(8, 12) + "-" + token.substring(12, 16);
    }

    public TokenResponseDTO convertToResponseDTO(PurchasedToken token) {
        TokenResponseDTO dto = new TokenResponseDTO();
        dto.setId(token.getId());
        dto.setMeterNumber(token.getMeterNumber());
        dto.setToken(formatToken(token.getToken()));
        dto.setTokenStatus(token.getTokenStatus());
        dto.setTokenValueDays(token.getTokenValueDays());
        dto.setPurchasedDate(token.getPurchasedDate());
        dto.setAmount(token.getAmount());
        return dto;
    }
}
