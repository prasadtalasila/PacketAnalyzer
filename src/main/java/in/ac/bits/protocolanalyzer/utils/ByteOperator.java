package in.ac.bits.protocolanalyzer.utils;

/**
 * The class contains utility methods for converting byte range into chosen primitive data types (int, short, long etc.).
 *
 * @author Shilpa Raju
 * @author crygnus
 * @version 21-Oct-2017
 */

public class ByteOperator {

/**
 * This method converts the given byte array into an integer
 *
 * @param bytes byte array to be converted to int
 * @return integer representing the byte array
 * @throws ArrayIndexOutOfBoundsException if the byte array length is over 4
 */

    public static int parseBytesint(byte[] bytes)
            throws ArrayIndexOutOfBoundsException {
        if (bytes.length > 4) {
            throw new ArrayIndexOutOfBoundsException(
                    "Byte array length exceeds 4!");
        } else {
            int val = 0;
            for (int i = 0; i < bytes.length; i++) {
                val = val << 8;
                val = val | bytes[i] & 0xFF;
            }
            return val;
        }
    }
    
/**
 * This method converts the given byte array into a single byte
 *
 * @param bytes byte array to be converted 
 * @return byte representing the byte array
 * @throws ArrayIndexOutOfBoundsException if the byte array length is over 1
 */

    public static byte parseBytesbyte(byte[] bytes)
            throws ArrayIndexOutOfBoundsException {
        if (bytes.length > 1) {
            throw new ArrayIndexOutOfBoundsException(
                    "Byte array length exceeds 4!");
        } else {
            int val = 0;
            for (int i = 0; i < bytes.length; i++) {
                val = val << 8;
                val = val | bytes[i] & 0xFF;
            }
            return (byte) val;
        }
    }
    
/**
 * This method converts the given byte array into type short int
 *
 * @param bytes byte array to be converted 
 * @return short int representing the byte array
 * @throws ArrayIndexOutOfBoundsException if the byte array length is over 2
 */

    public static short parseBytesshort(byte[] bytes)
            throws ArrayIndexOutOfBoundsException {
        if (bytes.length > 2) {
            throw new ArrayIndexOutOfBoundsException(
                    "Byte array length exceeds 4!");
        } else {
            int val = 0;
            for (int i = 0; i < bytes.length; i++) {
                val = val << 8;
                val = val | bytes[i] & 0xFF;
            }
            return (short) val;
        }
    }
/**
 * This method converts the given byte array into a long int
 *
 * @param bytes byte array to be converted 
 * @return long int representing the byte array
 * @throws ArrayIndexOutOfBoundsException if the byte array length is over 8
 */

    public static long parseByteslong(byte[] bytes) {
        if (bytes.length > 8) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            long val = 0l;
            for (int i = 0; i < bytes.length; i++) {
                val = val << 8;
                val = val | bytes[i] & 0xFF;
            }
            return val;
        }
    }

}
