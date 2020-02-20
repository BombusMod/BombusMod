/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bombusmod.tls;

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

    public AndroidTls(Socket socket, InputStream in, OutputStream out,
            String authHost)
            throws IOException {
        sslFactory = BombusModActivity.getInstance().getSslContext().getSocketFactory();
        tls = (SSLSocket) (sslFactory.createSocket(socket, authHost, socket.getPort(), true));
        tls.setUseClientMode(true);
        tls.startHandshake();
    }

    public InputStream getTlsInputStream() throws IOException {
        return tls.getInputStream();
    }

    public OutputStream getTlsOutputStream() throws IOException {
        return tls.getOutputStream();
    }
}
