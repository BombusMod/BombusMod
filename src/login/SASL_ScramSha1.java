package login;

import util.StringUtils;
import com.ssttr.crypto.HMACSHA1;
import com.ssttr.crypto.SHA1;
import com.ssttr.crypto.MessageDigest;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Vector;
import util.Strconv;


public class SASL_ScramSha1 {
    private String jid;
    String pass;
    String cnonce;
    String clientFirstMessageBare;
    String lServerSignature;
    HMACSHA1 hmac;

    public String init(String jid, String password) {
        this.jid = jid;
        this.pass = password;
        calculateClientFirstMessage();
        return "n,,"+clientFirstMessageBare;
    }

    public String response(String challenge) {
        String serverFirstMessage = challenge;
        String clientFinalMessage = processServerMessage(serverFirstMessage);
        return clientFinalMessage;
    }

    public boolean success(String success) {
        return lServerSignature.equals(success);
    }
        
    public SASL_ScramSha1() {
        hmac = new HMACSHA1();
    }
        
    private void xorB(byte[] dest, byte[] source) {
        int l = dest.length;
        for (int i = 0; i < l; i++) {
            dest[i] ^= source[i];
        }
    }

    private String getAttribute(Vector attrs, char id) {
        for (int i=0;i<attrs.size();i++) {
            String s = attrs.elementAt(i).toString();
            if (s.charAt(0) == id) return s.substring(2);
        }
        return null;
    }
    
    private String processServerMessage(String serverFirstMessage) {
        Vector attrs = StringUtils.split(serverFirstMessage,',');
        
        int i=Integer.parseInt( getAttribute(attrs, 'i') );
        String salt=getAttribute(attrs, 's');
        String r=getAttribute(attrs, 'r');
        
        byte[] pwd;
        
        try {
            pwd = pass.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        byte[] clientKey;
        byte[] saltedPassword;
        try {
            byte []bs = Strconv.fromBase64(salt);
            saltedPassword = Hi(pwd, bs, i);
            HMACSHA1 mac = getHMAC(saltedPassword);
            byte []ck = "Client Key".getBytes();
            
            clientKey = mac.hmac(ck);            
        } catch (Exception e){
            System.out.println("new byte[1]!!!!");
            return null;
        }
        
        MessageDigest sha;
        sha = new SHA1();
        sha.init();
        sha.update(clientKey);
        sha.finish();
        byte[] storedKey = sha.getDigestBits();
    
        String clientFinalMessageWithoutProof = "c=biws,r="+r;
        
        String authMessage = clientFirstMessageBare + "," 
                           + serverFirstMessage + "," 
                           + clientFinalMessageWithoutProof;
                
        byte[] clientSignature = getHMAC(storedKey).hmac(authMessage.getBytes());
        byte[] clientProof = new byte[clientKey.length];
        System.arraycopy(clientKey, 0, clientProof, 0, clientKey.length);
        xorB(clientProof, clientSignature);
        
        byte[] serverKey =  getHMAC(saltedPassword).hmac("Server Key".getBytes());
        byte[] serverSignature = getHMAC(serverKey).hmac(authMessage.getBytes());
        lServerSignature = "v="+util.Strconv.toBase64(serverSignature, serverSignature.length);
        
        return clientFinalMessageWithoutProof + ",p=" + util.Strconv.toBase64(clientProof, clientProof.length);
        
    }

    protected void calculateClientFirstMessage() {
        Random rnd = new Random(System.currentTimeMillis());
        cnonce="QD" + rnd.nextLong();
        String username = jid.substring(0, jid.indexOf('@'));
        clientFirstMessageBare = "n="+username + ",r=" + cnonce;
    }
    
    private byte[] Hi(byte[] str, byte[] salt, int i)  {
        HMACSHA1 mac = getHMAC(str);
        byte []ooo1 = {0,0,0,1};
        byte []m = new byte[salt.length+4];
        System.arraycopy(salt, 0, m, 0, salt.length);
        System.arraycopy(ooo1, 0, m, salt.length, 4);
        byte [] U = mac.hmac(m);        
        byte[] dest = new byte[U.length];
        System.arraycopy(U, 0, dest, 0, U.length);
        i--;
        while (i>0) {
            U = mac.hmac(U);
            xorB(dest, U);
            i--;
        }
        return dest;
    }

    private HMACSHA1 getHMAC(byte[] str) {         
        hmac.init(str);
        return hmac;
    }
    
    public void test() {
        pass="pencil";
        cnonce = "fyko+d2lbbFgONRv9qkxdawL";
        clientFirstMessageBare="n=user,r=fyko+d2lbbFgONRv9qkxdawL";
        String clientFinalMessage = processServerMessage("r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,s=QSXCR+Q6sek8bf92,i=4096");
        System.out.println("SCRAM-SHA1 "+clientFinalMessage);
        System.out.println("SCRAM-SHA1 "+lServerSignature);
    }

}

