package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

/**
 * DSTU 4145-2002
 * <p>
 * National Ukrainian standard of digital signature based on elliptic curves (DSTU 4145-2002).
 * </p>
 */
public class DSTU4145Signer
    implements DSA
{

    private ECKeyParameters key;
    private SecureRandom random;

    public void init(boolean forSigning, CipherParameters param)
    {
        if (forSigning)
        {
            if (param instanceof ParametersWithRandom)
            {
                ParametersWithRandom rParam = (ParametersWithRandom)param;

                this.random = rParam.getRandom();
                param = rParam.getParameters();
            }
            else
            {
                this.random = new SecureRandom();
            }

            this.key = (ECPrivateKeyParameters)param;
        }
        else
        {
            this.key = (ECPublicKeyParameters)param;
        }

    }

    public BigInteger[] generateSignature(byte[] message)
    {
        byte[] mRev = new byte[message.length];
        for (int i = 0; i != mRev.length; i++)
        {
            mRev[i] = message[mRev.length - 1 - i];
        }

        ECFieldElement h = hash2FieldElement(key.getParameters().getCurve(), mRev);
        if (h.toBigInteger().signum() == 0)
        {
            h = key.getParameters().getCurve().fromBigInteger(BigInteger.ONE);
        }

        BigInteger e, r, s;
        ECFieldElement Fe, y;

        do
        {
            do
            {
                do
                {
                    e = generateRandomInteger(key.getParameters().getN(), random);
                    Fe = key.getParameters().getG().multiply(e).getX();
                }
                while (Fe.toBigInteger().signum() == 0);

                y = h.multiply(Fe);
                r = fieldElement2Integer(key.getParameters().getN(), y);
            }
            while (r.signum() == 0);

            s = r.multiply(((ECPrivateKeyParameters)key).getD()).add(e).mod(key.getParameters().getN());
        }
        while (s.signum() == 0);

        return new BigInteger[]{r, s};
    }

    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s)
    {
        byte[] mRev = new byte[message.length]; // conversion is little-endian
        for (int i = 0; i != mRev.length; i++)
        {
            mRev[i] = message[mRev.length - 1 - i];
        }

        if (r.signum() == 0 || s.signum() == 0)
        {
            return false;
        }
        if (r.compareTo(key.getParameters().getN()) >= 0 || s.compareTo(key.getParameters().getN()) >= 0)
        {
            return false;
        }

        ECFieldElement h = hash2FieldElement(key.getParameters().getCurve(), mRev);
        if (h.toBigInteger().signum() == 0)
        {
            h = key.getParameters().getCurve().fromBigInteger(BigInteger.ONE);
        }

        ECPoint R = ECAlgorithms.sumOfTwoMultiplies(key.getParameters().getG(), s, ((ECPublicKeyParameters)key).getQ(), r);
        ECFieldElement y = h.multiply(R.getX());
        return fieldElement2Integer(key.getParameters().getN(), y).compareTo(r) == 0;
    }

    /**
     * Generates random integer such, than its bit length is less than that of n
     */
    private static BigInteger generateRandomInteger(BigInteger n, SecureRandom random)
    {
        return new BigInteger(n.bitLength() - 1, random);
    }

    private static ECFieldElement hash2FieldElement(ECCurve curve, byte[] hash)
    {
        BigInteger num = new BigInteger(1, hash);
        while (num.bitLength() >= curve.getFieldSize())
        {
            num = num.clearBit(num.bitLength() - 1);
        }

        return curve.fromBigInteger(num);
    }

    private static BigInteger fieldElement2Integer(BigInteger n, ECFieldElement fieldElement)
    {
        BigInteger num = fieldElement.toBigInteger();
        while (num.bitLength() >= n.bitLength())
        {
            num = num.clearBit(num.bitLength() - 1);
        }

        return num;
    }
}
