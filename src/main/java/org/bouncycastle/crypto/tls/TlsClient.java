package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;

public interface TlsClient {

    void init(TlsClientContext context);

    ProtocolVersion getClientHelloRecordLayerVersion();

    ProtocolVersion getClientVersion();

    int[] getCipherSuites();

    short[] getCompressionMethods();

    // Hashtable is (Integer -> byte[])
    Hashtable getClientExtensions() throws IOException;

    ProtocolVersion getMinimumVersion();

    void notifyServerVersion(ProtocolVersion selectedVersion) throws IOException;

    void notifySessionID(byte[] sessionID);

    void notifySelectedCipherSuite(int selectedCipherSuite);

    void notifySelectedCompressionMethod(short selectedCompressionMethod);

    void notifySecureRenegotiation(boolean secureNegotiation) throws IOException;

    // Hashtable is (Integer -> byte[])
    void processServerExtensions(Hashtable serverExtensions) throws IOException;

    TlsKeyExchange getKeyExchange() throws IOException;

    TlsAuthentication getAuthentication() throws IOException;

    TlsCompression getCompression() throws IOException;

    TlsCipher getCipher() throws IOException;
}
