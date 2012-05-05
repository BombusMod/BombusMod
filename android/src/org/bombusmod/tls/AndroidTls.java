/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bombusmod.tls;

import io.tls.TlsIO;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

/**
 *
 * @author Vitaly
 */
public final class AndroidTls extends TlsIO {
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) 
                    throws CertificateException {
            }
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) 
                    throws CertificateException {
            }
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }
    };
    
    SSLSocket tls;
    
    public AndroidTls(Socket socket, InputStream in, OutputStream out, 
            String authHost) 
            throws NoSuchAlgorithmException, KeyManagementException, IOException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        SSLSocketFactory sslFactory = 
                (SSLSocketFactory) sslContext.getSocketFactory();
        tls = (SSLSocket)(sslFactory.createSocket(socket, authHost, socket.getPort(), true));
    }
    
    public InputStream getTlsInputStream() throws IOException {
        return tls.getInputStream();
    }

    public OutputStream getTlsOutputStream() throws IOException {
        return tls.getOutputStream();
    }
}
