package uti;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import sm2.SM2KeyPair;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Random;


public class SM2Util {

    /**
     * Alice和Bob要想使用ECC通讯就必须在同一条曲线上计算，而这个曲线通常会选用标准曲线，标准曲线之所以标准，就是这条曲线上的参数都是确定的，
     * 基点和循环子群的阶自然也是确定的，并且是告诉你的，随意基点和循环子群的阶是确定的
     */
    private static Ecc config = Ecc.curve2;
    ;
    public ECCurve.Fp curve;
    public ECPoint G;
    private static SecureRandom random = new SecureRandom();
    private static SecureRandom randomSeed = null;
//    private static BigInteger fixRandom = getRandom(n.subtract(new BigInteger("1")));

    public BigInteger p = config.getP();
    public BigInteger a = config.getA();
    public BigInteger b = config.getB();
    public BigInteger xg = config.getXg();
    public BigInteger yg = config.getYg();
    public BigInteger n = config.getN();

    public SM2Util() {

        curve = new ECCurve.Fp(p, a, b);
        G = curve.createPoint(xg, yg);
    }

    //forTest:"sm2C1","sm2C2","sm2C3"
    public SM2Util(String curveSelect) {
        if (curveSelect.equals("sm2C1")) {
            config = Ecc.curve1;
        } else {
            config = Ecc.curve2;
        }

        curve = new ECCurve.Fp(p, a, b);
        G = curve.createPoint(xg, yg);
        autoConfig();
    }

    public void autoConfig() {
        p = config.getP();
        a = config.getA();
        b = config.getB();
        xg = config.getXg();
        yg = config.getYg();
        n = config.getN();
    }

    public SM2KeyPair generateKeyPair() {
        BigInteger d = getRandom(n.subtract(new BigInteger("1")));
        SM2KeyPair keyPair = new SM2KeyPair(G.multiply(d).normalize(), d);
//        SM2KeyPair keyPair = new SM2KeyPair(G.multiply(d), d);//pass

        return keyPair;
    }

    public SM2KeyPair generateKeyPairUseRecurMultiply() {
        BigInteger d = getRandom(n.subtract(new BigInteger("1")));
        SM2KeyPair keyPair = new SM2KeyPair(SelfMultiply.recursiveMultiply(G, d).normalize(), d);

        return keyPair;
    }

    public SM2KeyPair generateKeyPairUseMontgomery() {
        BigInteger d = getRandom(n.subtract(new BigInteger("1")));
        SM2KeyPair keyPair = new SM2KeyPair(SelfMultiply.montgomeryMultiply(curve.getInfinity(), G, d).normalize(), d);

        return keyPair;
    }

    public SM2KeyPair generateKeyPairUseIterativeAlg() {
        BigInteger d = getRandom(n.subtract(new BigInteger("1")));
        SM2KeyPair keyPair = new SM2KeyPair(SelfMultiply.iterativeMultiply(curve.getInfinity(), G, d).normalize(), d);

        return keyPair;
    }

    public SM2KeyPair generateKeyPairUseWindow() {
        BigInteger d = getRandom(n.subtract(new BigInteger("1")));
        SM2KeyPair keyPair = new SM2KeyPair(SelfMultiply.windowMultiply(curve.getInfinity(), G, d, 4).normalize(), d);

        return keyPair;
    }

    public boolean isPublicKeyValid(ECPoint publicKey) {
        if (!publicKey.isInfinity()) {
            BigInteger x = publicKey.getXCoord().toBigInteger();
            BigInteger y = publicKey.getYCoord().toBigInteger();

            BigInteger zero = new BigInteger("0");

            if (x.compareTo(zero) >= 0 && x.compareTo(p) < 0 && y.compareTo(zero) >= 0 && y.compareTo(p) < 0) {
                BigInteger right = x.pow(3).add(a.multiply(x)).add(b).mod(p);
                System.out.println("equation right side:" + right.toString());
                BigInteger left = y.pow(2).mod(p);
                System.out.println("equation left  side:" + left.toString());

                //有限域上的椭圆曲线还有一个很有用的结论，其上任意一个点P，经过若干次标量乘之后都会回到无穷远点0，也就是说总存在n，使得nP=0;
                // 对于公钥G，同时也是基点和生成原 NG = 0
                if (publicKey.multiply(n).isInfinity()) {
                    System.out.println("nG equals to Infinity");
                }
                if (left.compareTo(right) == 0 && publicKey.multiply(n).isInfinity()) {
                    System.out.println("SM2 KeyPair() generated successfully");
                    return true;
                }

            }

            return false;
        }
        return false;

    }

    /**
     * get a random num under constraint
     */
    public static BigInteger getRandom(BigInteger max) {
        BigInteger r = new BigInteger(256, random);
        while (r.compareTo(max) >= 0) {
            r = new BigInteger(128, random);
        }
        return r;
    }

    public static BigInteger getRandomWithSeed(BigInteger max) {

        String str = "Happy mid-autumn festival";

        // Declaring the byte Array b
        byte[] seed = str.getBytes();
        SecureRandom secureRandom = new SecureRandom(seed);
        BigInteger r = new BigInteger(256, secureRandom);
        while (r.compareTo(max) >= 0) {
            r = new BigInteger(128, secureRandom);
        }
        return r;
    }


}
