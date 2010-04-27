/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PEP.location;

import java.io.InputStream;
import java.io.OutputStream;
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
       // Sony Ericsson
	id = System.getProperty("com.sonyericsson.net.cellid");
	lac = System.getProperty("com.sonyericsson.net.lac");
	mcc = System.getProperty("com.sonyericsson.net.cmcc");
	mnc = System.getProperty("com.sonyericsson.net.cmnc");
        double [] res = GoogleLatLon(Integer.parseInt(mcc), Integer.parseInt(mnc), Integer.parseInt(lac, 16), Integer.parseInt(id));
        lat  = String.valueOf(res[0]);
        lon  = String.valueOf(res[1]);
   } catch(Exception e) {
	e.printStackTrace();
   }
  }


    // Get Lat Lon for GSM Cell from Google DB
   // double[0] - Lat; double[1] - Lon
   public static double[] GoogleLatLon(int MCC, int MNC, int LAC, int CID)
   {
      try
      {
         HttpConnection hc = (HttpConnection)Connector.open("http://www.google.com/glm/mmap");
         hc.setRequestMethod(HttpConnection.POST);

         byte data[] = new byte[55];
         data[0x01] = 0x0E; // fixed
         data[0x10] = 0x1B; // fixed
         for (int i = 0x2F; i <= 0x32; i++) data[i] = (byte)0xFF; // fixed

         if (CID > 65536)
            data[0x1c] = 5; // UTMS - 6 hex digits
         else
            data[0x1c] = 3; // GSM - 4 hex digits

         int stOfs = 0x1f;
         data[stOfs++] = (byte)((CID >> 24) & 0xFF); // 0x1f
         data[stOfs++] = (byte)((CID >> 16) & 0xFF);
         data[stOfs++] = (byte)((CID >> 8) & 0xFF);
         data[stOfs++] = (byte)((CID) & 0xFF);
         data[stOfs++] = (byte)((LAC >> 24) & 0xFF); //0x23
         data[stOfs++] = (byte)((LAC >> 16) & 0xFF);
         data[stOfs++] = (byte)((LAC >> 8) & 0xFF);
         data[stOfs++] = (byte)((LAC) & 0xFF);
         data[stOfs++] = (byte)((MNC >> 24) & 0xFF); // 0x27
         data[stOfs++] = (byte)((MNC >> 16) & 0xFF);
         data[stOfs++] = (byte)((MNC >> 8) & 0xFF);
         data[stOfs++] = (byte)((MNC) & 0xFF);
         data[stOfs++] = (byte)((MCC >> 24) & 0xFF); // 0x2b
         data[stOfs++] = (byte)((MCC >> 16) & 0xFF);
         data[stOfs++] = (byte)((MCC >> 8) & 0xFF);
         data[stOfs++] = (byte)((MCC) & 0xFF);

         hc.setRequestProperty("Content-Type","application/binary" );
         hc.setRequestProperty("Content-Length", Integer.toString(data.length));

         OutputStream os = hc.openOutputStream();
         os.write(data);
         os.close();

         InputStream in = hc.openInputStream();
         byte[] rd = new byte[15];
            int totalBytesRead = 0;
            while (totalBytesRead < rd.length)
                totalBytesRead += in.read(rd, totalBytesRead, rd.length - totalBytesRead);
         in.close();
         hc.close();

         short opcode1 = (short)(rd[0] << 8 | rd[1]);
            byte opcode2 = rd[2];
            int ret_code = ((rd[3] << 24) | (rd[4] << 16) | (rd[5] << 8) | (rd[6]));
            if ((opcode1 == 0x0E) && (opcode2 == 0x1B) && (ret_code == 0))
            {
                double lat = ((double)((rd[7] << 24) | (rd[8] << 16) | (rd[9] << 8) | (rd[10]))) / 1000000;
                double lon = ((double)((rd[11] << 24) | (rd[12] << 16) | (rd[13] << 8) | (rd[14]))) / 1000000;
                return new double[] { lat, lon };
            }
         return new double[] { 0, 0 };
      }
      catch (Exception e) {
          // no data
      }
      
      return new double[] { 0, 0 };

   }
}
