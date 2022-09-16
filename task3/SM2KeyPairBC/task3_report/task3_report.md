# Task-3: SM2 Key Pair Generation

## Project Background

- [2022腾讯犀牛鸟开源人才培养计划-Tencent konajdk](https://github.com/Tencent/OpenSourceTalent/issues/34)

Overall, in this task, we study the generation principle of SM2 key pairs, use different algorithms to generate SM2 key pairs that meet the requirements, and test their performance.



## Task requirement

### Description

Apply the tools in OpenJDK sun.security.util.math and sun.security.ec to the national secret SM2 algorithm to generate a key pair for SM2.
Need to consider: How to verify that the generated key pair conforms to the requirements of the SM2 curve? Is the security strength of the key pair up to standard?



## Elliptic Curve Principle

Here is a brief introduction to the principle of elliptic curves and how to use elliptic curves to generate key pairs.

For more detail, here is a good introduction:  [Elliptic Curve Cryptography: a gentle introduction](https://andrea.corbellini.name/2015/05/17/elliptic-curve-cryptography-a-gentle-introduction/)



### **Abelian group definition on elliptic curve**[1]

1. the elements in the group are the points on the elliptic curve.
2. the identity element is the point $0$ at infinity.
3. the **inverse** of a point P is the one symmetric about the x-axis;
4. **addition** is given by the following rule: **given three aligned, non-zero points P, Q and R, their sum is P+Q+R=0**.



### **Elliptic Curves in the Real Domain**
On the real number domain, an Elliptic curve is defined (“Weierstrass Normal Form”) as follows:

$y^2 = x^3+ax+b$

![image-20220910162825110](task3_report.assets/image-20220910162825110.png)

[Wolfram MathWorld](http://mathworld.wolfram.com/EllipticCurve.html)





### **Elliptic Curves in the finite field**

We define the elliptic curve on the finite field, at this point the elliptic curve appears to be composed of points:

$E(F_p)={(x,y):x,y\in F_p\ satisfy\ y^2 = x^3+ax+b}\bigcup \{O\}$

![image-20220910163401060](task3_report.assets/image-20220910163401060.png)

[ZhiHu: Finite Fields and Discrete Logarithms](https://zhuanlan.zhihu.com/p/44743146)





It can be seen that after the elliptic curve is defined on the finite field, **these points are still symmetrical about the x-axis.**
**Regarding the definition of abelian groups for elliptic curves, it still holds.**

The $F_p-256$ means the curve defined in the finite field and Its finite field size is a prime number of length 256 bits.

Define the number of points an elliptic curve has on a finite field is **the order of the group of elliptic curves**. Schoof's algorithm can quickly find the order of elliptic curve groups over finite fields.





### **Cyclic subgroup**



Elliptic curves on finite fields also have a useful conclusion. Any point P on it will return to infinity point ***0*** after several scalar multiplications, that is to say, there always exists n, let $nP$=*0*

For example[7]:

$y^2 = x^3+2x+3 (mod 97)$,　$Point:P=(3,6)$.

we have: $P=P,2P=2P,3P=3P,4P=4P,5P=0,...,kP=(k\ mod\ 5)P$

We call this set a cyclic subgroup of the group. For the above example, the order of the cyclic subgroup is 5, and other points in this cyclic subgroup are obtained through point P, we call P the base point or generator of this cyclic subgroup.

A conclusion in group theory is that the order of a subgroup must be a factor of the order of the parent group. Assuming that the order of the parent group is N and the order of the subgroup is n, then **h=N/n** must be an integer, and **h is called the cofactor of the subgroup**.





### **Elliptic_curve_point_multiplication**

We call elliptic curve point multiplication as scalar multiplication in the follows.

For a key pair generation algorithm on an elliptic curve, its essence is to perform a scalar multiplication on the base point of a cyclic subgroup on the curve[2].

Given a certain base point P, private key n, public key $Q=nP$



### Security 

The security is based the Elliptic Curve Discrete Logarithmic Problem. This is because adding points on two elliptic curves will result in another point on the curve. **Intuitively, Its position is not directly related to the first two points. When n is large, the final point Q seems to appear anywhere.**

When the attacker has Q and P and wants to calculate the private key n, he can basically only try all possible n to complete. This is computationally infeasible.



**About National secret algorithm**

The national encryption algorithm is a domestic encryption algorithm issued and recognized by the State Cryptography Administration, which is different from the international algorithm. International algorithms generally refer to the most common commercial algorithms released by the US Security Agency today.

Official standards can refer to: [link](http://c.gb688.cn/bzgk/gb/showGb?type=online&hcno=66A89DD6DA64F49C49456B757BA0624F)



## Implement with BC

This repository implements several elliptic curve scalar multiplications from this [wiki](https://en.wikipedia.org/wiki/Elliptic_curve_point_multiplication) :

The scalar multiplication method code implemented in [~/task3/SM2KeyPairBC/src/main/java/util/SelfMultiply.java](https://github.com/jquanC/TencentOpenSourceRb/blob/main/task3/SM2KeyPairBC/src/main/java/util/SelfMultiply.java)

The  correctness test of above algorithm can be checked in the package [~task3/SM2KeyPairBC/src/main/java/CorrectnessTest](https://github.com/jquanC/TencentOpenSourceRb/tree/main/task3/SM2KeyPairBC/src/main/java/CorrectnessTest)

The correctness test result like the following:

````shell
equation right side:15673738628471445721161132814100655470091655484731840244752417643811842010650
equation left  side:15673738628471445721161132814100655470091655484731840244752417643811842010650
nG equals to Infinity
SM2 KeyPair() generated successfully
````







**Briefly, here are the several scalar multiplications we implemented and their pseudocodes:**

1. **Iterative algorithm: lsb to msb**

   ![image-20220910174352311](task3_report.assets/image-20220910174352311.png)

  

2. **Recursive algorithm**

   ![image-20220910174337099](task3_report.assets/image-20220910174337099.png)

   

3. ### Montgomery ladder

​                       ​![image-20220910174509947](task3_report.assets/image-20220910174509947.png)   

4. **Window method**

   ​                ![image-20220910174600154](task3_report.assets/image-20220910174600154.png)



**The instructions on the wiki are relatively brief, and further instructions (specific implementation) as follows**[12]:

 $w:the\ window\ size$ 

$k:Scalar in dot product $

Firstly, calculate $2^w$ values of $dP$ in advance for $d = 0,1,2,...,2^w-1$

We denote k in base $2^w$ as: $k = (k_{n'-1},...,k_2,k_1,k_0)_{2^w}$

The algorithm works as follows：



$kP = \sum_{i=0}^{n'-1}k_i(2^{wi}P)=\sum_{j=1}^{2^w-1}(j\sum_{i:k_i=j}2^{wi}P)$



Let:



$Q_j=\sum_{i:k_i=j}2^{wi}P$



We have:



$kP = \sum_{j=1}^{2^w-1}(jQ_j)=Q_{2^w-1}+(Q_{2^w-1}+Q_{2^w-2})+((Q_{2^w-1}+Q_{2^w-2}+...+Q_1))$



During the benchmark, we tested the windowing method with and without precomputed overhead for more clarity to its performance.





### **Performance Testing**

We use JMH as a tool for performance testing programs.
Implement a performance test of the generation of public key using  point scalar multiplication algorithm in sm2 elliptic curve.
We tested Bouncycastle's SM2 curve standard scalar_multiplication_algo as well as our own implementation, Iterative_scalar_multiplication_algo , recursive_ scalar_multiplication_algo , montgomery_ladder_  scalar_multiplication_algo, window_method_ scalar_multiplication_algo.

We perform tests under two sm2 curves: sm2C1, sm2C2, and two different private keys, whose seed is 7 and 17 respectively.

The curve parameters are as follows:

````text
Sm2C1:Fp-256
p：8542D69E 4C044F18	E8B92435 BF6FF7DE 45728391 5C45517D 722EDB8B 08F1DFC3
a：787968B4 FA32C3FD 2417842E 73BBFEFF 2F3C848B 6831D7E0	EC65228B 3937E498
b：63E4C6D3 B23B0C84 9CF84241 484BFE48 F61D59A5 B16BA06E	6E12D1DA 27C5249A
h：1
G＝(xG，yc)
xG：421DEBD6 1B62EAB6 746434EB C3CC315E 32220B3B ADD50BDC 4C4E6C14 7FEDD43D
yG：0680512B CBB42C07 D47349D2 153B70C4 E5D7FDFC BFA36EA1 A85841B9 E46E09A2
n：8542D69E 4C044F18 E8B92435 BF6FF7DD 29772063 0485628D 5AE74EE7 C32E79B7

Sm2C2:Fp-256
p：FFFFFFFE FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF 00000000 FFFFFFFF FFFFFFFF
a：FFFFFFFE FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF 00000000 FFFFFFFF FFFFFFFC
b：28E9FA9E 9D9F5E34 4D5A9E4B CF6509A7 F39789F5 15AB8F92 DDBCBD41 4D940E93
h：1
G＝(xG，yc)
xG：32C4AE2C 1F198119 5F990446 6A39C994 8FE30BBF F2660BE1 715A4589 334C74C7
yG： BC3736A2 F4F6779C 59BDCEE3 6B692153 D0A9877C C62A4740 02DF32E5 2139F0A0
n： FFFFFFFE FFFFFFFF FFFFFFFF FFFFFFFF 7203DF6B 21C6052B 53BBF409 39D54123

````





### result

Overview:

![result](task3_report.assets/result.png)



Test result data for different methods:

![m1](task3_report.assets/m1.png)



![m2](task3_report.assets/m2.png)



![m3](task3_report.assets/m3.png)





### Conclusion

- From the test data, the mongMult takes the longest time. This is because it does point_add and point_double when processing each bit of *n*. Doing this makes it take longer, but the benefit is that the Montgomery ladder is resistant to power attacks or timing attacks in Sideway Attack.
- IteraMult and recurMult have similar time costs. This is as expected, they are  implemented in a different way but same in the theory.

- The time overhead of the 4bit_wnw_Mult is smaller than the bc_Mult. The possible reason is that BC has done a lot of security checks in the implementation.
- Different curves and different private keys will bring about differences in execution speed. But this difference shows consistency across different testing algorithms.





## Implement in JDK



Since SM2 and secp256r1 are known as Weierstrass curve. They have the same elliptic curve equation. Defined on the same type of prime finite field Fp, it is a prime curve. And p is a 256-bit prime number.

Naturally, we think to learn from the implementation of secp256r1 in jdk to implement sm2. The method is actually very simple. Through the use of the secp256r1 curve in debug mode and the search of the related parameters, method,and code file in the work space, you can gradually become familiar with the key point.

On the basis of understanding the principle of ECC curve above , we know that to create a new curve that can be used for  key pair generation, encryption and decryption and other functions, the most important things include: 1 establishment the elliptic curve  2 the prime field 3 the order of cyclic subgroups. Others, like point operations, key generation (point multiplication algorithm) and other encryption and decryption functions, jdk has implemented for us.

We use the latest repository based on jdk17 for construction: https://github.com/openjdk/jdk17u.

During the process, learning how to build your own JDK is a must: [build jdk guide](https://openjdk.org/groups/build/doc/building.html)



**Implement**

This commit is located in : [jdk-sup-sm2](https://github.com/openjdk/jdk17u/compare/master...jquanC:jdk17u:dev-jdksupsm2)

Like in the section Implement with BC, we implement two sm2 curves: sm2p256c1 and sm2p256c2

Briefly, in [FieldGen.java](https://github.com/openjdk/jdk17u/tree/master/make/jdk/src/classes/build/tools/intpoly/FieldGen.java), we need to prepare the field parameters for the modulus calculation of the sm2 curve at first .

Each curve has its own finite field calculation parameters and cyclic subgroup calculation parameters, namely the prime field size and the order of the cyclic subgroup that actually works.Imitate jdk secp256r1 to generate:

````java
//secp256   
static FieldParams P256 = new FieldParams(
            "IntegerPolynomialP256", 26, 10, 2, 256,
            Arrays.asList(
                    new Term(224, -1),
                    new Term(192, 1),
                    new Term(96, 1),
                    new Term(0, -1)
            ), 
            P256CrSequence(), simpleSmallCrSequence(10)
    );
 static FieldParams O256 = new FieldParams(
            "P256OrderField", 26, 10, 1, 256,
            "FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551",
            orderFieldCrSequence(10), orderFieldSmallCrSequence(10)
    );
````

The ' "IntegerPolynomialP256" 'here use the way to improve the ECC implementation

- [Improved ECC Implementation](https://bugs.openjdk.org/browse/JDK-8204574)

- >The finite field arithmetic in the implementation depends on the fact that the field is defined by a prime that has some structure (e.g. 2^256 - 2^224 + 2^192 + 2^96 - 1). 

  

Then, add the sm2 elliptic curve parameter in sun/security/util/CurveDB.java. The relevant method is:

````java
private static void add(KnownOIDs o, int type, String sfield,
            String a, String b, String x, String y, String n, int h) {....} 
````

Here involves the parameters a, b and G point coordinates (x, y) of the curve equation. 

Every curve declared in the JDK is created and made available externally. It is also necessary to  add supplement  of the curve we created for others to use. In some search method and some map structure, we need to make corresponding supplements.

For example, method  "public static NamedCurve lookup(String name) {..}"  return a NamedCurve for the specified OID/name or null if unknown. Of course, we alse need to add the name of the curve we want to add in SunEC.java. 



**Additional work**

In order to verify whether the sm2 curve we created is correct, I thought of using the ECDSA algorithm based on the sm2 curve.
Of course, this requires further additions to the code in the ECDSA part in the jdk. But fortunately, the method is similar, and I work it out finally. It should be noted that I did not make corresponding additions and modifications under test/jdk/.... .This is not necessary for this task.



For details, please refer to [commit](https://github.com/openjdk/jdk17u/compare/master...jquanC:jdk17u:dev-jdksupsm2)



### Result

We wrote a simple java program to verify that the jdk we built that supports SM2 elliptic curves works correctly:

Check here xxxx

And the result:

````shell
root@VM-0-5-debian:/home/jdk17/jdk17u/build/linux-x86_64-server-release/jdk/bin# ./java UseKeyPair 
ecc sm2p256c1-public key:Sun EC public key, 256 bits
  public x coord: 108215692817942810181640585185388365471717524375043327564736460688225691144182
  public y coord: 114466569395688925905000092749782687391649460749865190674160554055279618581487
  parameters: sm2p256c1 [NIST SM2C1-256,X9.62 prime256c1] (1.2.156.10197.1.301.1)
ecc sm2p256c2-public key:Sun EC public key, 256 bits
  public x coord: 114005523738278428600895603096780935228695818502624297900508758823478099683210
  public y coord: 58896388605639362952778070192345433558006911392475144762944321675349456306262
  parameters: sm2p256c1 [NIST SM2C1-256,X9.62 prime256c1] (1.2.156.10197.1.301.1)
Message to sign and verify:Tencent rhino bird
ecdsa(sm2c1)-algo-signature verify message result:true
ecdsa(sm2c2)-algo-signature verify message result:true
````



# Reference

1 [Elliptic Curve Cryptography: a gentle introduction](https://andrea.corbellini.name/2015/05/17/elliptic-curve-cryptography-a-gentle-introduction/)](https://andrea.corbellini.name/2015/05/17/elliptic-curve-cryptography-a-gentle-introduction/#elliptic-curves)

2 [Elliptic curve point multiplication](https://en.wikipedia.org/wiki/Elliptic_curve_point_multiplication)

3 [Complete addition formulas for prime order elliptic curves](https://eprint.iacr.org/2015/1060.pdf)

4 [SM2椭圆曲线公钥密码算法](https://www.oscca.gov.cn/sca/xxgk/2010-12/17/1002386/files/b791a9f908bb4803875ab6aeeb7b4e03.pdf)

5 [Elliptic Curve Cryptography ](http://aandds.com/blog/ecc.html)

6 [SM2椭圆曲线公钥密码算法推荐曲线参数](https://www.oscca.gov.cn/sca/xxgk/2010-12/17/1002386/files/b965ce832cc34bc191cb1cde446b860d.pdf)

7 [SM2国密算法/椭圆曲线密码学ECC之数学原理](https://www.jianshu.com/p/5b04b66a55a1)

8 [非对称密钥加密算法 RSA/ECC](https://thiscute.world/posts/practical-cryptography-basics-7-asymmetric-key-ciphers/#ecc-%E5%AF%86%E9%92%A5%E5%AF%B9%E7%94%9F%E6%88%90)

9 [SM2demo-Gitee](https://gitee.com/chunhung_chen/sm2demo)

10 [sm2国密算法](http://123.57.9.108/2022/03/15/SM2%E5%9B%BD%E5%AF%86%E7%AE%97%E6%B3%95/)

11 [国密标准](http://c.gb688.cn/bzgk/gb/showGb?type=online&hcno=66A89DD6DA64F49C49456B757BA0624F)

12 [兰修文. ECC计算算法的优化及其在SM2实现中的运用[D].电子科技大学,2019.](https://kns.cnki.net/KCMS/detail/detail.aspx?filename=1019851012.nh&dbname=CMFD202001&dbcode=CMFD&uid=WEEvREdxOWJmbC9oM1NjYkZCcDEyWVNlc0doMDhTYnlhN0paN3JoSHg4WTk=$R1yZ0H6jyaa0en3RxVUd8df-oHi7XMMDo7mtKT6mSmEvTuk11l2gFA!!&v=MzE3NjVSWWFtejExUEhia3FXQTBGckNVUjdpZmJ1ZHNGaW5oVXJ6TlZGMjZGN3U5SDlITnJaRWJQSVIrZm5zNHk=)



# Acknowledge

I would like to thank the organizers of the Tencent Rhino-Bird Competition for organizing this event and for giving the opportunity to participate in this event. This is my first open source project experience and I did learn a lot.
Special thanks to the two mentors for their guidance during this event. I have learned a lot from their valuable advice. Especially a lot of details, their attitude and tolerance has benefited me a lot. Special thanks to @johnsjiang for giving me a lot of guidance and help. Even answering my questions and offering help during some personal time. Thank you very much!







