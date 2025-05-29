package com.eucl.tokenmgmt.meter;

import com.eucl.tokenmgmt.user.User;
import com.eucl.tokenmgmt.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeterService {
    private final MeterRepository meterRepository;
    private final UserRepository userRepository;

    public ResponseEntity<String> registerMeter(String meterNumber, Long userId) {
        if (!meterNumber.matches("^[0-9A-Za-z]{6}$")) {
            return new ResponseEntity<>("Meter number must be 6 alphanumeric characters", HttpStatus.BAD_REQUEST);
        }
        if (meterRepository.findByMeterNumber(meterNumber).isPresent()) {
            return new ResponseEntity<>("Meter number already exists", HttpStatus.CONFLICT);
        }
        if (!userRepository.findById(userId).isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userRepository.findById(userId).get();
        Meter meter = new Meter();
        meter.setMeterNumber(meterNumber);
        meter.setUser(user);
        meterRepository.save(meter);
        return new ResponseEntity<>("Meter registered successfully", HttpStatus.CREATED);
    }

    public List<Meter> getAllMeters() {
        return meterRepository.findAll();
    }

    public Optional<Meter> getMeterById(Long id) {
        return meterRepository.findById(id);
    }

    public Optional<Meter> getMeterByNumber(String meterNumber) {
        return meterRepository.findByMeterNumber(meterNumber);
    }

    public List<Meter> getMetersByUserId(Long userId) {
        return meterRepository.findByUserId(userId);
    }

    public ResponseEntity<String> updateMeter(Long id, MeterDTO meterDTO) {
        Optional<Meter> existingMeter = meterRepository.findById(id);
        if (existingMeter.isEmpty()) {
            return new ResponseEntity<>("Meter not found", HttpStatus.NOT_FOUND);
        }

        // Check if meter number is being changed and if it already exists
        Meter meter = existingMeter.get();
        if (!meter.getMeterNumber().equals(meterDTO.getMeterNumber()) && 
            meterRepository.findByMeterNumber(meterDTO.getMeterNumber()).isPresent()) {
            return new ResponseEntity<>("Meter number already exists", HttpStatus.CONFLICT);
        }

        // Check if user exists
        Optional<User> user = userRepository.findById(meterDTO.getUserId());
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        meter.setMeterNumber(meterDTO.getMeterNumber());
        meter.setUser(user.get());
        meterRepository.save(meter);

        return new ResponseEntity<>("Meter updated successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> deleteMeter(Long id) {
        if (!meterRepository.existsById(id)) {
            return new ResponseEntity<>("Meter not found", HttpStatus.NOT_FOUND);
        }

        meterRepository.deleteById(id);
        return new ResponseEntity<>("Meter deleted successfully", HttpStatus.OK);
    }

    public MeterResponseDTO convertToResponseDTO(Meter meter) {
        MeterResponseDTO dto = new MeterResponseDTO();
        dto.setId(meter.getId());
        dto.setMeterNumber(meter.getMeterNumber());
        dto.setUserId(meter.getUser().getId());
        dto.setUserName(meter.getUser().getName());
        return dto;
    }
}
