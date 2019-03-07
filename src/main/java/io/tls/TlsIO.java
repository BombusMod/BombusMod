/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.Socket;

/**
 *
 * @author Vitaly
 */
public abstract class TlsIO {
    
    public static TlsIO create(Socket conn, InputStream in, OutputStream out, String authHost)
            throws Exception {
        Class<?> clazz = Class.forName("org.bombusmod.android.tls.AndroidTls");
        Constructor<?> constructor = clazz.getConstructor(Socket.class, InputStream.class, OutputStream.class, String.class);
        Object instance = constructor.newInstance(conn, in, out, authHost);
        return (TlsIO)instance;
    }
    
    public abstract InputStream getTlsInputStream()  throws IOException;
    public abstract OutputStream getTlsOutputStream()  throws IOException;
}
