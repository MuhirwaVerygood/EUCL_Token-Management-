package com.eucl.tokenmgmt.notification;

import com.eucl.tokenmgmt.meter.MeterRepository;
import com.eucl.tokenmgmt.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JdbcTemplate jdbcTemplate;
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final MeterRepository meterRepository;

    @Scheduled(cron = "0 0 * * * *") // Run hourly
    public void checkExpiringTokens() {
        String sql = "SELECT pt.id, pt.meter_number, pt.token, pt.token_value_days, pt.purchased_date, u.name, u.email " +
                "FROM purchased_tokens pt " +
                "JOIN meter m ON pt.meter_number = m.meter_number " +
                "JOIN _user u ON m.user_id = u.id " +
                "WHERE pt.token_status = 'NEW' AND " +
                "EXTRACT(EPOCH FROM (NOW() - pt.purchased_date))/3600 >= (pt.token_value_days * 24 - 5)";

        List<NotificationDTO> expiringTokens = jdbcTemplate.query(sql, (rs, rowNum) -> {
            NotificationDTO dto = new NotificationDTO();
            dto.setMeterNumber(rs.getString("meter_number"));
            dto.setName(rs.getString("name"));
            dto.setEmail(rs.getString("email"));
            return dto;
        });

        for (NotificationDTO dto : expiringTokens) {
            if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                continue; // Skip invalid emails
            }
            String message = String.format(
                    "Dear %s, REG is pleased to remind you that the token in the %s is going to expire in 5 hours. Please purchase a new token.",
                    dto.getName(), dto.getMeterNumber());
            Notification notification = new Notification();
            notification.setMeterNumber(dto.getMeterNumber());
            notification.setMessage(message);
            notification.setIssuedDate(LocalDateTime.now());
            notificationRepository.save(notification);

            sendEmail(dto.getEmail(), "Token Expiry Notification", message);
        }
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
