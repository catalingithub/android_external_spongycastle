package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

/**
 * class for describing an ECPoint as a DER object.
 */
public class X9ECPoint
    implements DEREncodable
{
    ECPoint p;

    public X9ECPoint(
        ECPoint p)
    {
        this.p = p;
    }

    public X9ECPoint(
        ECCurve          c,
        ASN1OctetString  s)
    {
        this.p = c.decodePoint(s.getOctets());
    }

    public ECPoint getPoint()
    {
        return p;
    }

    /**
     * <pre>
     *  ECPoint ::= OCTET STRING
     * </pre>
     * <p>
     * Octet string produced using ECPoint.getEncoded().
     */
    public DERObject getDERObject()
    {
        return new DEROctetString(p.getEncoded());
    }
}
