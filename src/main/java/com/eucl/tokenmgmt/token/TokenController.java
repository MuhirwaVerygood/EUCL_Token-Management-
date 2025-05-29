package com.eucl.tokenmgmt.token;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.eucl.tokenmgmt.user.Role;
import com.eucl.tokenmgmt.user.User;
import com.eucl.tokenmgmt.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final UserService userService;

    @Operation(
        summary = "Purchase electricity token", 
        description = "Allows customer to purchase a token by entering amount and meter number",
        responses = {
            @ApiResponse(responseCode = "201", description = "Token purchased successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Meter number not found")
        }
    )
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseToken(@Valid @RequestBody TokenPurchaseDTO tokenPurchaseDTO) {
        return tokenService.purchaseToken(tokenPurchaseDTO.getMeterNumber(), tokenPurchaseDTO.getAmount());
    }

    @Operation(
        summary = "Validate token", 
        description = "Validates a token and shows number of days",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "400", description = "Invalid token format"),
            @ApiResponse(responseCode = "404", description = "Token not found"),
            @ApiResponse(responseCode = "410", description = "Token has expired")
        }
    )
    @GetMapping("/validate/{token}")
    public ResponseEntity<?> validateToken(@PathVariable String token) {
        return tokenService.validateToken(token.replace("-", ""));
    }

    @Operation(
        summary = "Get tokens by meter number", 
        description = "Lists all tokens for a given meter number",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of tokens retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid meter number format")
        }
    )
    @GetMapping("/meter/{meterNumber}")
    public ResponseEntity<?> getTokensByMeterNumber(@PathVariable String meterNumber) {
        return tokenService.getTokensByMeterNumber(meterNumber);
    }

    @Operation(
        summary = "Get all tokens", 
        description = "Returns a list of all tokens (admin only)",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of tokens retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @GetMapping
    public ResponseEntity<?> getAllTokens(@AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;

        if (!isAdmin) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        List<PurchasedToken> tokens = tokenService.getAllTokens();
        List<TokenResponseDTO> tokenDTOs = tokens.stream()
                .map(tokenService::convertToResponseDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(tokenDTOs, HttpStatus.OK);
    }

    @Operation(
        summary = "Get token by ID", 
        description = "Returns a token by its ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Token not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getTokenById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;

        if (!isAdmin) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        Optional<PurchasedToken> token = tokenService.getTokenById(id);
        if (token.isEmpty()) {
            return new ResponseEntity<>("Token not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(tokenService.convertToResponseDTO(token.get()), HttpStatus.OK);
    }

    @Operation(
        summary = "Update token status", 
        description = "Updates a token's status (admin only)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Token not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        }
    )
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateTokenStatus(
            @PathVariable Long id, 
            @RequestParam TokenStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;

        if (!isAdmin) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        return tokenService.updateTokenStatus(id, status);
    }

    @Operation(
        summary = "Delete token", 
        description = "Deletes a token (admin only)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Token not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteToken(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;

        if (!isAdmin) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        return tokenService.deleteToken(id);
    }
}
