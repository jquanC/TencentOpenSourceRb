package JDKPointMulti;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Test {
    public static final String SPEC = "secp256r1"; //ECC使用的曲线
    public static PublicKey publicKey = null;
    private static PrivateKey privateKey = null;
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SPEC);//"secp256k1"
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        keyPairGen.initialize(ecSpec, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

    }
}
