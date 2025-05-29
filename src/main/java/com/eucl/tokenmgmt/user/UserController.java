package com.eucl.tokenmgmt.user;

import com.eucl.tokenmgmt.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Operation(
        summary = "Register a new user", 
        description = "Allows users to sign up with name, email, phone, national ID (must be exactly 16 characters), password, and role",
        responses = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Email already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        }
    )
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO userDTO) {
        if (userService.findByEmail(userDTO.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setNationalId(userDTO.getNationalId());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        userService.saveUser(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @Operation(
        summary = "User login", 
        description = "Allows users to log in with email and password and receive access and refresh tokens",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login successful", 
                content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
        }
    )
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            return new ResponseEntity<>(new JwtResponse(accessToken, refreshToken), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
        summary = "Get all users", 
        description = "Returns a list of all users (admin only)",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> userDTOs = users.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    @Operation(
        summary = "Get user by ID", 
        description = "Returns a user by their ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is requesting their own data or is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;
        boolean isOwnData = requestingUser.isPresent() && 
                           requestingUser.get().getId().equals(id);

        if (!isAdmin && !isOwnData) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(convertToResponseDTO(user.get()), HttpStatus.OK);
    }

    @Operation(
        summary = "Update user", 
        description = "Updates a user's information",
        responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, 
                                           @Valid @RequestBody UserDTO userDTO,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is updating their own data or is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;
        boolean isOwnData = requestingUser.isPresent() && 
                           requestingUser.get().getId().equals(id);

        if (!isAdmin && !isOwnData) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        return userService.updateUser(id, userDTO);
    }

    @Operation(
        summary = "Delete user", 
        description = "Deletes a user (admin only)",
        responses = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, 
                                           @AuthenticationPrincipal UserDetails userDetails) {
        // Check if user is an admin
        Optional<User> requestingUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = requestingUser.isPresent() && 
                          requestingUser.get().getRole() == Role.ROLE_ADMIN;

        if (!isAdmin) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }

        return userService.deleteUser(id);
    }

    // Helper method to convert User to UserResponseDTO (without password)
    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setNationalId(user.getNationalId());
        dto.setRole(user.getRole());
        return dto;
    }
}
