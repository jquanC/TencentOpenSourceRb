package uti;

import java.math.BigInteger;

/**
 * 不同的曲线在这里完成配置 + 注册
 * */
public class Ecc extends Configure {
    public static Ecc curve1 = new Ecc();
    public static Ecc curve2 = new Ecc();

    static {
      configure();
    }
    public static void configure(){
        configureCurveN1();
        configureCurveN2();
    }
    public static void configureCurveN1() {
        /*param comes: http://c.gb688.cn/bzgk/gb/showGb?type=online&hcno=66A89DD6DA64F49C49456B757BA0624F*/
        curve2.setP(new BigInteger("8542D69E"+"4C044F18"+"E8B92435"+"BF6FF7DE"+"45728391"+"5C45517D"+"722EDB8B"+"08F1DFC3", 16));
        curve2.setA(new BigInteger("787968B4"+"FA32C3FD"+"2417842E"+"73BBFEFF"+"2F3C848B"+"6831D7E0"+"EC65228B"+"3937E498", 16));
        curve2.setB(new BigInteger("63E4C6D3"+"B23B0C84"+"9CF84241"+"484BFE48"+"F61D59A5"+"B16BA06E"+"6E12D1DA"+"27C5249A", 16));
        //选择基点G，余因子h=1
        curve2.setXg(new BigInteger("421DEBD6"+ "1B62EAB6"+ "746434EB"+ "C3CC315E"+ "32220B3B"+"ADD50BDC"+"4C4E6C14"+"7FEDD43D", 16));
        curve2.setYg(new BigInteger("0680512B" +"CBB42C07" +"D47349D2" +"153B70C4" +"E5D7FDFC" +"BFA36EA1" +"A85841B9" +"E46E09A2", 16));
        curve2.setN(new BigInteger("8542D69E" +"4C044F18" +"E8B92435" +"BF6FF7DD" +"29772063" +"0485628D" +"5AE74EE7" +"C32E79B7", 16));

    }


    /*param comes:https://blog.51cto.com/u_10125763/4061233*/
    public static void configureCurveN2() {
        curve1.setP(new BigInteger("FFFFFFFE" + "FFFFFFFF"
                + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF"
                + "FFFFFFFF", 16));
        curve1.setA(new BigInteger("FFFFFFFE" + "FFFFFFFF"
                + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF"
                + "FFFFFFFC", 16));
        curve1.setB(new BigInteger("28E9FA9E" + "9D9F5E34"
                + "4D5A9E4B" + "CF6509A7" + "F39789F5" + "15AB8F92" + "DDBCBD41"
                + "4D940E93", 16));
        curve1.setN(new BigInteger("FFFFFFFE" + "FFFFFFFF"
                + "FFFFFFFF" + "FFFFFFFF" + "7203DF6B" + "21C6052B" + "53BBF409"
                + "39D54123", 16));
        curve1.setXg(new BigInteger("32C4AE2C" + "1F198119"
                + "5F990446" + "6A39C994" + "8FE30BBF" + "F2660BE1" + "715A4589"
                + "334C74C7", 16));
        curve1.setYg(new BigInteger("BC3736A2" + "F4F6779C"
                + "59BDCEE3" + "6B692153" + "D0A9877C" + "C62A4740" + "02DF32E5"
                + "2139F0A0", 16));
    }


}
