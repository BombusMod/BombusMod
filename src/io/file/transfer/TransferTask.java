/*
 * TransferTask.java
 *
 * Created on 28.10.2006, 17:00
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package io.file.transfer;

import Client.Jid;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import images.RosterIcons;
import io.file.FileIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import locale.SR;
import Colors.ColorTheme;
import ui.IconTextElement;
import xmpp.XmppError;

import com.ssttr.crypto.SHA1;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import Client.StaticData;
import ServiceDiscovery.DiscoForm;
import com.alsutton.jabber.datablocks.Message;
import io.SOCKS5Stream;
import util.Strconv;

/**
 *
 * @author Evg_S
 */
public class TransferTask
        extends IconTextElement
        implements Runnable {

    public final static int COMPLETE = 1;
    public final static int PROGRESS = 3;
    public final static int ERROR = 4;
    public final static int NONE = 5;
    public final static int HANDSHAKE = 6;
    public final static int IN_ASK = 7;
    public final static int PROXYACTIVATE = 8;
    public final static int PROXYOPEN = 9;
    public int state = NONE;
    private boolean sending;
    boolean showEvent;
    boolean isBytes;
    byte[] bytes;
    Jid jid;
    String id;
    String sid;
    String fileName;
    String description;
    String errMsg;
    int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    private OutputStream os;
    private InputStream is;
    public String method;
    private Vector methods;
    public Vector streamhosts;
    long started;
    long finished;
    String host, port;
    
    /** Creates TransferTask for incoming file */
    public TransferTask(Jid jid, String id, String sid, String name, String description, int size, Vector methods) {
        super(RosterIcons.getInstance());
        state = IN_ASK;
        showEvent = true;
        this.jid = jid;
        this.id = id;
        this.sid = sid;
        this.fileName = name;
        this.description = description;
        this.fileSize = size;
        this.methods = methods;
    }

    /**
     * Sending constructor
     */
    public TransferTask(Jid jid, String sid, String fileName, String description, boolean isBytes, byte[] bytes) {
        super(RosterIcons.getInstance());
        state = HANDSHAKE;
        sending = true;
        //showEvent=true;
        this.jid = jid;
        this.sid = sid;
        this.fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        this.description = description;

        this.isBytes = isBytes;
        this.bytes = bytes;

        //this.fileSize=size;
        //this.methods=methods;
        if (!isBytes) {
            try {
                file = FileIO.createConnection(fileName);
                is = file.openInputStream();

                fileSize = (int) file.fileSize();
            } catch (Exception e) {
//#ifdef DEBUG
//#                 e.printStackTrace();
//#endif
                state = ERROR;
                errMsg = SR.MS_CANT_OPEN_FILE;
                showEvent = true;
            }
        } else {
            is = new ByteArrayInputStream(bytes);
            fileSize = bytes.length;

        }
    }

    public int getImageIndex() {
        return state;
    }

    public int getColor() {
        return (sending) ? ColorTheme.getColor(ColorTheme.MESSAGE_OUT) : ColorTheme.getColor(ColorTheme.MESSAGE_IN);
    }

    public void drawItem(Graphics g, int ofs, boolean sel) {
        int xpgs = (g.getClipWidth() / 3) * 2;
        int pgsz = g.getClipWidth() - xpgs - 4;
        int filled = (fileSize == 0) ? 0 : (pgsz * filePos) / fileSize;

        int oldColor = g.getColor();
        g.setColor(0xffffff);

        g.fillRect(xpgs, 3, pgsz, getVHeight() - 6);
        g.setColor(0x668866);
        g.drawRect(xpgs, 3, pgsz, getVHeight() - 6);
        g.fillRect(xpgs, 3, filled, getVHeight() - 6);
        g.setColor(oldColor);

        super.drawItem(g, ofs, sel);
        showEvent = false;
    }

    public String toString() {
        return fileName;
    }

    void decline() {
        finished = System.currentTimeMillis();
        JabberDataBlock reject = new Iq(jid.toString(), Iq.TYPE_ERROR, id);
        reject.addChild(new XmppError(XmppError.NOT_ALLOWED, "declined by user"));
        TransferDispatcher.getInstance().send(reject, true);

        state = ERROR;
        errMsg = SR.MS_REJECTED;
        showEvent = true;
    }

    void accept() {
        String[] methodNames = new String[2];
        methodNames[0] = TransferDispatcher.NS_BYTESTREAMS;
        methodNames[1] = TransferDispatcher.NS_IBB;
        for (int i = 0; i < methodNames.length; i++) {
            String nextMethod = methodNames[i];
            if (methods.indexOf(nextMethod) >= 0) {
                method = nextMethod;
                break;
            }
        }
        if (method.length() != 0) {
            started = System.currentTimeMillis();
            new Thread(this).start();
        } else {
            JabberDataBlock badreq = new Iq(jid.toString(), Iq.TYPE_ERROR, id);
            JabberDataBlock err = new XmppError(XmppError.BAD_REQUEST, "no known methods").construct();
            JabberDataBlock novalid = new JabberDataBlock("no-valid-streams", null, null);
            novalid.setNameSpace(TransferDispatcher.NS_SI);
            err.addChild(novalid);
            badreq.addChild(err);
            TransferDispatcher.getInstance().send(badreq, false);
            state = ERROR;
            errMsg = SR.MS_REJECTED;
            showEvent = true;
        }
    }

    void writeFile(byte b[]) {
        try {
            os.write(b);
            filePos += b.length;
            state = PROGRESS;
        } catch (IOException ex) {
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
            state = ERROR;
            errMsg = "Write error";
            showEvent = true;
            //todo: terminate transfer
        }
    }

    int readFile(byte b[]) {
        try {
            int len = is.read(b);
            if (len < 0) {
                len = 0;
            }
            filePos += len;
            state = PROGRESS;
            return len;
        } catch (IOException ex) {
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
            state = ERROR;
            errMsg = "Read error";
            showEvent = true;
            //todo: terminate transfer
            return 0;
        }
    }

    boolean isAcceptWaiting() {
        return state == IN_ASK;
    }

    void closeFile() {
        finished = System.currentTimeMillis();
        try {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
            if (file != null) {
                file.close();
            }
            if (state != ERROR) {
                state = COMPLETE;
            }
        } catch (Exception ex) {
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
            errMsg = "File close error";
            state = ERROR;
        }
        file = null;
        is = null;
        os = null;
        showEvent = true;
    }

    void sendInit() {
        started = System.currentTimeMillis();
        if (state == ERROR) {
            return;
        }

        JabberDataBlock iq = new Iq(jid.toString(), Iq.TYPE_SET, sid);

        JabberDataBlock si = iq.addChildNs("si", TransferDispatcher.NS_SI);
        si.setAttribute("id", sid);
        si.setAttribute("mime-type", "text/plain");
        si.setAttribute("profile", "http://jabber.org/protocol/si/profile/file-transfer");

        JabberDataBlock f = si.addChildNs("file", "http://jabber.org/protocol/si/profile/file-transfer");
        f.setAttribute("name", fileName);
        f.setAttribute("size", String.valueOf(fileSize));

        f.addChild("desc", description);

        JabberDataBlock feature = si.addChildNs("feature", "http://jabber.org/protocol/feature-neg");

        JabberDataBlock x = feature.addChildNs("x", DiscoForm.NS_XDATA);
        x.setTypeAttribute("form");

        JabberDataBlock field = x.addChild("field", null);
        field.setTypeAttribute("list-single");
        field.setAttribute("var", "stream-method");
        field.addChild("option", null).addChild("value", TransferDispatcher.NS_BYTESTREAMS);
        field.addChild("option", null).addChild("value", TransferDispatcher.NS_IBB);
        TransferDispatcher.getInstance().send(iq, true);

    }

    void initIBB() {
        method = TransferDispatcher.NS_IBB;
        JabberDataBlock iq = new Iq(jid.toString(), Iq.TYPE_SET, sid);
        JabberDataBlock open = iq.addChildNs("open", TransferDispatcher.NS_IBB);
        open.setAttribute("sid", sid);
        open.setAttribute("block-size", "2048");
        open.setAttribute("stanza", "message");
        TransferDispatcher.getInstance().send(iq, false);
    }
    protected SOCKS5Stream proxystream;
    
    void initProxy() {
        String proxyjid = TransferConfig.getInstance().ftProxy;        
        JabberDataBlock iq = new Iq(proxyjid, Iq.TYPE_GET, "discovery" + sid);
        iq.addChildNs("query", "http://jabber.org/protocol/bytestreams");
        TransferDispatcher.getInstance().send(iq, false);        
    }

    void initBytestreams(String host, String port) {
        method = TransferDispatcher.NS_BYTESTREAMS;
        this.host = host;
        this.port = port;
        JabberDataBlock iq = new Iq(jid.toString(), Iq.TYPE_SET, sid);
        JabberDataBlock query = iq.addChildNs("query", TransferDispatcher.NS_BYTESTREAMS);
        query.setAttribute("sid", sid);
        query.setAttribute("mode", "tcp");
        JabberDataBlock streamhost = query.addChild("streamhost", null);
        streamhost.setAttribute("jid", TransferConfig.getInstance().ftProxy);
        streamhost.setAttribute("host", host);
        streamhost.setAttribute("port", port);
        TransferDispatcher.getInstance().send(iq, false);
        state = PROXYACTIVATE;
    }

    void ProxyActivate() {
        JabberDataBlock iq = new Iq(TransferConfig.getInstance().ftProxy, Iq.TYPE_SET, "activate" + sid);
        JabberDataBlock query = iq.addChildNs("query", TransferDispatcher.NS_BYTESTREAMS);
        query.setAttribute("sid", sid);
        query.addChild("activate", jid.toString());
        TransferDispatcher.getInstance().send(iq, false);
        state = PROXYOPEN;
    }

    public boolean openStreams(final String host, int port) {
        try {
            StreamConnection connection = (StreamConnection) Connector.open("socket://" + host + ":" + port);
            proxystream = new SOCKS5Stream(connection);
            return true;
        } catch (Exception e) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
        }
        return false;
    }

    public boolean connectStream() throws IOException, InterruptedException {
        new Thread(this).start();
        Thread.sleep(2000); // wait for proxy handshake
        return true;
    }

    public void run() {
        if (state == IN_ASK) {
            try {
                file = FileIO.createConnection(filePath + fileName);
                os = file.openOutputStream();
            } catch (Exception e) {
//#ifdef DEBUG
//#                 e.printStackTrace();
//#endif
                decline();
                return;
            }

            JabberDataBlock accept = new Iq(jid.toString(), Iq.TYPE_RESULT, id);

            JabberDataBlock si = accept.addChildNs("si", TransferDispatcher.NS_SI);

            JabberDataBlock feature = si.addChildNs("feature", "http://jabber.org/protocol/feature-neg");

            JabberDataBlock x = feature.addChildNs("x", DiscoForm.NS_XDATA);
            x.setTypeAttribute("submit");

            JabberDataBlock field = x.addChild("field", null);
            field.setAttribute("var", "stream-method");
            field.addChild("value", method);

            TransferDispatcher.getInstance().send(accept, false);

            state = HANDSHAKE;
            return;

        }
        if (method.equals(TransferDispatcher.NS_IBB)) {
            byte buf[] = new byte[2048];
            int seq = 0;
            try {
                while (true) {
                    int sz = readFile(buf);
                    if (sz == 0) {
                        break;
                    }
                    JabberDataBlock msg = new Message(jid.toString());

                    JabberDataBlock data = msg.addChildNs("data", TransferDispatcher.NS_IBB);
                    data.setAttribute("sid", sid);
                    data.setAttribute("seq", String.valueOf(seq));
                    seq++;
                    data.setText(Strconv.toBase64(buf, sz));

                    JabberDataBlock amp = msg.addChildNs("amp", "http://jabber.org/protocol/amp");

                    JabberDataBlock rule;

                    rule = amp.addChild("rule", null);
                    rule.setAttribute("condition", "deliver-at");
                    rule.setAttribute("value", "stored");
                    rule.setAttribute("action", "error");

                    rule = amp.addChild("rule", null);
                    rule.setAttribute("condition", "match-resource");
                    rule.setAttribute("value", "exact");
                    rule.setAttribute("action", "error");

                    TransferDispatcher.getInstance().send(msg, false);
                    TransferDispatcher.getInstance().repaintNotify();

                    Thread.sleep(1500L); //shaping traffic
                }
            } catch (Exception e) { /*null pointer exception if terminated*/

            }
            closeFile();
            JabberDataBlock iq = new Iq(jid.toString(), Iq.TYPE_SET, "close");
            JabberDataBlock close = iq.addChildNs("close", TransferDispatcher.NS_IBB);
            close.setAttribute("sid", sid);
            TransferDispatcher.getInstance().send(iq, false);
        } else {
            if (state == PROXYACTIVATE) {
                String proxyjid = null;
                if (!sending) {
                    int size = streamhosts.size();
                    boolean success = false;
                    for (int i = 0; i < size; i++) {
                        JabberDataBlock nexthost = (JabberDataBlock) streamhosts.elementAt(i);
                        if (nexthost.getTagName().equals("streamhost")) {
                            String proxyhost = nexthost.getAttribute("host");
                            String proxyport = nexthost.getAttribute("port");
                            proxyjid = nexthost.getAttribute("jid");
                            if (proxyhost.startsWith("192.168.")) {
                                continue;
                            }
                            if (proxyhost.startsWith("10.")) {
                                continue;
                            }
                            if (proxyhost.startsWith("172.16.")) {
                                continue;
                            }
                            if (proxyhost != null && proxyport != null) {
                                try {
                                    openStreams(proxyhost, Integer.parseInt(proxyport));
                                    success = true;
                                    //System.out.println("Opened: " + proxyhost + ":" + proxyport);
                                    break;
                                } catch (Exception e) {
//#ifdef DEBUG
//#                                     e.printStackTrace();
//#endif
                                }
                            }
                        }
                    }
                    if (!success) {
                        cancel();
                    }
                }
                try {
                    byte[] socks5Connect = {
                        0x05, // VER
                        0x01, // 1 method
                        0x00, // No authentication
                    };
                    proxystream.send(socks5Connect);
                    proxystream.flush();
                    byte[] readbuf = new byte[256];
                    proxystream.read(readbuf); // Waiting for response;
                    byte[] socks5CommandStart = {
                        0x05, // VER
                        0x01, // CMD = CONNECT
                        0x00, // RSV
                        0x03, // ATYP = domain
                    };
                    SHA1 Command = new SHA1();
                    Command.init();
                    String verifyString = (sending) ? StaticData.getInstance().roster.myJid.toString() + jid : jid + StaticData.getInstance().roster.myJid.toString();
                    Command.updateASCII(sid + verifyString);
                    Command.finish();
                    byte[] socks5CommandHost = Command.getDigestHex().getBytes();
                    byte[] socks5CommandFinish = {0x00, 0x00};

                    byte[] socks5Command = new byte[socks5CommandStart.length + 1 + socks5CommandHost.length + socks5CommandFinish.length];
                    System.arraycopy(socks5CommandStart, 0, socks5Command, 0, socks5CommandStart.length);
                    socks5Command[socks5CommandStart.length] = (byte) socks5CommandHost.length;
                    System.arraycopy(socks5CommandHost, 0, socks5Command, socks5CommandStart.length + 1, socks5CommandHost.length);
                    System.arraycopy(socks5CommandFinish, 0, socks5Command, socks5CommandStart.length + 1 + socks5CommandHost.length, socks5CommandFinish.length);
                    proxystream.send(socks5Command);
                    proxystream.flush();
                    proxystream.read(readbuf); // Waiting for response;             
                } catch (IOException e) {
                    cancel();
                    return;
                }
                if (!sending) {
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ex) {
//#ifdef DEBUG                        
//#                         ex.printStackTrace();
//#endif                        
                    }
                    Iq notify = new Iq(jid.toString(), Iq.TYPE_RESULT, id);
                    JabberDataBlock query = notify.addChildNs("query", TransferDispatcher.NS_BYTESTREAMS);
                    JabberDataBlock streamused = query.addChild("streamhost-used", null);
                    streamused.setAttribute("jid", proxyjid);
                    TransferDispatcher.getInstance().send(notify, false);
                    state = TransferTask.PROXYOPEN;
                    byte buf[] = new byte[2048];
                    try {
                        int readed;
                        while ((readed = proxystream.read(buf)) > 0) {
                            byte buf2[] = new byte[readed];
                            System.arraycopy(buf, 0, buf2, 0, buf2.length);
                            writeFile(buf2);
                            TransferDispatcher.getInstance().repaintNotify();
                        }
                        closeFile();
                        proxystream.close();
                    } catch (Exception e) {
//#ifdef DEBUG
//#                         e.printStackTrace();
//#endif
                        cancel();
                    }
                    TransferDispatcher.getInstance().eventNotify();
                }
            } else {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException ex) {
//#ifdef DEBUG                        
//#                     ex.printStackTrace();
//#endif                        
                }

                byte buf[] = new byte[2048];
                try {
                    int cnt;
                    while ((cnt = readFile(buf)) != 0) {
                        proxystream.send(buf, 0, cnt);
                        TransferDispatcher.getInstance().repaintNotify();
                        // Thread.sleep( 500L ); //shaping traffic
                    }
                    proxystream.flush();
                    closeFile();
                    proxystream.close();

                } catch (Exception ex) {
//#ifdef DEBUG
//#                 ex.printStackTrace();
//#endif
                }
                TransferDispatcher.getInstance().eventNotify();
            }
        }
    }

    void startTransfer(String id) {
        this.id = id;
        new Thread(this).start();
    }

    boolean isStopped() {
        return (state == COMPLETE || state == ERROR);
    }

    boolean isStarted() {
        return (state != NONE && state != IN_ASK);
    }

    public void cancel() {
        if (isStopped()) {
            return;
        }
        state = ERROR;
        errMsg = "Canceled";
        if (!isBytes) {
            closeFile();
        } else {
            bytes = null;
        }
    }
}
