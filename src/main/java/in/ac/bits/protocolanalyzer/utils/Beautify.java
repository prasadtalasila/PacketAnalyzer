package in.ac.bits.protocolanalyzer.utils;

import org.apache.commons.codec.binary.Hex;

/**
 * This class converts the given address into string format.
 * @author Shilpa Raju
 * @author crygnus
 * @version 21-Oct-2017
 */

public class Beautify {

/**
 * This method converts the array of bytes passed to it into string form. 
 * @return The converted string.
 * @param bytes This is the series of bytes which must be converted to a string.
 * @param mode This is the mode of the address.
 */

    public static String beautify(byte[] bytes, String mode)
            throws IllegalArgumentException {

        if (mode.equalsIgnoreCase("ip4")) {
            if (bytes.length != 4) {
                return "INVALID-ADDRESS";
            } else {
                String[] address = new String[4];
                for (int i = 0; i < bytes.length; i++) {
                    // to convert to an unsigned byte, operate bitwise & with
                    // 0xFF
                    address[i] = String.valueOf(bytes[i] & 0xFF);
                }
                return address[0] + "." + address[1] + "." + address[2] + "."
                        + address[3];
            }
        } else if (mode.equalsIgnoreCase("hex")) {
            return Hex.encodeHexString(bytes);

        } else if (mode.equalsIgnoreCase("hex2")) {
            String hexString = Hex.encodeHexString(bytes);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < hexString.length(); i += 2) {
                builder.append(hexString.substring(i, i + 2));
                builder.append(':');
            }
            builder.setLength(builder.length() - 1);
            return builder.toString();

        } else if (mode.equalsIgnoreCase("hex4")) {
            String hexString = Hex.encodeHexString(bytes);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < hexString.length(); i += 4) {
                builder.append(hexString.substring(i, i + 4));
                builder.append(':');
            }
            builder.setLength(builder.length() - 1);
            return builder.toString();
        } else {
            throw new IllegalArgumentException("The mode: " + mode
                    + " is not supported for beautification!");
        }

    }
}
