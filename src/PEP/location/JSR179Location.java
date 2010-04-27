/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PEP.location;
import javax.microedition.location.Coordinates;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

/**
 *
 * @author Vitaly
 */
public class JSR179Location extends LocationImpl {

    private Location location;
    private LocationProvider locationProvider;
    private Coordinates coordinates;
    double lat, lon;


    public String getLatitude() {
        return String.valueOf(lat);
    }

    public String getLongitude() {
        return String.valueOf(lon);
    }

    public void getCoordinates() {
        
        try {
            locationProvider = LocationProvider.getInstance(null);
        }
         catch (LocationException e) {
             //TODO: Handle location exception.
             return;
         }
        try {
            location = locationProvider.getLocation(60);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        coordinates = location.getQualifiedCoordinates();
        if (coordinates != null) {
            // Use coordinate information
            lat = coordinates.getLatitude();
            lon = coordinates.getLongitude();
        }
    }

}
