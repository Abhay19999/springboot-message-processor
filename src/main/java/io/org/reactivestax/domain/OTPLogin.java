package io.org.reactivestax.domain;

import io.org.reactivestax.type.enums.CustomerStatusEnum;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;

import io.org.reactivestax.type.enums.OTPStatusEnum;

import io.org.reactivestax.type.enums.OTPVerificationStatusEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class OTPLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;


    @Column(nullable = false, unique = true, updatable = false)
    private String otpId;

    private Long otpNumber;

    private Integer countGenerationNumber;

    private Long clientId;


    @Enumerated(EnumType.STRING)
    private OTPVerificationStatusEnum otpVerificationStatus;

    @Enumerated(EnumType.STRING)
    private OTPStatusEnum otpStatus;

    private int verificationCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private CustomerStatusEnum customerStatusEnum;

    private LocalDateTime blockedTimeFrame;
    private String mobileNumber;
    private String email;

    @Enumerated(EnumType.STRING)
    private DeliveryMethodEnum deliveryMethod;

    @PrePersist
    public void onPrePersist() {
        if (otpId == null) {
            otpId = UUID.randomUUID().toString();
        }
    }
}
