package org.icbt.onlinebillingsystempahanaedu.core.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 8/5/2025
 * time : 1:05 PM
 */
public class PasswordSecurityUtil {

    private static final Logger logger = Logger.getLogger(PasswordSecurityUtil.class.getName());

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    private static final byte[] FIXED_SALT = {
            (byte) 0x23, (byte) 0x64, (byte) 0x56, (byte) 0x12,
            (byte) 0x5a, (byte) 0x47, (byte) 0x31, (byte) 0x2b,
            (byte) 0x8f, (byte) 0x7c, (byte) 0x5e, (byte) 0x90,
            (byte) 0xa1, (byte) 0xb2, (byte) 0xc3, (byte) 0xd4
    };


    private PasswordSecurityUtil() {

    }

    public static String hashPassword(String password) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), FIXED_SALT, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.log(Level.SEVERE, "Password hashing failed: " + e.getMessage(), e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        // Hash the provided password using the same method and parameters
        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(storedHash);
    }
}
