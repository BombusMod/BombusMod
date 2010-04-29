/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PEP.location;

import java.io.InputStream;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author Vitaly
 */
public class CellIDLocation extends LocationImpl {

    private String lat, lon;

    public String getLatitude() {
        return lat;
    }

    public String getLongitude() {
        return lon;
    }

    public void getCoordinates() {
        update();
    }

    private String id, lac, mnc, mcc;

   public void update() {
   try {             
	GoogleLatLon(getLAC(), getCellId());
   } catch(Exception e) {
	e.printStackTrace();
   }
  }


   // Get Lat Lon for GSM Cell from Google DB
    public void GoogleLatLon(String LAC, String CID) {
        InputStream is = null;
        StringBuffer url = null;
        HttpConnection hc = null;
        Hashtable ht = null;
        try {
            url = new StringBuffer("http://bm-bot.appspot.com/loc/").append(CID).append("/").append(LAC);
            hc = (HttpConnection) Connector.open(url.toString());

            if (hc.getResponseCode() == HttpConnection.HTTP_OK) {

                ht = new util.StringLoader().hashtableLoader(hc.openInputStream());
                lat = (String) ht.get("lat");
                lon = (String) ht.get("lon");
            }

        } catch (Exception e) {
            // no data
            lat = "-1"; lon = "-1";
                e.printStackTrace();
        }

    }

    public static String getCellId() {
        String out = "";
        try {
            out = System.getProperty("Cell-ID");
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("CellID");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                System.getProperty("phone.cid");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("com.nokia.mid.cellid");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("com.sonyericsson.net.cellid");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("com.samsung.cellid");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("com.siemens.cellid");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("cid");
            }
        } catch (Exception e) {
            return out == null ? "0" : out;
        }

        return out == null ? "0" : out;
    }

   	/**
     * get the lac string from phone
     *
     * @return lac
     */
    public static String getLAC() {
        String out = "";
        try {

            out = System.getProperty("phone.lac");

            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("com.nokia.mid.lac");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("com.sonyericsson.net.lac");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("LocAreaCode");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("com.samsung.cellid");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("com.siemens.cellid");
            }
            if (out == null || out.equals("null") || out.equals("")) {
                out = System.getProperty("cid");
            }

        } catch (Exception e) {
            return out == null ? "0" : out;
        }

        return out == null ? "0" : out;
    }

}
