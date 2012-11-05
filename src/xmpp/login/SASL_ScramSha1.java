package xmpp.login;

import util.StringUtils;
//#if (android)
//# import java.security.MessageDigest;
//# import javax.crypto.Mac;
//# import javax.crypto.spec.SecretKeySpec;
//#else
import com.ssttr.crypto.MessageDigest;
import com.ssttr.crypto.HMACSHA1;
//#endif
import java.io.UnsupportedEncodingException;
import java.util.Random;
import util.Strconv;

public class SASL_ScramSha1 {

    private String jid;
    String pass;
    String cnonce;
    String clientFirstMessageBare;
    String lServerSignature;
//#if (android)
//#     Mac hmac;
//#else    
    HMACSHA1 hmac;
//#endif    

    public String init(String jid, String password) {
        this.jid = jid;
        this.pass = password;
        calculateClientFirstMessage();
        return "n,," + clientFirstMessageBare;
    }

    public String response(String challenge) {
        String serverFirstMessage = challenge;
        String clientFinalMessage = "";
        try {
            clientFinalMessage = processServerMessage(serverFirstMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return clientFinalMessage;
    }

    public boolean success(String success) {
        return lServerSignature.equals(success);
    }

    public SASL_ScramSha1() {
//#if (android)
//#         try {
//#             hmac = Mac.getInstance("HmacSHA1");
//#         } catch (Exception e) {
//#             // TODO Auto-generated catch block
//#             e.printStackTrace();
//#         }
//# 
//#else    
        hmac = new HMACSHA1();
//#endif           

    }

    private void xorB(byte[] dest, byte[] source) {
        int l = dest.length;
        for (int i = 0; i < l; i++) {
            dest[i] ^= source[i];
        }
    }

    private String getAttribute(String[] attrs, char id) {
        for (int i = 0; i < attrs.length; i++) {
            String s = attrs[i].toString();
            if (s.charAt(0) == id) {
                return s.substring(2);
            }
        }
        return null;
    }

    protected void calculateClientFirstMessage() {
        Random rnd = new Random(System.currentTimeMillis());
        cnonce = "666" + rnd.nextLong();
        String username = jid.substring(0, jid.indexOf('@'));
        clientFirstMessageBare = "n=" + username + ",r=" + cnonce;
    }

//#if (android)
//# private String processServerMessage(String serverFirstMessage) {
//# 		String[] attrs = serverFirstMessage.split(",");
//# 
//# 		int i=Integer.parseInt( getAttribute(attrs, 'i') );
//# 		String salt=getAttribute(attrs, 's');
//# 		String r=getAttribute(attrs, 'r');
//# 		byte[] pwd;
//# 		try {
//# 			pwd = pass.getBytes("UTF-8");
//# 		} catch (UnsupportedEncodingException e) {
//# 			e.printStackTrace();
//# 			return null;
//# 		}
//# 		byte[] saltedPassword = Hi(pwd, Strconv.fromBase64(salt), i);
//# 		byte[] clientKey = getHMAC(saltedPassword).doFinal( "Client Key".getBytes() );
//# 		MessageDigest sha;
//# 		try {
//# 			sha = MessageDigest.getInstance("SHA-1");
//# 		} catch (Exception e) { return null; }
//# 
//# 		byte[] storedKey = sha.digest(clientKey);
//# 		String clientFinalMessageWithoutProof = "c=biws,r="+r;
//# 		String authMessage = clientFirstMessageBare + "," 
//# 		                   + serverFirstMessage + "," 
//# 				           + clientFinalMessageWithoutProof;
//# 		byte[] clientSignature = getHMAC(storedKey).doFinal( authMessage.getBytes() );
//# 		byte[] clientProof = clientKey.clone();
//# 		xorB(clientProof, clientSignature);
//# 
//# 
//# 		byte[] serverKey = getHMAC(saltedPassword).doFinal("Server Key".getBytes());
//# 
//# 
//# 		byte[] serverSignature = getHMAC(serverKey).doFinal(authMessage.getBytes());
//# 
//# 
//# 		lServerSignature = "v=" + Strconv.toBase64(serverSignature, serverSignature.length);
//# 
//# 
//# 		return clientFinalMessageWithoutProof + ",p=" + Strconv.toBase64(clientProof, clientProof.length);
//# 
//# 
//# 	}
//#     
//#     private Mac getHMAC(byte[] str) {
//#         try {
//#             SecretKeySpec secret = new SecretKeySpec(str, "HmacSHA1");
//#             hmac.init(secret);
//#         } catch (Exception e) {
//#             e.printStackTrace();
//#         }
//#         return hmac;
//#     }
//#     private byte[] Hi(byte[] str, byte[] salt, int i) {
//#         byte[] dest;
//# 
//# 
//#         Mac hmac = getHMAC(str);
//# 
//# 
//#         hmac.update(salt);
//# 
//# 
//#         //INT(1), MSB first
//#         hmac.update((byte) 0);
//#         hmac.update((byte) 0);
//#         hmac.update((byte) 0);
//#         hmac.update((byte) 1);
//# 
//# 
//#         byte[] U = hmac.doFinal();
//# 
//# 
//#         dest = U.clone();
//# 
//# 
//#         i--;
//# 
//# 
//#         while (i > 0) {
//#             U = hmac.doFinal(U);
//#             xorB(dest, U);
//#             i--;
//#         }
//# 
//# 
//#         return dest;
//#     }
//# 
//#else
    private String processServerMessage(String serverFirstMessage) throws Exception {
        String[] attrs = StringUtils.explode(serverFirstMessage, ',');

        int i = Integer.parseInt(getAttribute(attrs, 'i'));
        String salt = getAttribute(attrs, 's');
        String r = getAttribute(attrs, 'r');

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
            byte[] bs = Strconv.fromBase64(salt);
            saltedPassword = Hi(pwd, bs, i);
            HMACSHA1 mac = getHMAC(saltedPassword);
            byte[] ck = "Client Key".getBytes();

            clientKey = mac.hmac(ck);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        sha.update(clientKey, 0, clientKey.length);
        byte[] storedKey = new byte[20];
        sha.digest(storedKey, 0, storedKey.length);
        String clientFinalMessageWithoutProof = "c=biws,r=" + r;

        String authMessage = clientFirstMessageBare + ","
                + serverFirstMessage + ","
                + clientFinalMessageWithoutProof;

        byte[] clientSignature = getHMAC(storedKey).hmac(authMessage.getBytes());
        byte[] clientProof = new byte[clientKey.length];
        System.arraycopy(clientKey, 0, clientProof, 0, clientKey.length);
        xorB(clientProof, clientSignature);

        byte[] serverKey = getHMAC(saltedPassword).hmac("Server Key".getBytes());
        byte[] serverSignature = getHMAC(serverKey).hmac(authMessage.getBytes());
        lServerSignature = "v=" + util.Strconv.toBase64(serverSignature, serverSignature.length);

        return clientFinalMessageWithoutProof + ",p=" + util.Strconv.toBase64(clientProof, clientProof.length);

    }
    
    private byte[] Hi(byte[] str, byte[] salt, int i) {
        HMACSHA1 mac = getHMAC(str);
        byte[] ooo1 = {0, 0, 0, 1};
        byte[] m = new byte[salt.length + 4];
        System.arraycopy(salt, 0, m, 0, salt.length);
        System.arraycopy(ooo1, 0, m, salt.length, 4);
        byte[] U = mac.hmac(m);
        byte[] dest = new byte[U.length];
        System.arraycopy(U, 0, dest, 0, U.length);
        i--;
        while (i > 0) {
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
//#endif    
}
