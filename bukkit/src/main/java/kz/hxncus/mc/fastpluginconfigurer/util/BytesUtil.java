package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BytesUtil {
    public String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
