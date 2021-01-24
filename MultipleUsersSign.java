import java.io.File;
import java.util.Scanner;

public class MultipleUsersSign {

    /**
     * Utility class
    * Encrypt a text file that another user publicKey with existing keys can decrypt it
     * functions asks for receivers username, and if that user already have generated keys uses them
     * to sign file
    */
    public static void encryptWithReceiversPublicKey() {
        Scanner scn = new Scanner(System.in);
        System.out.println("Type receivers username (file will be encrypted with their key):");
        String receiverName = scn.nextLine();
        File recvFile = new File(receiverName + "_pub.key");
        if(recvFile.exists()) {
            KeyPair receiverPubKey = RSA.readKey(receiverName + "_pub.key");
            System.out.println("Type name of the file to encrypt:");
            String encryptedFilename = scn.nextLine();
            File fileToEncrypt = new File(encryptedFilename);
            if (fileToEncrypt.exists()) {
                RSA.encryptFile(encryptedFilename, receiverPubKey);
            } else {
                System.out.println("Provided filename doesn't exist");
            }
        } else {
            System.out.println("Provided username doesn't have matching keys");
        }
    }
}
