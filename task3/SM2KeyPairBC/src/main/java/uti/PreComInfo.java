package uti;

import org.bouncycastle.math.ec.ECPoint;

public class PreComInfo {
    public ECPoint[] preCompute = null;
    public ECPoint[] preComputeDigitWeight = null;
    public int[] windowDigit = null;//k 转为 w进制
    public int windowWidth = 0;
}
