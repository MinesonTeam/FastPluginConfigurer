package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CharSequenceUtils {
    public final int NOT_FOUND = -1;

    public int indexOf(final CharSequence sequence, final CharSequence searchChar, final int start) {
        if (sequence instanceof String) {
            return ((String) sequence).indexOf(searchChar.toString(), start);
        }
        if (sequence instanceof StringBuilder) {
            return ((StringBuilder) sequence).indexOf(searchChar.toString(), start);
        }
        if (sequence instanceof StringBuffer) {
            return ((StringBuffer) sequence).indexOf(searchChar.toString(), start);
        }
        return sequence.toString().indexOf(searchChar.toString(), start);
    }

    public int indexOf(final CharSequence sequence, final int searchChar, int start) {
        if (sequence instanceof String) {
            return ((String) sequence).indexOf(searchChar, start);
        }
        if (searchChar < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            for (int i = Math.max(start, 0); i < sequence.length(); i++) {
                if (sequence.charAt(i) == searchChar) {
                    return i;
                }
            }
            return NOT_FOUND;
        }
        // supplementary characters (LANG1300)
        if (searchChar <= Character.MAX_CODE_POINT) {
            final char[] chars = Character.toChars(searchChar);
            for (int i = Math.max(start, 0); i < sequence.length() - 1; i++) {
                if (sequence.charAt(i) == chars[0] && sequence.charAt(i + 1) == chars[1]) {
                    return i;
                }
            }
        }
        return NOT_FOUND;
    }
}
