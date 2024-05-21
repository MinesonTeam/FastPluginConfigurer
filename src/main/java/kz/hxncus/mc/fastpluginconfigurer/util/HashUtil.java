package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class HashUtil {
    @Getter
    private final MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String toSHA256(String message) {
        return toSHA256(message.getBytes(StandardCharsets.UTF_8));
    }

    public String toSHA256(byte[] bytes) {
        return BytesUtil.bytesToHex(digest.digest(bytes));
    }
}
