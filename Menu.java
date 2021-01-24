import java.io.File;
import java.util.Scanner;

/**
 * Driver class with options menu and all functional functions
 */
public class Menu {
    /**
     * The Public key. Variable initialise upon calling generateKeys() and readKeys()
     */
    static KeyPair publicKey = null;
    /**
     * The Private key. Variable initialise upon calling generateKeys() and readKeys()
     */
    static KeyPair privateKey = null;
    /**
     * The User name. Variable initialise on start of the program or when changing user. Re-initialising of keys
     * is then necessary.
     */
    static String userName = null;

    /**
     * The entry point of application.
     * <p>
     * * Some minor issues with Scanner, won't assign new values if I tried use the global 'scn' value,
     * * that's why call for 'new Scanner' for every user input
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        System.out.print("\n\t\tWelcome to RSA Encryption Program\n" +
                "======================================================================================\n" +
                "If you are a new user provide a username, if you're coming back use your existing one.\n");
        System.out.println("Please enter username for your key files:");
        userName = new Scanner(System.in).nextLine();
        while (true) {
            System.out.println("Available options:\n" +
                    "1. Generate keys\n" +
                    "2. Read in your saved keys\n" +
                    "3. Encrypt a message\n" +
                    "4. Decrypt a message\n" +
                    "5. Encrypt a text file\n" +
                    "6. Decrypt a text file\n" +
                    "9. Change user\n" +
                    "0. Exit\n" +
                    "==================================================================");
            int input = scn.nextInt();
            switch (input) {
                case 1 -> generateKeys(userName);
                case 2 -> readKeys(userName);
                case 3 -> encryptMessage();
                case 4 -> decryptMessage();
                case 5 -> encryptFile();
                case 6 -> decryptFile();
                case 7 -> logNewUser();
                case 0 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> {
                    System.err.println("Please provide a number between 0 and 7");
                    System.out.println("====================================================");
                }
            }
        }
    }

    private static void decryptFile() {
        System.out.println("Type name of the encrypted file: ");
        String textFile = new Scanner(System.in).nextLine();
        if (privateKey != null) {
            RSA.decryptFile(textFile, privateKey);
        } else {
            System.out.println("Please read in your keys first");
        }
    }

    /**
     * Encrypt text file. Functions encrypts text file with either current user publicKey (option 1)
     * or (option 2) with another users publicKey (if available) - relies on MultipleUsersSign class
     */
    private static void encryptFile() {
        System.out.println("1. Encrypt a file for your own use\n" +
                "2. Encrypt and sign a file that only another user can decrypt (Current user won't be able to decrypt it)\n" +
                "Type your option here: ");
        int input = new Scanner(System.in).nextInt();

        switch (input) {
            case 1 -> {
                System.out.println("Type name of the file: ");
                String textFile = new Scanner(System.in).nextLine();
                if (publicKey == null) {
                    readKeys(userName);
                }
                RSA.encryptFile(textFile, publicKey);
            }
            case 2 -> MultipleUsersSign.encryptWithReceiversPublicKey();
            default -> System.out.println("Please type in a number either 1 or 2");
        }
    }

    /**
     * Decrypt user input.
     * Function decrypts user input, accepts String and returns decrypted String
     */
    private static void decryptMessage() {
        if (publicKey != null) {
            System.out.println("Please paste your encrypted message here: ");
            String encryptedMsg = new Scanner(System.in).nextLine();
            String decrypted = RSA.decrypt(encryptedMsg, privateKey);
            System.out.println("Your decrypted message: " + decrypted);
        } else {
            System.out.println("Reading in your private key in order to decrypt a message...");
            readKeys(userName);
            decryptMessage();
        }
    }

    /**
     * Encrypt user input.
     * Function encrypts message and returns it through stdout
     */
    private static void encryptMessage() {
        System.out.println("Type your message: ");
        String msg = new Scanner(System.in).nextLine();
        if (publicKey != null) {
            String encrypted = RSA.encrypt(msg, publicKey);
            System.out.println("Your message has been encrypted to:\n " + encrypted);
        } else {
            System.out.println("Please start with generating keys in order to work with the program.\n" +
                    "If you've already done it please read your username keys");
        }
    }

    private static void readKeys(String fileName) {
        publicKey = RSA.readKey(fileName + "_pub.key");
        privateKey = RSA.readKey(fileName + "_priv.key");
        System.out.println("Keys successfully uploaded\n");
    }

    private static void generateKeys(String fileName) {
        RSA.generateRSAKeys(fileName);
        System.out.println("Keys successfully generated\n");
    }

    /**
     * Log new user. Variable 'username' changes, reinitialising of keys is required. Make possible encrypting
     * and decrypting messages between multiple users
     */
    public static void logNewUser() {
        System.out.println("Please enter a new username:");
        userName = new Scanner(System.in).nextLine();
        File file = new File(userName + "_pub.key");
        if (file.exists()) {
            System.out.println("Reading in keys for a new user...");
            readKeys(userName);
        } else {
            System.out.println("Please start with generating new keys");
        }
    }
}
