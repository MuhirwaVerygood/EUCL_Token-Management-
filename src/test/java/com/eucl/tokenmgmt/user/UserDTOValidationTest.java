package com.eucl.tokenmgmt.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDTOValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidRwandanNationalId() {
        UserDTO userDTO = createValidUserDTO();
        userDTO.setNationalId("1199978123456789"); // Valid Rwandan female born in 1999

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertTrue(violations.isEmpty(), "No violations should be found for valid Rwandan national ID");
    }

    @Test
    public void testValidRefugeeNationalId() {
        UserDTO userDTO = createValidUserDTO();
        userDTO.setNationalId("2200581234567890"); // Valid refugee male born in 2005

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertTrue(violations.isEmpty(), "No violations should be found for valid refugee national ID");
    }

    @Test
    public void testInvalidFirstDigit() {
        UserDTO userDTO = createValidUserDTO();
        userDTO.setNationalId("3199978123456789"); // Invalid first digit (3)

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertEquals(1, violations.size(), "Should have one violation for invalid first digit");
        assertTrue(violations.iterator().next().getMessage().contains("National ID must be 16 digits starting with 1"));
    }

    @Test
    public void testInvalidGenderDigit() {
        UserDTO userDTO = createValidUserDTO();
        userDTO.setNationalId("1199968123456789"); // Invalid gender digit (6)

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertEquals(1, violations.size(), "Should have one violation for invalid gender digit");
        assertTrue(violations.iterator().next().getMessage().contains("National ID must be 16 digits starting with 1"));
    }

    @Test
    public void testInvalidLength() {
        UserDTO userDTO = createValidUserDTO();
        userDTO.setNationalId("119997812345678"); // Only 15 digits

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertEquals(2, violations.size(), "Should have violations for invalid length and pattern");
    }

    @Test
    public void testNonNumeric() {
        UserDTO userDTO = createValidUserDTO();
        userDTO.setNationalId("1199978ABCDEFGHI"); // Contains letters

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertEquals(1, violations.size(), "Should have one violation for non-numeric characters");
        assertTrue(violations.iterator().next().getMessage().contains("National ID must be 16 digits starting with 1"));
    }

    private UserDTO createValidUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setPhone("0789123456");
        userDTO.setPassword("password123");
        userDTO.setRole(Role.ROLE_CUSTOMER);
        return userDTO;
    }
}
