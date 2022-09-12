package uti;

import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi;
import org.bouncycastle.math.ec.*;

import java.math.BigInteger;

public class SelfMultiply {

    static BigInteger twoVal = new BigInteger("2");

    static ECPoint[] preCompute = null;
    static ECPoint[] preComputeDigitWeight = null;
    static int[] windowDigit = null;//k 转为 w进制
    static int windowWidth = 0;


    public static ECPoint iterativeMultiply(ECPoint r0, ECPoint p, BigInteger k) {
        int bitLen = k.bitLength();
        ECPoint temp = p;
        int idx = 1;
        while (idx <= bitLen) {
            if (k.testBit(idx)) {
                r0 = r0.add(temp);
            }
            temp = temp.twice();
            idx++;
        }
        return r0;
    }

    public static ECPoint recursiveMultiply(ECPoint p, BigInteger k) {

        ECCurve c = p.getCurve();
        int size = FixedPointUtil.getCombSize(c);//方法返回工作的循环群的阶order,即n的大小。如果order不空，否则放回有限域大小

        if (k.bitLength() > size) {
            /*
             * TODO The comb works best when the scalars are less than the (possibly unknown) order.
             * Still, if we want to handle larger scalars, we could allow customization of the comb
             * size, or alternatively we could deal with the 'extra' bits either by running the comb
             * multiple times as necessary, or by using an alternative multiplier as prelude.
             */
            throw new IllegalStateException("fixed-point comb doesn't support scalars larger than the curve order");
        }
        ECPoint R = c.getInfinity();
        if (k.compareTo(BigInteger.ZERO) == 0) {
            return R;
        }
        if (k.compareTo(BigInteger.ONE) == 0) {
            return p;
        }
        if (k.remainder(twoVal).compareTo(BigInteger.ONE) == 0) {
            return p.add(recursiveMultiply(p, k.subtract(BigInteger.ONE)));
        } else {
            return recursiveMultiply(p.twice(), k.divide(twoVal));
        }

    }

    public static ECPoint montgomeryMultiply(ECPoint r0, ECPoint p, BigInteger k) {
        ECPoint r1 = p;
        int index = k.bitLength();//number of bits in the minimal two's-complement representation of this BigInteger, excluding a sign bit.
        while (index >= 0) {
            if (k.testBit(index--)) {
                r0 = r0.add(r1);
                r1 = r1.add(r1);
            } else {
                r1 = r0.add(r1);
                r0 = r0.add(r0);
            }
        }
        return r0;

        /**testBit()源码分析：
         *   return (getInt(n >>> 5) & (1 << (n & 31))) != 0;
         *   e.g., n=3, 12 = ob 1100, 12.testBit(3) should be true
         *   31 = 0111 1111 ,
         *   n & 31 = 3 & 31 = 0000 0011 & 0111 1111 = 0000 011 = n
         *   1<<3 = 0b 1000
         *   getInt(n>>>5) 取mag[0] 还是12 ob 1100
         *   1100 & 1000 != 0 return true;
         * */

    }

    public static ECPoint windowMultiply(ECPoint r0, ECPoint p, BigInteger k, int width) {
        windowWidth = width;
        preCompute = new ECPoint[(int) Math.pow(2, width)];//0,1P,2P,....,15P
        preComputeDigitWeight = new ECPoint[(int) k.bitLength() / width + 1];//2^{wi}p

        preCompute[0] = r0;
        for (int i = 1; i < preCompute.length; i++) {
            preCompute[i] = preCompute[i - 1].add(p);
        }
        preComputeDigitWeight[0] = p;
        preComputeDigitWeight[1] = preCompute[preCompute.length - 1].add(p);

        for (int i = 2; i < preComputeDigitWeight.length; i++) {
            preComputeDigitWeight[i] = preComputeDigitWeight[i - 1].twice();
        }
        windowDigit = new int[(int) k.bitLength() / width + 1];
        //pre-compute work finish

        return windowMultiply(r0, k);
//        return null;//for test
    }

