package in.ac.bits.protocolanalyzer.utils;

import java.util.Arrays;

/**
 * This utility class allows for different bit operations. 
 * 
 * @author Shilpa Raju
 * @author crygnus
 * @version 21-Oct-2017
 */

public class BitOperator {

    /**
     * Returns the bit in byte b at the given index. Note that index must be
     * between 0-7
     * 
     * @param b This is the byte from which a particular bit has to be extracted.
     * @param index This gives the index of the bit to be extracted. 
     * @return Bit 0 or 1 
     * @throws ArrayIndexOutOfBoundsException if the index is not in the required range.
     */
    public static int getBit(byte b, int index)
            throws ArrayIndexOutOfBoundsException {

        if (index < 0 || index > 7) {
            throw new ArrayIndexOutOfBoundsException(
                    "Provided bit location is beyond the range 0-7");
        }
        return (b >> index) & 1;
    }

    /**
     * Converts the given byte to an integer array, such that the least significant bit is bits[0].
     * If the input is b = 113, the returned integer array will be (1,0,0,0,1,1,1,0).
     * 
     * @param b This is the byte to be converted.
     * @return Integer array representing the byte
     */
    public static int[] getBits(byte b) {
        int[] bits = new int[8];
        for (int i = 0; i < 8; i++) {
            bits[i] = getBit(b, i);
        }
        return bits;
    }

    /**
     * Returns int equivalent of numberOfBits number of least significant bits, left shifted by the number given by startBit
     * if b = 113, startBit = 3 and numberOfBits = 2, 
     * the returned value will be 8
     * 
     * @param b This is the byte from which values are obtained
     * @param startBit Start location
     * @param numberOfBits Length of bits required
     * @return integer equivalent of bit array
     * @throws ArrayIndexOutOfBoundsException if the bits specified are out of range
     */
    public static int getValue(byte b, int startBit, int numberOfBits)
            throws ArrayIndexOutOfBoundsException {

        if (startBit + numberOfBits > 8) {
            throw new ArrayIndexOutOfBoundsException(
                    "Relevant bits are beyond this byte!");
        } else {
            int val = 0;
            for (int i = 0; i < numberOfBits; i++) {
                val = val | (getBit(b, i) << i + startBit);
            }
            return val;
        }
    }

    /**
     * Returns integer equivalent of the nibble in byte b with given nibble
     * index (either 0 or 1). If nibbleIndex is 0, lower nibble is returned. If it is 1, upper nibble.
     * If b = 113, for nibbleIndex = 0, return value is 1. If nibbleIndex is 1, return value is 7.
     * 
     * @param b byte from which nibble is returned
     * @param nibbleIndex indicates which nibble is to be returned
     * @return integer equivalent of required nibble
     * @throws ArrayIndexOutOfBoundsException if nibbleIndex is anything but 0 or 1
     */
    public static int getNibble(byte b, int nibbleIndex)
            throws ArrayIndexOutOfBoundsException {
        if (nibbleIndex == 0) {
            return b & 0xF;
        } else if (nibbleIndex == 1) {
            return (b >> 4) & 0xF;
        } else {
            throw new ArrayIndexOutOfBoundsException(
                    "Nibble index can only be 0 or 1");
        }
    }
    
    /**
     * This method takes in a byte array and returns a smaller byte array depending on the parameters. 
     * It returns a subarray of the input which goes from the startBit specified to the endBit specified.
     * For example, if b = (113,100,60,45,90), startBit = 8 and endBit = 35, the output would be 100 60 45 5 
     * because three bytes would be returned as they are, as well as a small part of the next byte.
     *
     * @param header this is the byte array whose subarray is returned.
     * @param startBit This is the beginning of the smaller array returned.
     * @param endBit This is the end of the smaller array returned.
     * @throws IllegalArgumentException if startBit and endBit aren't within the range of header.
     * @return A byte array which is from startBit to endBt of header.
     */    
    

