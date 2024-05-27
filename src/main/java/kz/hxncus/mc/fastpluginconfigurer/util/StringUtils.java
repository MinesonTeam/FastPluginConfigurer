package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    public int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public boolean contains(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return false;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, 0) >= 0;
    }

    public boolean contains(final CharSequence seq, final int searchChar) {
        if (isEmpty(seq)) {
            return false;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, 0) >= 0;
    }

    public boolean isNumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
