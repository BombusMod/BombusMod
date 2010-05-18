/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PEP.location;

/**
 *
 * @author Vitaly
 */
public abstract class LocationIO {

    protected final static int NONE = 0;
    protected final static int JSR179 = 1;
    protected final static int CELLID = 2;

    public abstract String getLatitude();
    public abstract String getLongitude();
    static int providerType;

    public static LocationIO getInstance() throws ClassNotFoundException {
        if (providerType == LocationIO.NONE) {
        try {
            // this will throw an exception if JSR-179 is missing
            Class.forName("javax.microedition.location.Location");
            Class c = Class.forName("PEP.location.JSR179Location");
            providerType = LocationIO.JSR179;
//#ifdef DEBUG
//#         System.out.println("JSR-179 presents");
//#endif

        } catch (Exception e) {
            try {
                e.printStackTrace();
                Class c = Class.forName("PEP.location.CellIDLocation");
                providerType = LocationIO.CELLID;
//#ifdef DEBUG
//#         System.out.println("CellID will be used");
//#endif

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        }
        switch (providerType) {
            case LocationIO.JSR179:
                return new JSR179Location();
            default:
                return new CellIDLocation();
        }
    }
    public abstract void getCoordinates();
    public static LocationIO fallback() {
        return new CellIDLocation();
    }
        
}
