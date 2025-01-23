package io.org.reactivestax.repository;

import io.org.reactivestax.domain.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepository extends JpaRepository<Otp,Long> {
    Otp findByOtpId(String otpId);
}
