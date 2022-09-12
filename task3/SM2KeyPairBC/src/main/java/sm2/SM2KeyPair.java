package sm2;


import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class SM2KeyPair {

    private ECPoint publicKey;
    private BigInteger privateKey;

    public SM2KeyPair(ECPoint publicKey,BigInteger privateKey){
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public ECPoint getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(ECPoint publicKey) {
        this.publicKey = publicKey;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(BigInteger privateKey) {
        this.privateKey = privateKey;
    }
}
