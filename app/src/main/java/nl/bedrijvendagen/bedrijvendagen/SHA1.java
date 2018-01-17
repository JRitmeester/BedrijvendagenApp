package nl.bedrijvendagen.bedrijvendagen;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 {

    private final static String HEX = "0123456789ABCDEF";

    public static String hash(String text) {
        // SHA-1 Hashing from: https://codebutchery.wordpress.com/2014/08/27/how-to-get-the-sha1-hash-sum-of-a-string-in-android/
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("UTF-8"), 0, text.length());
            byte[] sha1hash = md.digest();
            return toHex(sha1hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        int l = buf.length;
        StringBuffer result = new StringBuffer(2 * l);

        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f))
                .append(HEX.charAt(b & 0x0f));
    }

}
