/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bombusmod.android.tls;

import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;

import org.bombusmod.App;
import org.bombusmod.BombusModActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import io.tls.TlsIO;

/**
 *
 * @author Vitaly
 */
public final class AndroidTls extends TlsIO {

    SSLSocket tls;
    SSLSocketFactory sslFactory;
    SSLSessionCache sslSessionCache;

    public AndroidTls(Socket socket, InputStream in, OutputStream out,
            String authHost)
            throws IOException {
        sslSessionCache = new SSLSessionCache(App.getInstance());
        sslFactory = SSLCertificateSocketFactory.getDefault(60000, sslSessionCache);
        tls = (SSLSocket) (sslFactory.createSocket(socket, authHost, socket.getPort(), true));
        tls.startHandshake();
    }

    public InputStream getTlsInputStream() throws IOException {
        return tls.getInputStream();
    }

    public OutputStream getTlsOutputStream() throws IOException {
        return tls.getOutputStream();
    }
}
