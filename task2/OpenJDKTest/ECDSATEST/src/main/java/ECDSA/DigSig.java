package ECDSA;


import com.alibaba.fastjson.JSONObject;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * reference
 * url:https://metamug.com/article/security/sign-verify-digital-signature-ecdsa-java.html
 * jmh annotations:http://javadox.com/org.openjdk.jmh/jmh-core/0.9/org/openjdk/jmh/annotations/package-summary.html
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 1)//timeUnit：时间的单位，默认的单位是秒
@Measurement(iterations = 5, time = 5)
@Threads(5)
//这个注释可以放在Benchmark方法上，只对该方法产生影响，或者放在外围类实例上，对类中的所有Benchmark方法产生影响;fork是面向进程的，而Threads是面向线程的。指定了这个注解以后，将会开启并行测试。
@Fork(1)//fork的值一般设置成1，表示只使用一个进程进行测试
@State(value = Scope.Benchmark)//指定了在类中变量的作用范围。它有三个取值。Benchmark：表示变量的作用范围是某个基准测试类
@OutputTimeUnit(TimeUnit.NANOSECONDS)

public class DigSig {
    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException, RunnerException {
        /*DigSig digSigTool = new DigSig();

        JSONObject obj = digSigTool.sender();
        boolean result = digSigTool.receiver(obj);
        System.out.println("result should be true: "+result);*/
        Options opt = new OptionsBuilder()
                .include(DigSig.class.getSimpleName())
                .result("result.json")
                .resultFormat(ResultFormatType.JSON).build();
        new Runner(opt).run();

    }

    //    private static final String SPEC = "secp256k1"; //ECC使用的曲线
    private static final String SPEC = "secp256r1"; //ECC使用的曲线
    private static final String ALGO = "SHA256withECDSA"; //数字签名算法
    private static final String PLAIN_TEXT = "Hello,ECDSA!";
    private static JSONObject obj = null;

    @Setup
    public void setUp() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        obj = sender();
    }

    @Benchmark
    public JSONObject sender() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, SignatureException, InvalidKeyException {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SPEC);//"secp256k1"
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        keyPairGen.initialize(ecSpec, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        return sign(privateKey, publicKey, PLAIN_TEXT);

    }

    private JSONObject sign(PrivateKey privateKey, PublicKey publicKey, String plainText) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature ecdsaSign = Signature.getInstance(ALGO);
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] signature = ecdsaSign.sign();


        String pkStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String sigStr = Base64.getEncoder().encodeToString(signature);
//        System.out.println("signature in String format:" + sigStr);
//        System.out.println("public key in String format:" + pkStr);

//        JSONObject obj = new JSONObject();
        this.obj = new JSONObject();
        obj.put("publicKey", pkStr);
        obj.put("signature", sigStr);
        obj.put("message", plainText);
        obj.put("algorithm", ALGO);
        // correctness_ test
//        obj.put("message", plainText+"test");


        return obj;

    }

    @Benchmark //测试入参注释 JSONObject obj
    public boolean receiver() throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        Signature ecdsaVerify = Signature.getInstance(obj.getString("algorithm"));
        KeyFactory kf = KeyFactory.getInstance("EC");

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                Base64.getDecoder().decode(obj.getString("publicKey"))
        );
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return verify(ecdsaVerify, publicKey, obj);


    }

    private boolean verify(Signature ecdsaVerify, PublicKey publicKey, JSONObject obj) throws InvalidKeyException, SignatureException {
        ecdsaVerify.initVerify(publicKey);
        byte[] message = obj.getString("message").getBytes(StandardCharsets.UTF_8);
        ecdsaVerify.update(message);
        boolean result = ecdsaVerify.verify(Base64.getDecoder().decode(obj.getString("signature")));
        System.out.println("result should be true: " + result);
        return result;
    }


}
