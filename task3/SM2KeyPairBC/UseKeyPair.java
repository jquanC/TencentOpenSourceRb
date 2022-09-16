import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class UseKeyPair {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
//        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256r1");
        ECGenParameterSpec ecGenParameterSpec1 = new ECGenParameterSpec("sm2p256c1");
        ECGenParameterSpec ecGenParameterSpec2 = new ECGenParameterSpec("sm2p256c1");

        KeyPairGenerator keyPairGenerator1 = KeyPairGenerator.getInstance("EC");
        KeyPairGenerator keyPairGenerator2 = KeyPairGenerator.getInstance("EC");

        keyPairGenerator1.initialize(ecGenParameterSpec1, new SecureRandom());
        keyPairGenerator2.initialize(ecGenParameterSpec2, new SecureRandom());

        KeyPair keyPair1 = keyPairGenerator1.generateKeyPair();
        PublicKey pk1 = keyPair1.getPublic();
        PrivateKey sk1 = keyPair1.getPrivate();
        System.out.println("ecc sm2p256c1-public key:"+pk1);
        Signature ecdsaSign1 = Signature.getInstance("SHA256withECDSA");
        ecdsaSign1.initSign(sk1);

        KeyPair keyPair2 = keyPairGenerator2.generateKeyPair();
        PublicKey pk2 = keyPair2.getPublic();
        PrivateKey sk2 = keyPair2.getPrivate();
        System.out.println("ecc sm2p256c2-public key:"+pk2);
        Signature ecdsaSign2 = Signature.getInstance("SHA256withECDSA");
        ecdsaSign2.initSign(sk2);

        //
        String message = "Tencent rhino bird";
        System.out.println("Message to sign and verify:"+message);

        //
        ecdsaSign1.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] signature1 = ecdsaSign1.sign();
        Signature ecdsaVerify1 = Signature.getInstance("SHA256withECDSA");
        ecdsaVerify1.initVerify(pk1);
        ecdsaVerify1.update(message.getBytes(StandardCharsets.UTF_8));
        boolean result1 = ecdsaVerify1.verify(signature1);
        System.out.println("ecdsa(sm2c1)-algo-signature verify message result:"+result1);


        ecdsaSign2.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] signature2 = ecdsaSign2.sign();
        Signature ecdsaVerify2 = Signature.getInstance("SHA256withECDSA");
        ecdsaVerify2.initVerify(pk2);
        ecdsaVerify2.update(message.getBytes(StandardCharsets.UTF_8));
        boolean result2 = ecdsaVerify2.verify(signature2);
        System.out.println("ecdsa(sm2c2)-algo-signature verify message result:"+result2);

    }
}
