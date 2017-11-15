package in.ac.bits.protocolanalyzer.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class contains wrapper functions around cryptographic algorithms.
 *
 * @author Shilpa Raju
 * @author crygnus
 * @version 21-Oct-2017
 */

public class Security {

    /**
     * This field stores the string "SHA-256"
     */

    public static final String SHA256_ALGORITHM = "SHA-256";
    
/**
 * This method is used to get the hash computation of a MessageDigest object that implements the SHA-256 digest algorithm.
 * @param str This is the String which is to be operated upon.
 * @return Gives the result in string form
 */

    public static String createHash(String str) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(SHA256_ALGORITHM);
            md.update(str.getBytes("UTF-8"));
            byte[] digest = md.digest();
            String hash = String.format("%064x",
                    new java.math.BigInteger(1, digest));
            return hash;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
