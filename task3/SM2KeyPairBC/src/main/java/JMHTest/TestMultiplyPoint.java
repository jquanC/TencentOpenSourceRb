package JMHTest;

import org.bouncycastle.math.ec.ECPoint;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import uti.PreComInfo;
import uti.SM2Util;
import uti.SelfMultiply;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)//其他参数 time:持续时间，默认10s timeUnit：时间的单位，默认的单位是秒 ;
@Measurement(iterations = 5) //
@Threads(5)
//这个注释可以放在Benchmark方法上，只对该方法产生影响，或者放在外围类实例上，对类中的所有Benchmark方法产生影响;fork是面向进程的，而Threads是面向线程的。指定了这个注解以后，将会开启并行测试。
@Fork(1)//fork的值一般设置成1，表示只使用一个进程进行测试
//@State(value = Scope.Benchmark)//指定了在类中变量的作用范围。它有三个取值。Benchmark：表示变量的作用范围是某个基准测试类共享 ; Thread: 每个实例有自己的一份数据
@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)

public class TestMultiplyPoint {
    public BigInteger fixD = null;
    public ECPoint G = null;
    public SM2Util sm2 = null;
    public ECPoint r0 = null;
    public PreComInfo preInfo = null;

    @Param({"sm2C1", "sm2C2"})
    public static String sm2Curve;

    @Param({"7","17"})
    public static int skSeed;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TestMultiplyPoint.class.getSimpleName())
                .result("sm2PublicKeyGen.json")
                .resultFormat(ResultFormatType.JSON).build();
        new Runner(opt).run();

    }

    @Setup
    public void setUp() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        sm2 = new SM2Util(sm2Curve);
//        fixD = SM2Util.getRandom(sm2.n.subtract(BigInteger.ONE));
        fixD = SM2Util.getRandomWithSeed(sm2.n.subtract(BigInteger.ONE));
        System.out.println(fixD.toString());
        G = sm2.G;
        r0 = sm2.curve.getInfinity();
        preInfo = SelfMultiply.windowMultPreCompute(r0, G, fixD, 4);//execute the preComputeTask
    }

    @Benchmark
    public void bcMult() {
        G.multiply(fixD);
    }

    @Benchmark
    public void iteraMult() {
        SelfMultiply.iterativeMultiply(r0, G, fixD);
    }

    @Benchmark
    public void recurMult() {
        SelfMultiply.recursiveMultiply(G, fixD);
    }

    @Benchmark
    public void mongMult() {
        SelfMultiply.montgomeryMultiply(r0, G, fixD);
    }

    @Benchmark
    public void wndMultContainPreCompute() {
//        SelfMultiply.windowMultiply(r0,G,fixD,4);
        PreComInfo curPreInfo = SelfMultiply.windowMultPreCompute(r0, G, fixD, 4);
        SelfMultiply.windowMultiplyTest(curPreInfo, r0, fixD);
    }


    @Benchmark
    public void wndMult() {
//        SelfMultiply.windowMultiply(r0,G,fixD,4);
        SelfMultiply.windowMultiplyTest(preInfo, r0, fixD);
    }


}
