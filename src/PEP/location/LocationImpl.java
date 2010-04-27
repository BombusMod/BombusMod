/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PEP.location;

/**
 *
 * @author Vitaly
 */
public abstract class LocationImpl {

    public abstract String getLatitude();
    public abstract String getLongitude();

    public static LocationImpl getInstance() throws ClassNotFoundException {
        LocationImpl provider = null;
        try {
            // this will throw an exception if JSR-179 is missing
            Class.forName("javax.microedition.location.Location");
            Class c = Class.forName("PEP.location.JSR179Location");
            provider = (LocationImpl)(c.newInstance());
//#ifdef DEBUG
//#         System.out.println("JSR-179 presents");
//#endif

        } catch (Exception e) {
            try {
                e.printStackTrace();
                Class c = Class.forName("PEP.location.CellIDLocation");
                provider = (LocationImpl) (c.newInstance());
//#ifdef DEBUG
//#         System.out.println("CellID will be used");
//#endif

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return provider;
    }
    public abstract void getCoordinates();
        
}
