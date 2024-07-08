package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {
    public int length(final CharSequence sequence) {
        return sequence == null ? 0 : sequence.length();
    }

    public boolean isEmpty(final CharSequence sequence) {
        return sequence == null || sequence.length() == 0;
    }

    public boolean isNotEmpty(final CharSequence sequence) {
        return !isEmpty(sequence);
    }

    public boolean isBlank(final CharSequence sequence) {
        final int strLen = length(sequence);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(sequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean isNotBlank(final CharSequence sequence) {
        return !isBlank(sequence);
    }

    public boolean contains(final CharSequence sequence, final CharSequence searchSeq) {
        if (sequence == null || searchSeq == null) {
            return false;
        }
        return CharSequenceUtil.indexOf(sequence, searchSeq, 0) >= 0;
    }

    public boolean contains(final CharSequence sequence, final int searchChar) {
        if (isEmpty(sequence)) {
            return false;
        }
        return CharSequenceUtil.indexOf(sequence, searchChar, 0) >= 0;
    }

    public boolean isNumeric(final CharSequence sequence) {
        if (isEmpty(sequence)) {
            return false;
        }
        for (int i = 0; i < sequence.length(); i++) {
            if (!Character.isDigit(sequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