    public static ECPoint windowMultiply(ECPoint r0, BigInteger k) {

        //处理k
        int idx = 0;
        int cou = 0;
        int eachKi = 0;
        int binaryWeight = 1;
        for (int i = 0; i < k.bitLength(); i++) {
            if (cou == 4) {
                windowDigit[idx++] = eachKi;
                eachKi = 0;
                cou = 0;
                binaryWeight = 1;
            }

            if (k.testBit(i)) {
                eachKi += binaryWeight;
            }
            binaryWeight *= 2;
            cou++;
        }
        windowDigit[idx++] = eachKi;

        //计算Qj
        ECPoint[] Q = new ECPoint[(int) Math.pow(2, windowWidth)];

        for (int i = 1; i < (int) Math.pow(2, windowWidth); i++) {
            Q[i] = r0;
            for (int j = 0; j < windowDigit.length; j++) {
                if (windowDigit[j] == i) {
                    Q[i] = Q[i].add(preComputeDigitWeight[j]);
                }
            }
        }

        ECPoint r = r0;
        ECPoint adder = r0;
        for (int j = (int) Math.pow(2, windowWidth) - 1; j >= 1; j--) {
            adder = adder.add(Q[j]);
            r = r.add(adder);
        }
        return r;
    }

    /**
     * 避免 JMH 测试并发对PreComInfo修改导致错误
     */
    public static PreComInfo windowMultPreCompute(ECPoint r0, ECPoint p, BigInteger k, int width) {
        PreComInfo preInfo = new PreComInfo();
        preInfo.windowWidth = width;
        preInfo.preCompute = new ECPoint[(int) Math.pow(2, width)];//0,1P,2P,....,15P
        preInfo.preComputeDigitWeight = new ECPoint[(int) k.bitLength() / width + 1];//2^{wi}p

        preInfo.preCompute[0] = r0;
       /* System.out.println("preComput len:" + preInfo.preCompute.length);
        if (preInfo.preCompute[0] == null) System.out.println("prec[0] = null*********&&&&&&&&&&&&&&&&&");*/

        for (int i = 1; i < preInfo.preCompute.length; i++) {
            preInfo.preCompute[i] = preInfo.preCompute[i - 1].add(p);
//            if (preInfo.preCompute[i] == null) System.out.println("i=" + i + " ,will pow null point exception");
        }
        preInfo.preComputeDigitWeight[0] = p;
        preInfo.preComputeDigitWeight[1] = preInfo.preCompute[preInfo.preCompute.length - 1].add(p);

        for (int i = 2; i < preInfo.preComputeDigitWeight.length; i++) {
            preInfo.preComputeDigitWeight[i] = preInfo.preComputeDigitWeight[i - 1].twice();
        }
        preInfo.windowDigit = new int[(int) k.bitLength() / width + 1];
        //pre-compute work finish
        return preInfo;
    }

    public static ECPoint windowMultiplyTest(PreComInfo preInfo, ECPoint r0, BigInteger k) {

        //处理k
        int idx = 0;
        int cou = 0;
        int eachKi = 0;
        int binaryWeight = 1;
        for (int i = 0; i < k.bitLength(); i++) {
            if (cou == 4) {
                preInfo.windowDigit[idx++] = eachKi;
                eachKi = 0;
                cou = 0;
                binaryWeight = 1;
            }

            if (k.testBit(i)) {
                eachKi += binaryWeight;
            }
            binaryWeight *= 2;
            cou++;
        }
        preInfo.windowDigit[idx++] = eachKi;

        //计算Qj
        ECPoint[] Q = new ECPoint[(int) Math.pow(2, preInfo.windowWidth)];

        for (int i = 1; i < (int) Math.pow(2, preInfo.windowWidth); i++) {
            Q[i] = r0;
            for (int j = 0; j < preInfo.windowDigit.length; j++) {
                if (preInfo.windowDigit[j] == i) {
                    Q[i] = Q[i].add(preInfo.preComputeDigitWeight[j]);
                }
            }
        }

        ECPoint r = r0;
        ECPoint adder = r0;
        for (int j = (int) Math.pow(2, windowWidth) - 1; j >= 1; j--) {
            adder = adder.add(Q[j]);
            r = r.add(adder);
        }
        return r;
    }
}
