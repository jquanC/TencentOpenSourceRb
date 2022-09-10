package ECDSA;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * reference
 * url:https://metamug.com/article/security/sign-verify-digital-signature-ecdsa-java.html
 * jmh annotations:http://javadox.com/org.openjdk.jmh/jmh-core/0.9/org/openjdk/jmh/annotations/package-summary.html
 */
/*@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 1)//timeUnit：时间的单位，默认的单位是秒
@Measurement(iterations = 5, time = 5)
@Threads(5)
//这个注释可以放在Benchmark方法上，只对该方法产生影响，或者放在外围类实例上，对类中的所有Benchmark方法产生影响;fork是面向进程的，而Threads是面向线程的。指定了这个注解以后，将会开启并行测试。
@Fork(1)//fork的值一般设置成1，表示只使用一个进程进行测试
@State(value = Scope.Benchmark)//指定了在类中变量的作用范围。它有三个取值。Benchmark：表示变量的作用范围是某个基准测试类
@OutputTimeUnit(TimeUnit.NANOSECONDS)*/

public class TestDigSigForAsyncProfiler {
    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException, RunnerException {
      /*  TestDigSig digSigTool = new TestDigSig();
        digSigTool.setUp();
        digSigTool.sign();
        boolean result = digSigTool.verify();
        System.out.println("result should be true: "+result);*/

        /*
        Options opt = new OptionsBuilder()
                .include(TestDigSigForAsyncProfiler.class.getSimpleName())
                .result("result.json")
                .resultFormat(ResultFormatType.JSON).build();
        new Runner(opt).run();*/
        TestDigSigForAsyncProfiler testProfiler = new TestDigSigForAsyncProfiler();

        testProfiler.setUp();
        testProfiler.sign();
        testProfiler.verify();


    }

    public static final String SPEC = "secp256r1"; //ECC使用的曲线
//    public static final String SPEC = "secp256k1"; //ECC使用的曲线
    public static final String ALGO = "SHA256withECDSA"; //数字签名算法
    public static String PLAIN_TEXT = "Hello,ECDSA!";

    //need initial
    public static byte[] SIGN_CONTENT = null;
    public static PublicKey publicKey = null;
    private static PrivateKey privateKey = null;
    public static byte[] sigContent = null;

//    private static JSONObject obj = null;

    @Setup
    public void setUp() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException {
        Random random = new Random(7);
        //1024 KB
//        SIGN_CONTENT = new byte[1<<20];
        SIGN_CONTENT = new byte[1 << 10];
        random.nextBytes(SIGN_CONTENT);
//        sign();
    }


//    @Benchmark
    public void sign() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidAlgorithmParameterException {

        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SPEC);//"secp256k1/r1"
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        keyPairGen.initialize(ecSpec, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

        Signature ecdsaSign = Signature.getInstance(ALGO);
        ecdsaSign.initSign(privateKey);
//        ecdsaSign.update(PLAIN_TEXT.getBytes(StandardCharsets.UTF_8));
        ecdsaSign.update(SIGN_CONTENT);
        sigContent = ecdsaSign.sign();


    }


//    @Benchmark
    public void verify() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException {

        Signature ecdsaVerify = Signature.getInstance(ALGO);

        ecdsaVerify.initVerify(publicKey);
//        byte[] message = PLAIN_TEXT.getBytes(StandardCharsets.UTF_8);
//        byte[] message = (PLAIN_TEXT+"test").getBytes(StandardCharsets.UTF_8);
        ecdsaVerify.update(SIGN_CONTENT);
        boolean result = ecdsaVerify.verify(sigContent);

        if(result) System.out.println("digital signature verify pass");
        if (!result) System.out.println("digital signature verify failed");
//        return result;
    }


}
