package io.org.reactivestax.repository;

import io.org.reactivestax.domain.OTPLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPLoginRepository extends JpaRepository<OTPLogin,Long> {
    OTPLogin findByOtpId(String otpId);
}
