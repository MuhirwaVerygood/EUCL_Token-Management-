package com.eucl.tokenmgmt.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is mandatory")
    @Pattern(regexp = "^07[89]\\d{7}$", message = "Phone number must be 10 digits starting with 078 or 079")
    private String phone;

    @NotBlank(message = "National ID is mandatory")
    @Size(min = 16, max = 16, message = "National ID must be exactly 16 digits")
    @Pattern(regexp = "^[12]\\d{4}[78]\\d{10}$", message = "National ID must be 16 digits starting with 1 (Rwandan) or 2 (refugee), followed by year of birth, then 7 (female) or 8 (male), followed by other digits")
    private String nationalId;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is mandatory")
    private Role role;
}
