/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.tls;

//#if !(android)
import bwmorg.bouncycastle.crypto.tls.TlsProtocolHandler;
import bwmorg.bouncycastle.crypto.tls.AlwaysValidVerifyer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Vitaly
 */
public final class TlsMIDP extends TlsIO {
    private TlsProtocolHandler tls;
        public TlsMIDP(InputStream in, OutputStream out) throws IOException {
            tls = new TlsProtocolHandler(in, out);
            tls.connect(new AlwaysValidVerifyer());            
        }

    public InputStream getTlsInputStream() {
        return tls.getTlsInputStream();
    }

    public OutputStream getTlsOutputStream() {
        return tls.getTlsOuputStream();
    }
}

//#endif
