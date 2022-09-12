package CorrectnessTest;

import sm2.SM2KeyPair;
import uti.SM2Util;

public class Test3 {
    public static void main(String[] args) {
        SM2Util sm2  = new SM2Util();
        SM2KeyPair keyPair = sm2.generateKeyPairUseMontgomery();
        sm2.isPublicKeyValid(keyPair.getPublicKey());

    }
}
