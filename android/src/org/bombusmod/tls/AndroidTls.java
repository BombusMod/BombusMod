/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bombusmod.tls;

import android.net.SSLCertificateSocketFactory;
import io.tls.TlsIO;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author Vitaly
 */
public final class AndroidTls extends TlsIO {

    SSLSocket tls;
    SSLSocketFactory sslFactory;

    public AndroidTls(Socket socket, InputStream in, OutputStream out,
            String authHost)
            throws NoSuchAlgorithmException, KeyManagementException, IOException {
        sslFactory = SSLCertificateSocketFactory.getInsecure(0, null);
        tls = (SSLSocket) (sslFactory.createSocket(socket, authHost, socket.getPort(), true));
        tls.setUseClientMode(true);
    }

    public InputStream getTlsInputStream() throws IOException {
        return tls.getInputStream();
    }

    public OutputStream getTlsOutputStream() throws IOException {
        return tls.getOutputStream();
    }
}
