import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;


public class Encrypto {

    private static String TRIPLE_DES_TRANSFORMATION = "DESede/ECB/PKCS7Padding";
    private static String ALGORITHM = "DESede";
    private static String BOUNCY_CASTLE_PROVIDER = "BC";
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String PASSWORD_HASH_ALGORITHM = "SHA";

    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /*
     * To do : encrypt plaintext using 3Des algorithm
     */
    private static byte[] encode(byte[] input, String key) throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
        init();
        Cipher encrypter = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION, BOUNCY_CASTLE_PROVIDER);

        encrypter.init(Cipher.ENCRYPT_MODE, buildKey(key.toCharArray()));
        //encrypt
        return encrypter.doFinal(input);
    }

    /*
     * To do : decrypt plaintext using 3Des algorithm
     */
    private static byte[] decode(byte[] input, String key) throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
        init();
        Cipher decrypter = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION, BOUNCY_CASTLE_PROVIDER);
        //hash key to sha, and init decrypter
        decrypter.init(Cipher.DECRYPT_MODE, buildKey(key.toCharArray()));
        //decrypt
        return decrypter.doFinal(input);
    }

    private static Key buildKey(char[] password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        init();
        MessageDigest digester = MessageDigest.getInstance(PASSWORD_HASH_ALGORITHM);
        digester.update(String.valueOf(password).getBytes(UNICODE_FORMAT));
        byte[] key = digester.digest();

        //3des key using 24 byte, convert to 24 byte
        byte[] keyDes = Arrays.copyOf(key, 24);
        SecretKeySpec spec = new SecretKeySpec(keyDes, ALGORITHM);
        return spec;
    }

    private static byte[] getByte(String string) throws UnsupportedEncodingException {
        return string.getBytes(UNICODE_FORMAT);
    }

    /*
     * to do : byte to String
     */
    private static String getString(byte[] byteText) {
        return new String(byteText);
    }


    public static void main(String[] args) {
        try {
            byte[] test = encode(getByte("IMANOL"),"hjsgfcgvhbjkn");
            System.out.println(getString(test));


            System.out.println("Decrypted:");

            byte[] test2 = decode(test,"hjsgfcgvhbjkn");
            System.out.println(getString(test2));
        } catch (IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
