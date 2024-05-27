package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;

@UtilityClass
public class NumberUtils {
    public boolean isCreatable(final String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        final char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        final int start = chars[0] == '-' || chars[0] == '+' ? 1 : 0;
        if (sz > start + 1 && chars[start] == '0' && !StringUtils.contains(str, '.')) { // leading 0, skip if is a decimal number
            if (chars[start + 1] == 'x' || chars[start + 1] == 'X') { // leading 0x/0X
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9')
                            && (chars[i] < 'a' || chars[i] > 'f')
                            && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
            if (Character.isDigit(chars[start + 1])) {
                // leading 0, but not hex, must be octal
                int i = start + 1;
                for (; i < chars.length; i++) {
                    if (chars[i] < '0' || chars[i] > '7') {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwards
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || i < sz + 1 && allowSigns && !foundDigit) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns
                    && (chars[i] == 'd'
                    || chars[i] == 'D'
                    || chars[i] == 'f'
                    || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l'
                    || chars[i] == 'L') {
                // not allowing L with an exponent or decimal point
                return foundDigit && !hasExp && !hasDecPoint;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }

    public BigDecimal createBigDecimal(final String str) {
        if (str == null) {
            return null;
        }
        // handle JDK1.3.1 bug where "" throws IndexOutOfBoundsException
        if (StringUtils.isBlank(str)) {
            throw new NumberFormatException("A blank string is not a valid number");
        }
        return new BigDecimal(str);
    }

    public BigInteger createBigInteger(final String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            throw new NumberFormatException("An empty string is not a valid number");
        }
        int pos = 0; // offset within string
        int radix = 10;
        boolean negate = false; // need to negate later?
        final char char0 = str.charAt(0);
        if (char0 == '-') {
            negate = true;
            pos = 1;
        } else if (char0 == '+') {
            pos = 1;
        }
        if (str.startsWith("0x", pos) || str.startsWith("0X", pos)) { // hex
            radix = 16;
            pos += 2;
        } else if (str.startsWith("#", pos)) { // alternative hex (allowed by Long/Integer)
            radix = 16;
            pos++;
        } else if (str.startsWith("0", pos) && str.length() > pos + 1) { // octal; so long as there are additional digits
            radix = 8;
            pos++;
        } // default is to treat as decimal

        final BigInteger value = new BigInteger(str.substring(pos), radix);
        return negate ? value.negate() : value;
    }

    public Double createDouble(final String str) {
        if (str == null) {
            return null;
        }
        return Double.valueOf(str);
    }

    public Float createFloat(final String str) {
        if (str == null) {
            return null;
        }
        return Float.valueOf(str);
    }

    public Integer createInteger(final String str) {
        if (str == null) {
            return null;
        }
        // decode() handles 0xAABD and 0777 (hex and octal) as well.
        return Integer.decode(str);
    }

    public Long createLong(final String str) {
        if (str == null) {
            return null;
        }
        return Long.decode(str);
    }

    private static String getMantissa(final String str, final int stopPos) {
        final char firstChar = str.charAt(0);
        final boolean hasSign = firstChar == '-' || firstChar == '+';
        final int length = str.length();
        if (length <= (hasSign ? 1 : 0) || length < stopPos) {
            throw new NumberFormatException(str + " is not a valid number.");
        }
        return hasSign ? str.substring(1, stopPos) : str.substring(0, stopPos);
    }

    public static boolean isDigits(final String str) {
        return StringUtils.isNumeric(str);
    }

    private static boolean isAllZeros(final String str) {
        if (str == null) {
            return true;
        }
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }

    private static boolean isZero(final String mant, final String dec) {
        return isAllZeros(mant) && isAllZeros(dec);
    }

    public Number createNumber(final String str) {
        if (str == null) {
            return null;
        }
        if (StringUtils.isBlank(str)) {
            throw new NumberFormatException("A blank string is not a valid number");
        }
        // Need to deal with all possible hex prefixes here
        final String[] hexPrefixes = {"0x", "0X", "#"};
        final int length = str.length();
        final int offset = str.charAt(0) == '+' || str.charAt(0) == '-' ? 1 : 0;
        int pfxLen = 0;
        for (final String pfx : hexPrefixes) {
            if (str.startsWith(pfx, offset)) {
                pfxLen += pfx.length() + offset;
                break;
            }
        }
        if (pfxLen > 0) { // we have a hex number
            char firstSigDigit = 0; // strip leading zeroes
            for (int i = pfxLen; i < length; i++) {
                firstSigDigit = str.charAt(i);
                if (firstSigDigit != '0') {
                    break;
                }
                pfxLen++;
            }
            final int hexDigits = length - pfxLen;
            if (hexDigits > 16 || hexDigits == 16 && firstSigDigit > '7') { // too many for Long
                return createBigInteger(str);
            }
            if (hexDigits > 8 || hexDigits == 8 && firstSigDigit > '7') { // too many for an int
                return createLong(str);
            }
            return createInteger(str);
        }
        final char lastChar = str.charAt(length - 1);
        final String mant;
        final String dec;
        final String exp;
        final int decPos = str.indexOf('.');
        final int expPos = str.indexOf('e') + str.indexOf('E') + 1; // assumes both not present
        // if both e and E are present, this is caught by the checks on expPos (which prevent IOOBE)
        // and the parsing which will detect if e or E appear in a number due to using the wrong offset

        // Detect if the return type has been requested
        final boolean requestType = !Character.isDigit(lastChar) && lastChar != '.';
        if (decPos > -1) { // there is a decimal point
            if (expPos > -1) { // there is an exponent
                if (expPos <= decPos || expPos > length) { // prevents double exponent causing IOOBE
                    throw new NumberFormatException(str + " is not a valid number.");
                }
                dec = str.substring(decPos + 1, expPos);
            } else {
                // No exponent, but there may be a type character to remove
                dec = str.substring(decPos + 1, requestType ? length - 1 : length);
            }
            mant = getMantissa(str, decPos);
        } else {
            if (expPos > -1) {
                if (expPos > length) { // prevents double exponent causing IOOBE
                    throw new NumberFormatException(str + " is not a valid number.");
                }
                mant = getMantissa(str, expPos);
            } else {
                // No decimal, no exponent, but there may be a type character to remove
                mant = getMantissa(str, requestType ? length - 1 : length);
            }
            dec = null;
        }
        if (requestType) {
            if (expPos > -1 && expPos < length - 1) {
                exp = str.substring(expPos + 1, length - 1);
            } else {
                exp = null;
            }
            //Requesting a specific type.
            final String numeric = str.substring(0, length - 1);
            switch (lastChar) {
                case 'l' :
                case 'L' :
                    if (dec == null
                            && exp == null
                            && (!numeric.isEmpty() && numeric.charAt(0) == '-' && isDigits(numeric.substring(1)) || isDigits(numeric))) {
                        try {
                            return createLong(numeric);
                        } catch (final NumberFormatException ignored) {
                            // Too big for a long
                        }
                        return createBigInteger(numeric);

                    }
                    throw new NumberFormatException(str + " is not a valid number.");
                case 'f' :
                case 'F' :
                    try {
                        final Float f = createFloat(str);
                        if (!(f.isInfinite() || f.floatValue() == 0.0F && !isZero(mant, dec))) {
                            //If it's too big for a float or the float value = 0 and the string
                            //has non-zeros in it, then float does not have the precision we want
                            return f;
                        }

                    } catch (final NumberFormatException ignored) {
                        // ignore the bad number
                    }
                    //$FALL-THROUGH$
                case 'd' :
                case 'D' :
                    try {
                        final Double d = createDouble(str);
                        if (!(d.isInfinite() || d.doubleValue() == 0.0D && !isZero(mant, dec))) {
                            return d;
                        }
                    } catch (final NumberFormatException ignored) {
                        // ignore the bad number
                    }
                    try {
                        return createBigDecimal(numeric);
                    } catch (final NumberFormatException ignored) {
                        // ignore the bad number
                    }
                    //$FALL-THROUGH$
                default :
                    throw new NumberFormatException(str + " is not a valid number.");

            }
        }
        //User doesn't have a preference on the return type, so let's start
        //small and go from there...
        if (expPos > -1 && expPos < length - 1) {
            exp = str.substring(expPos + 1);
        } else {
            exp = null;
        }
        if (dec == null && exp == null) { // no decimal point and no exponent
            //Must be an Integer, Long, Biginteger
            try {
                return createInteger(str);
            } catch (final NumberFormatException ignored) {
                // ignore the bad number
            }
            try {
                return createLong(str);
            } catch (final NumberFormatException ignored) {
                // ignore the bad number
            }
            return createBigInteger(str);
        }

        //Must be a Float, Double, BigDecimal
        try {
            final Float f = createFloat(str);
            final Double d = createDouble(str);
            if (!f.isInfinite()
                    && !(f.floatValue() == 0.0F && !isZero(mant, dec))
                    && f.toString().equals(d.toString())) {
                return f;
            }
            if (!d.isInfinite() && !(d.doubleValue() == 0.0D && !isZero(mant, dec))) {
                final BigDecimal b = createBigDecimal(str);
                if (b.compareTo(BigDecimal.valueOf(d.doubleValue())) == 0) {
                    return d;
                }
                return b;
            }
        } catch (final NumberFormatException ignored) {
            // ignore the bad number
        }
        return createBigDecimal(str);
    }
}
