import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

/**
 * Utility class
 * Implementation based on material from lecture.
 */
public class RSA {
    static private BigInteger p;
    static private BigInteger q;
    static private BigInteger n;
    static private BigInteger phiN;
    static private BigInteger e;
    static private BigInteger d;
    static private KeyPair publicKey;
    static private KeyPair privateKey;

    /**
     * Generate rsa keys.
     *
     * @param fileName the file name is used to save keys under proper name,
     *                 redistribute later when calling other functions
     */
    public static void generateRSAKeys(String fileName) {
        SecureRandom rand = new SecureRandom();
        int bitLength = 2048;
        p = new BigInteger(bitLength / 2, 100, rand);
        q = new BigInteger(bitLength / 2, 100, rand);
        n = p.multiply(q);
        phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger("3");
        while (phiN.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }

        d = e.modInverse(phiN);
        publicKey = new KeyPair(e, n);
        privateKey = new KeyPair(d, n);
        saveKey(fileName + "_pub.key", publicKey);
        saveKey(fileName + "_priv.key", privateKey);
    }

    /**
     * Encrypt file.
     *
     * @param fileName the file name of a .txt file
     * @param key      the key
     */
    public static void encryptFile(String fileName, KeyPair key) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(fileName);
                byte[] buffer = new byte[10];
                StringBuilder sb = new StringBuilder();
                while (fileIn.read(buffer) != -1 ) {
                    sb.append(new String(buffer));
                    buffer = new byte[10];
                }
                fileIn.close();
                String content = sb.toString();
                String encryptedStr = (new BigInteger(content.getBytes())).modPow(key.getKey(), key.getN()).toString();
                FileWriter writer = new FileWriter(fileName);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(encryptedStr);
                bufferedWriter.close();
                System.out.println("Contents of " + fileName + " have been encrypted");
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        } else {
            System.out.println("File with name " + fileName + " doesn't exist\n");
        }
    }

    /**
     * Decrypt file.
     *
     * @param fileName the file name
     * @param key      the key
     */
    public static void decryptFile(String fileName, KeyPair key){
        File file = new File(fileName);
        BigInteger n = null;
        if (file.exists()){
            try {
                Scanner scan = new Scanner(file);
                while(scan.hasNext()) {
                    n = scan.nextBigInteger();
                }
                scan.close();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        } else {
            System.out.println("File with name " + fileName + " doesn't exist\n");
        }
        String decryptedMsg = new String(n.modPow(key.getKey(), key.getN()).toByteArray());
        try {
            FileWriter writer = new FileWriter(fileName);
            BufferedWriter bf = new BufferedWriter(writer);
            bf.write(decryptedMsg);
            bf.close();
            System.out.println("Content of the text file has been decrypted");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static void saveKey(String fileName, KeyPair key) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(key);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read key key pair.
     *
     * @param fileName the file name
     * @return the key pair
     */
    public static KeyPair readKey(String fileName) {
        KeyPair key = null;
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            key = (KeyPair) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * Encrypt string.
     *
     * @param msg the msg
     * @param key the key
     * @return the encrypted string
     */
    public static String encrypt(String msg, KeyPair key) {
        return (new BigInteger(msg.getBytes())).modPow(key.getKey(), key.getN()).toString();
    }

    /**
     * Decrypt string.
     *
     * @param msg the msg
     * @param key the key
     * @return the decrypted string
     */
    public static String decrypt(String msg, KeyPair key) {
        return new String((new BigInteger(msg)).modPow(key.getKey(), key.getN()).toByteArray());
    }


}
