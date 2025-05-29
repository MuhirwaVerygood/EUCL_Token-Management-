package com.eucl.tokenmgmt.meter;

import com.eucl.tokenmgmt.user.User;
import lombok.* ;
import jakarta.persistence.*;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 6)
    private String meterNumber;

    @ManyToOne
    private User user;
}