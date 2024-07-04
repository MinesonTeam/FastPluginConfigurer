package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;

@UtilityClass
public class NumberUtil {
    private static final String IS_NOT_A_VALID_NUMBER = " is not a valid number.";

    public boolean isCreatable(final String string) {
        if (StringUtil.isEmpty(string)) {
            return false;
        }
        final char[] chars = string.toCharArray();
        int charsLength = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = chars[0] == '-' || chars[0] == '+' ? 1 : 0;
        // leading 0, skip if is a decimal number
        if (charsLength > start + 1 && chars[start] == '0' && !StringUtil.contains(string, '.')) {
            // leading 0x/0X
            if (chars[start + 1] == 'x' || chars[start + 1] == 'X') {
                if (start + 2 == charsLength) {
                    // string == "0x"
                    return false;
                }
                // checking hex (it can't be anything else)
                for (int i = start + 2; i < chars.length; i++) {
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
                for (int i = start + 1; i < chars.length; i++) {
                    if (chars[i] < '0' || chars[i] > '7') {
                        return false;
                    }
                }
                return true;
            }
        }
        // don't want to loop to the last char, check it afterwards
        charsLength--;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (start < charsLength || start < charsLength + 1 && allowSigns && !foundDigit) {
            if (chars[start] >= '0' && chars[start] <= '9') {
                foundDigit = true;
                allowSigns = false;
            } else if (chars[start] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[start] == 'e' || chars[start] == 'E') {
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
            } else if (chars[start] == '+' || chars[start] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                // we need a digit after the E
                foundDigit = false;
            } else {
                return false;
            }
            start++;
        }
        if (start < chars.length) {
            if (chars[start] >= '0' && chars[start] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[start] == 'e' || chars[start] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[start] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns
                    && (chars[start] == 'd'
                    || chars[start] == 'D'
                    || chars[start] == 'f'
                    || chars[start] == 'F')) {
                return foundDigit;
            }
            if (chars[start] == 'l'
                    || chars[start] == 'L') {
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

    public BigDecimal createBigDecimal(final String string) {
        if (string == null) {
            return null;
        }
        // handle JDK1.3.1 bug where "" throws IndexOutOfBoundsException
        if (StringUtil.isBlank(string)) {
            throw new NumberFormatException("A blank string is not a valid number");
        }
        return new BigDecimal(string);
    }

    public BigInteger createBigInteger(final String string) {
        if (string == null) {
            return null;
        }
        if (string.isEmpty()) {
            throw new NumberFormatException("An empty string is not a valid number");
        }
        int pos = 0; // offset within string
        int radix = 10;
        boolean negate = false; // need to negate later?
        final char char0 = string.charAt(0);
        if (char0 == '-') {
            negate = true;
            pos = 1;
        } else if (char0 == '+') {
            pos = 1;
        }
        if (string.startsWith("0x", pos) || string.startsWith("0X", pos)) { // hex
            radix = 16;
            pos += 2;
        } else if (string.startsWith("#", pos)) { // alternative hex (allowed by Long/Integer)
            radix = 16;
            pos++;
        } else if (string.startsWith("0", pos) && string.length() > pos + 1) { // octal; so long as there are additional digits
            radix = 8;
            pos++;
        } // default is to treat as decimal

        final BigInteger value = new BigInteger(string.substring(pos), radix);
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
            throw new NumberFormatException(str + IS_NOT_A_VALID_NUMBER);
        }
        return hasSign ? str.substring(1, stopPos) : str.substring(0, stopPos);
    }

    public static boolean isDigits(final String str) {
        return StringUtil.isNumeric(str);
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
        } else if (StringUtil.isBlank(str)) {
            throw new NumberFormatException("A blank string is not a valid number");
        }
        // Need to deal with all possible hex prefixes here
        final int offset = str.charAt(0) == '+' || str.charAt(0) == '-' ? 1 : 0;
        int pfxLen = 0;
        for (final String pfx : new String[]{"0x", "0X", "#"}) {
            if (str.startsWith(pfx, offset)) {
                pfxLen += pfx.length() + offset;
                break;
            }
        }
        if (pfxLen > 0) { // we have a hex number
            char firstSigDigit = 0; // strip leading zeroes
            for (int i = pfxLen; i < str.length(); i++) {
                firstSigDigit = str.charAt(i);
                if (firstSigDigit != '0') {
                    break;
                }
                pfxLen++;
            }
            final int hexDigits = str.length() - pfxLen;
            if (hexDigits > 16 || hexDigits == 16 && firstSigDigit > '7') { // too many for Long
                return createBigInteger(str);
            } else if (hexDigits > 8 || hexDigits == 8 && firstSigDigit > '7') { // too many for an int
                return createLong(str);
            }
            return createInteger(str);
        }
        final int decPos = str.indexOf('.');
        final int expPos = str.indexOf('e') + str.indexOf('E') + 1; // assumes both not present
        // if both e and E are present, this is caught by the checks on expPos (which prevent IOOBE)
        // and the parsing which will detect if e or E appear in a number due to using the wrong offset
        // Detect if the return type has been requested
        final char lastChar = str.charAt(str.length() - 1);
        final boolean requestType = !Character.isDigit(lastChar) && lastChar != '.';
        // there is a decimal point
        final String dec;
        final String mantissa;
        int endIndex = requestType ? str.length() - 1 : str.length();
        if (decPos > -1) {
            // there is an exponent
            if (expPos > -1) {
                // prevents double exponent causing IOOBE
                if (expPos <= decPos || expPos > str.length()) {
                    throw new NumberFormatException(str + IS_NOT_A_VALID_NUMBER);
                }
                dec = str.substring(decPos + 1, expPos);
            } else {
                // No exponent, but there may be a type character to remove
                dec = str.substring(decPos + 1, endIndex);
            }
            mantissa = getMantissa(str, decPos);
        } else {
            if (expPos > -1) {
                // prevents double exponent causing IOOBE
                if (expPos > str.length()) {
                    throw new NumberFormatException(str + IS_NOT_A_VALID_NUMBER);
                }
                mantissa = getMantissa(str, expPos);
            } else {
                // No decimal, no exponent, but there may be a type character to remove
                mantissa = getMantissa(str, endIndex);
            }
            dec = null;
        }
        final String exp;
        if (requestType) {
            if (expPos > -1 && expPos < str.length() - 1) {
                exp = str.substring(expPos + 1, str.length() - 1);
            } else {
                exp = null;
            }
            //Requesting a specific type.
            final String numeric = str.substring(0, str.length() - 1);
            switch (lastChar) {
                case 'l':
                case 'L':
                    if (dec == null && exp == null && (!numeric.isEmpty() && numeric.charAt(0) == '-' && isDigits(numeric.substring(1)) || isDigits(numeric))) {
                        try {
                            return createLong(numeric);
                        } catch (final NumberFormatException ignored) {
                            return createBigInteger(numeric);
                        }

                    }
                    throw new NumberFormatException(str + IS_NOT_A_VALID_NUMBER);
                case 'f':
                case 'F':
                    try {
                        final Float createdFloat = createFloat(str);
                        if (!(createdFloat.isInfinite() || createdFloat == 0.0F && !isZero(mantissa, dec))) {
                            //If it's too big for a float or the float value = 0 and the string
                            //has non-zeros in it, then float does not have the precision we want
                            return createdFloat;
                        }

                    } catch (final NumberFormatException ignored) {
                        // ignore the bad number
                    }
                    //$FALL-THROUGH$
                case 'd':
                case 'D':
                    try {
                        final Double createdDouble = createDouble(str);
                        if (!(createdDouble.isInfinite() || createdDouble == 0.0D && !isZero(mantissa, dec))) {
                            return createdDouble;
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
                    throw new NumberFormatException(str + IS_NOT_A_VALID_NUMBER);

            }
        }
        //User doesn't have a preference on the return type, so let's start
        //small and go from there...
        if (expPos > -1 && expPos < str.length() - 1) {
            exp = str.substring(expPos + 1);
        } else {
            exp = null;
        }
        // no decimal point and no exponent
        if (dec == null && exp == null) {
            // Must be an Integer, Long, Biginteger
            try {
                return createInteger(str);
            } catch (final NumberFormatException ignored) {
                try {
                    return createLong(str);
                } catch (final NumberFormatException ignored2) {
                    return createBigInteger(str);
                }
            }
        }
        // Must be a Float, Double, BigDecimal
        try {
            final Float createdFloat = createFloat(str);
            final Double createdDouble = createDouble(str);
            if (!createdFloat.isInfinite() && !(createdFloat == 0.0F && !isZero(mantissa, dec)) && createdFloat.toString().equals(createdDouble.toString())) {
                return createdFloat;
            } else if (!createdDouble.isInfinite() && !(createdDouble == 0.0D && !isZero(mantissa, dec))) {
                final BigDecimal bigDecimal = createBigDecimal(str);
                if (bigDecimal.compareTo(BigDecimal.valueOf(createdDouble)) == 0) {
                    return createdDouble;
                }
                return bigDecimal;
            }
        } catch (final NumberFormatException ignored) {
            // ignore the bad number
        }
        return createBigDecimal(str);
    }
}
