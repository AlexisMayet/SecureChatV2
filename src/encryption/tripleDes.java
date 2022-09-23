package encryption;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;

public class tripleDes{

//    private static String inputFilePath = ".json";

    public static final String DES_ENCRYPTION_SCHEME = "DES/TripleDES/CBC/PKCS5Padding";


    // Initialize a new Secret Key Spec for the server.
    private SecretKeySpec secretKeySpec;

    // Initialize a generator for the secret key.
    private SecretKeyFactory secretGenerator;

    // Initialize a cipher to encrypt the file.
    private Cipher cipher;

    // Initialize a byte array. This will be the secret key.
    byte[] keyInBytes;

    // Initialize Encryption key and encryption key scheme.
    private String encryptionKey;
    private String encryptionKeyScheme;

    // Initialize a secret key.
    SecretKey Key;

    // Generate a constructor for the encryption.tripleDes Class.
    public tripleDes() throws Exception{

        encryptionKey = "This is a secret";
        encryptionKeyScheme = DES_ENCRYPTION_SCHEME;

        keyInBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);

        // Generate a new Secret Key Spec for the server.
        byte[] secretKey = keyInBytes;

        // TripleDES is one of Java's integrated security algorithms.
        secretKeySpec = new SecretKeySpec(secretKey, "TripleDES");

        // Generate a new instance encryption scheme.
        secretGenerator = SecretKeyFactory.getInstance(encryptionKeyScheme);

        // Generate a new cipher.
        cipher = Cipher.getInstance(encryptionKeyScheme);

        // Generate a secret key using SecretKeyFactory.
        Key = secretGenerator.generateSecret(secretKeySpec);

    }

    /** Getter method that Encrypts the key String */

    public String encrypt(String plain){
        // Initialize a new encrypted string and set it to null.
        String encryptedStr = null;

        // Use Try Catch to check for correctness of key, using the cipher.

        /** TODO: Fix the IllegalBlockSizeException and BadPaddingException. I tried looking for solutions but it has something to do with using cipher without using UTF_8 format.
         *  Check this link out, perhaps you can find a solution to this: https://stackoverflow.com/questions/30383736/illegalblocksizeexception-when-trying-to-encrypt-and-decrypt-a-string-with-aes
         */
        try{
            cipher.init(Cipher.ENCRYPT_MODE,Key );

            byte[] plainText  = plain.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedText = cipher.doFinal(plainText);

            // Wrap (encrypt) the byte string in a vector Parameter class.
            encryptedStr = Base64.getEncoder().encodeToString(encryptedText);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return encryptedStr;
    }

    /** Getter method that Decrypts the key String */

    public String decrypt(String encryption){
        String decryptedText = null;

        try{
            cipher.init(Cipher.DECRYPT_MODE,Key);

            byte[] encryptedText = Base64.getDecoder().decode(encryption);
            byte[] plainText = cipher.doFinal(encryptedText);

            decryptedText = Base64.getEncoder().encodeToString(plainText);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    /** Setter method that encrypts or decrypts given json files
     *  Source: https://www.youtube.com/watch?v=zn_kg55GRWo
     */
    public static void encryptionAndDecryption(String key, int cipherMode, File input, File output) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {

        FileInputStream filesIn = new FileInputStream(input);
        FileOutputStream filesOut = new FileOutputStream(output);

        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));

        SecretKeyFactory generator = SecretKeyFactory.getInstance("TripleDES");
        SecretKey secretKey = generator.generateSecret(desKeySpec);

        Cipher tripleCipher = Cipher.getInstance("DES/TripleDES/CBC/PKCS5Padding");

        // When using CBC protocol, another parameter called the initialization vector is required.
        // Create an 8-byte array of random values. this will be the initialize vector.
        byte[] iv = new byte[8];

        // Instantiate the vector spec for the iv.
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        if (cipherMode == Cipher.ENCRYPT_MODE) {
            // SHA1 is an integrated hash algorithm and PRNG is a pseudo random number generator.
            // The PRNG is the source of randomness.
            tripleCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec, SecureRandom.getInstance("SHA1PRNG"));
            CipherInputStream cipherinputs = new CipherInputStream(filesIn, tripleCipher);
            write(cipherinputs, filesOut);

        } else if (cipherMode == Cipher.DECRYPT_MODE) {
            tripleCipher.init(Cipher.DECRYPT_MODE,secretKey,ivSpec,SecureRandom.getInstance("SHA1PRNG"));
            CipherOutputStream cipheroutputs = new CipherOutputStream(filesOut, tripleCipher);
            write(filesIn, cipheroutputs);
        }
    }

    /** Setter method that reads and writes given json files in byte format. */

    private static void write(InputStream in, OutputStream out) throws IOException{
        // Create a buffer that stores an allocated number of bytes
        byte[] buffer = new byte[64];
        int byteCount;

        // Read the input file in the while loop. Count the bytes. Feed the buffer with the input stream.
        while((byteCount = in.read(buffer)) != -1){
            // Parse the bytes to the output stream.
            out.write(buffer, 0, byteCount);
        }
        out.close();
        in.close();
    }

    public static void main(String[] args) throws Exception{
        // Instantiate a plaintext File
        File plain = new File("idkwhatgoeshere.json");

        // Instantiate an encrypted File
        File encryptedFile = new File("samehere.json");

        // key must have 8 bytes
        try{
            encryptionAndDecryption("00000000", Cipher.ENCRYPT_MODE, plain, encryptedFile);
            System.out.println(" File successfully encrypted.");
        }catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException| IOException | InvalidAlgorithmParameterException e){
            e.printStackTrace();
        }

        // Instantiate a decrypted File
        File decryptedFile = new File("samehere.json");

        try{
            encryptionAndDecryption("00000000", Cipher.DECRYPT_MODE, encryptedFile, decryptedFile);
            System.out.println(" File successfully decrypted.");
        }catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException| IOException e){
            e.printStackTrace();
        }

    }

}
