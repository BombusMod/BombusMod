/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io;

/**
 *
 * @author Vitaly
 */
public class HttpBindConnection extends HttpXmppConnection {

    public long rid;
    public String sid;
    public int waitPeriod;

    public HttpBindConnection(String host, String bindUrl) {
        super(host, bindUrl);
        contentType = "text/xml; charset=utf-8";
        rid = System.currentTimeMillis();
    }

    public String wrap(String xmppData) {
        if (xmppData.startsWith("<body")) {
            return xmppData;
        }
        StringBuffer body = new StringBuffer();
        body.append("<body xmlns='http://jabber.org/protocol/httpbind' sid='").append(sid).append("' rid='").append(nextRid());
        if (xmppData.equals("")) {
            body.append("'/>");
        } else {
            body.append("'>");
            body.append(xmppData).append("</body>");
        }
        return body.toString();
    }

    public String nextRid() {
        return Long.toString(rid++);
    }

}
