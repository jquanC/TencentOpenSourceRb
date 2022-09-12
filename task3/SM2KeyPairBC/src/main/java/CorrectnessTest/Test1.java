package CorrectnessTest;

import sm2.SM2KeyPair;
import uti.SM2Util;

public class Test1 {
    public static void main(String[] args) {
        SM2Util sm2  = new SM2Util();
        SM2KeyPair keyPair = sm2.generateKeyPair();
        sm2.isPublicKeyValid(keyPair.getPublicKey());
    }

}
