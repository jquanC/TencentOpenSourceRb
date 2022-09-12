package uti;

import java.math.BigInteger;

/**
 * 为了方便更换曲线测试，写一个曲线配置类
 * */
public abstract class Configure {
    /**
     * 素数p
     */
    public   BigInteger p ;

    /**
     * 系数a
     */
    private  BigInteger a;

    /**
     * 系数b
     */
    private  BigInteger b;

    /**
     * 基点G, G=(xg,yg)
     */
    private  BigInteger xg ;

    private  BigInteger yg;

    /**
     * 基点G, G=(xg,yg),其阶记为n
     * G点的n倍，结果是无穷远点
     */
    private  BigInteger n ;


    /**
     * Alice和Bob要想使用ECC通讯就必须在同一条曲线上计算，而这个曲线通常会选用标准曲线，标准曲线之所以标准，就是这条曲线上的参数都是确定的，
     * 基点和循环子群的阶自然也是确定的，并且是告诉你的，随意基点和循环子群的阶是确定的
     */


    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public BigInteger getB() {
        return b;
    }

    public void setB(BigInteger b) {
        this.b = b;
    }

    public BigInteger getXg() {
        return xg;
    }

    public void setXg(BigInteger xg) {
        this.xg = xg;
    }

    public BigInteger getYg() {
        return yg;
    }

    public void setYg(BigInteger yg) {
        this.yg = yg;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }
}
