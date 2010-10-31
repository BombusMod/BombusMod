/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io;

import javax.microedition.pki.Certificate;
import javax.microedition.pki.CertificateException;

/**
 *
 * @author evgs
 */
public class SSLExceptionDecoder {

    private static String getErrorName(int code) {
        switch (code) {
            case CertificateException.BAD_EXTENSIONS:
                return "\nBAD_EXTENSIONS: certificate has unrecognized critical extensions";
            case CertificateException.BROKEN_CHAIN:
                return "\nBROKEN_CHAIN: certificate in a chain was not issued by the next authority in the chain";
            case CertificateException.CERTIFICATE_CHAIN_TOO_LONG:
                return "\nCERTIFICATE_CHAIN_TOO_LONG: server certificate chain exceeds the length allowed by an issuer's policy.";
            case CertificateException.EXPIRED:
                return "\nEXPIRED: certificate is expired.";
            case CertificateException.INAPPROPRIATE_KEY_USAGE:
                return "\nINAPPROPRIATE_KEY_USAGE: certificate public key has been used in way deemed inappropriate by the issuer.";
            case CertificateException.MISSING_SIGNATURE:
                return "\nMISSING_SIGNATURE: certificate object does not contain a signature.";
            case CertificateException.NOT_YET_VALID:
                return "\nNOT_YET_VALID: certificate is from time machine ;)";
            case CertificateException.ROOT_CA_EXPIRED:
                return "\nROOT_CA_EXPIRED: root CA's public key is expired.";
            case CertificateException.SITENAME_MISMATCH:
                return "\nSITENAME_MISMATCH: certificate does not contain the correct site name.";
            case CertificateException.UNAUTHORIZED_INTERMEDIATE_CA:
                return "\nUNAUTHORIZED_INTERMEDIATE_CA: intermediate certificate in the chain does not have the authority to be a intermediate CA.";
            case CertificateException.UNRECOGNIZED_ISSUER:
                return "\nUNRECOGNIZED_ISSUER: certificate was issued by an unrecognized entity.";
            case CertificateException.UNSUPPORTED_PUBLIC_KEY_TYPE:
                return "\nUNSUPPORTED_PUBLIC_KEY_TYPE: type of the public key in a certificate is not supported by the device.";
            case CertificateException.UNSUPPORTED_SIGALG:
                return "\nUNSUPPORTED_SIGALG: certificate was signed using an unsupported algorithm.";
            case CertificateException.VERIFICATION_FAILED:
                return "\nVERIFICATION_FAILED: certificate failed verification";
        }
        return "unknown code "+code;
    }

    public static String decode(Exception e) {
        StringBuffer desc=new StringBuffer();
        if (e instanceof CertificateException) {
            CertificateException ce=(CertificateException) e;
            desc.append(getErrorName(ce.getReason()));

            Certificate c=ce.getCertificate();
            if (c!=null) {
                desc.append("\ntype=").append(c.getType());
                desc.append("\nver=").append(c.getVersion());
                desc.append("\nissuer=").append(c.getIssuer());
                desc.append("\nsubj=").append(c.getSubject());
                //desc.append("\nvalid from=").append(ui.Time.dayLocalString(c.getNotBefore()));
                //desc.append("\nuntil=").append(ui.Time.dayLocalString(c.getNotAfter()));
                desc.append("\nalg=").append(c.getSigAlgName());
                desc.append("\nSN=").append(c.getSerialNumber());
            }
        }
        return desc.toString();
    }


}
