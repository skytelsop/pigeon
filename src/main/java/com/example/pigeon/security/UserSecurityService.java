package com.example.pigeon.security;

import com.example.pigeon.model.PasswordResetToken;
import com.example.pigeon.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;

@Service
@Transactional
public class UserSecurityService implements ISecurityUserService {

    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public String validatePasswordResetToken(String token) {

        final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        return !isTokenFound(passwordResetToken) ? "invalidToken"
                : isTokenExpired(passwordResetToken) ? "expired" : null;
    }

    private boolean isTokenFound(PasswordResetToken passwordResetToken) { return passwordResetToken != null; }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {

        final Calendar cal = Calendar.getInstance();
        return passwordResetToken.getExpiryDate().before(cal.getTime());
    }
}
