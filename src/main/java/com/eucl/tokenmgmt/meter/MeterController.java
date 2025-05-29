package com.eucl.tokenmgmt.meter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.eucl.tokenmgmt.user.Role;
import com.eucl.tokenmgmt.user.User;
import com.eucl.tokenmgmt.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
public class MeterController {

    private final MeterService meterService;
    private final UserService userService;

    @Operation(
        summary = "Register meter number", 
        description = "Allows admin to register a unique 6-character meter number for a user",
        responses = {
            @ApiResponse(responseCode = "201", description = "Meter registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Meter number already exists")
        }
    )
    @PostMapping("/register")
    public ResponseEntity<String> registerMeter(@Valid @RequestBody MeterDTO meterDTO, 
                                              @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;

        if (!isAdmin) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        return meterService.registerMeter(meterDTO.getMeterNumber(), meterDTO.getUserId());
    }

    @Operation(
        summary = "Get all meters", 
        description = "Returns a list of all meters (admin only)",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of meters retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @GetMapping
    public ResponseEntity<?> getAllMeters(@AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;

        if (!isAdmin) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        List<Meter> meters = meterService.getAllMeters();
        List<MeterResponseDTO> meterDTOs = meters.stream()
                .map(meterService::convertToResponseDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(meterDTOs, HttpStatus.OK);
    }

    @Operation(
        summary = "Get meter by ID", 
        description = "Returns a meter by its ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Meter retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Meter not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getMeterById(@PathVariable Long id, 
                                        @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin or the owner of the meter
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        Optional<Meter> meter = meterService.getMeterById(id);

        if (meter.isEmpty()) {
            return new ResponseEntity<>("Meter not found", HttpStatus.NOT_FOUND);
        }

        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;
        boolean isOwner = requestingUser.isPresent() && 
                          meter.get().getUser().getId().equals(requestingUser.get().getId());

        if (!isAdmin && !isOwner) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(meterService.convertToResponseDTO(meter.get()), HttpStatus.OK);
    }

    @Operation(
        summary = "Get meter by number", 
        description = "Returns a meter by its number",
        responses = {
            @ApiResponse(responseCode = "200", description = "Meter retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Meter not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @GetMapping("/number/{meterNumber}")
    public ResponseEntity<?> getMeterByNumber(@PathVariable String meterNumber, 
                                            @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin or the owner of the meter
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        Optional<Meter> meter = meterService.getMeterByNumber(meterNumber);

        if (meter.isEmpty()) {
            return new ResponseEntity<>("Meter not found", HttpStatus.NOT_FOUND);
        }

        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;
        boolean isOwner = requestingUser.isPresent() && 
                          meter.get().getUser().getId().equals(requestingUser.get().getId());

        if (!isAdmin && !isOwner) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(meterService.convertToResponseDTO(meter.get()), HttpStatus.OK);
    }

    @Operation(
        summary = "Get meters by user ID", 
        description = "Returns all meters for a user",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of meters retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMetersByUserId(@PathVariable Long userId, 
                                             @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin or requesting their own meters
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;
        boolean isOwnData = requestingUser.isPresent() && 
                           requestingUser.get().getId().equals(userId);

        if (!isAdmin && !isOwnData) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        List<Meter> meters = meterService.getMetersByUserId(userId);
        List<MeterResponseDTO> meterDTOs = meters.stream()
                .map(meterService::convertToResponseDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(meterDTOs, HttpStatus.OK);
    }

    @Operation(
        summary = "Update meter", 
        description = "Updates a meter's information (admin only)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Meter updated successfully"),
            @ApiResponse(responseCode = "404", description = "Meter or user not found"),
            @ApiResponse(responseCode = "409", description = "Meter number already exists"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMeter(@PathVariable Long id, 
                                            @Valid @RequestBody MeterDTO meterDTO,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;

        if (!isAdmin) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        return meterService.updateMeter(id, meterDTO);
    }

    @Operation(
        summary = "Delete meter", 
        description = "Deletes a meter (admin only)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Meter deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Meter not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMeter(@PathVariable Long id, 
                                            @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;

        if (!isAdmin) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        return meterService.deleteMeter(id);
    }
}