    public static byte[] parse(byte[] header, int startBit, int endBit)
            throws IllegalArgumentException {

        // check if start and end bits are within the range of header
        int byteStartBit = 0;
        int byteEndBit = header.length * 8 - 1;

        if (!lieBetween(startBit, byteStartBit, byteEndBit)
                || !lieBetween(endBit, byteStartBit, byteEndBit)) {
            throw new IllegalArgumentException(
                    "Either startBit or endBit is out of range of header bytes provided!!");

        }
        // check if bit boundaries are perfect byte boundaries. If yes then use
        // arrays.copy util function
        boolean sbBoundary = startBit % 8 == 0;
        boolean ebBoundary = (endBit + 1) % 8 == 0;

        if (sbBoundary && ebBoundary) {
            return Arrays.copyOfRange(header, startBit / 8, (endBit + 1) / 8);
        } else if (sbBoundary && !ebBoundary) {
            int fullByteStart = startBit / 8;
            int fullByteEnd = ((endBit + 1) - ((endBit + 1) % 8)) / 8;
            byte[] returnBytes = new byte[fullByteEnd - fullByteStart + 1];
            byte[] fullBytes = Arrays.copyOfRange(header, fullByteStart,
                    fullByteEnd);
            for (int i = 0; i < fullBytes.length; i++) {
                returnBytes[i] = fullBytes[i];
            }
            returnBytes[returnBytes.length - 1] = bitToByte(header,
                    fullByteStart, fullByteEnd, (endBit + 1) % 8, false);
            return returnBytes;
        } else if (!sbBoundary && ebBoundary) {
            int fullByteEnd = (endBit + 1) / 8;
            int lowerBits = 8 - (startBit % 8);
            int fullByteStart = (startBit + lowerBits) / 8;
            byte[] returnBytes = new byte[fullByteEnd - fullByteStart + 1];
            byte[] fullBytes = Arrays.copyOfRange(header, fullByteStart,
                    fullByteEnd);
            for (int i = 0; i < fullBytes.length; i++) {
                returnBytes[i + 1] = fullBytes[i];
            }
            returnBytes[0] = bitToByte(header, fullByteStart, fullByteEnd,
                    startBit % 8, true);
            return returnBytes;
        } else {
            int lowerBits = 8 - (startBit % 8);
            int fullByteStart = (startBit + lowerBits) / 8;
            int fullByteEnd = ((endBit + 1) - ((endBit + 1) % 8)) / 8;
            byte[] returnBytes = new byte[fullByteEnd - fullByteStart + 2];
            byte[] fullBytes = Arrays.copyOfRange(header, fullByteStart,
                    fullByteEnd);
            for (int i = 0; i < fullBytes.length; i++) {
                returnBytes[i + 1] = fullBytes[i];
            }
            returnBytes[0] = bitToByte(header, fullByteStart, fullByteEnd,
                    startBit % 8, true);
            returnBytes[returnBytes.length - 1] = bitToByte(header,
                    fullByteStart, fullByteEnd, (endBit + 1) % 8, false);
            return returnBytes;
        }

    }
    
    /**
     * This method returns a modified byte from the byte array entered, based on the input parameters. 
     * If reverse is 0, the method returns the byte at index higher, right shifted by (8 - extraBits) number of bits.
     * If reverse is 1, the method returns the byte index lower-1, after making the upper extraBits number of bits 0.
     * 
     * @param original This is the array from which the byte is taken.
     * @param lower This is one of the indices from which the byte is returned.
     * @param higher This is another one of the indices for which byte is returned.
     * @param extraBits This specifies how the byte is manipulated.
     * @param reverse This also specifies how the byte is manipulated. 
     * @return the manipulated byte value.
     */

    private static byte bitToByte(byte[] original, int lower, int higher,
            int extraBits, boolean reverse) {
        if (!reverse) {
            // extra bits after higher boundary
            byte[] b = Arrays.copyOfRange(original, higher, higher + 1);
            int val = b[0];
            val = val >> (8 - extraBits);
            return (byte) val;
        } else {
            // extra bits before lower boundary
            byte[] b = Arrays.copyOfRange(original, lower - 1, lower);
            int val = b[0];
            val = val << (extraBits) & 0xFF;
            val = val >> (extraBits);
            return (byte) val;
        }
    }
    
    /**
     * Checks if the target lies between the lowerLimit and UpperLimit
     * 
     * @param target This is the value that must lie between the given values
     * @param lowerLimit target must be above this.
     * @param upperLimit target must be below this.
     * @return true if target lies between the two numbers, otherwise false
     */

    private static boolean lieBetween(int target, int lowerLimit,
            int upperLimit) {
        if (target < lowerLimit || target > upperLimit) {
            return false;
        }
        return true;
    }
}
