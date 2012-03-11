/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//#ifdef PEP_LOCATION
//#if !(android)
//# package PEP.location;
//# 
//# import javax.microedition.location.Coordinates;
//# import javax.microedition.location.Location;
//# import javax.microedition.location.LocationProvider;
//# 
//# /**
//#  *
//#  * @author Vitaly
//#  */
//# public class JSR179Location extends LocationIO {
//# 
//#     private Location location;
//#     private LocationProvider locationProvider;
//#     private Coordinates coordinates;
//#     double lat, lon;
//# 
//# 
//#     public String getLatitude() {
//#         return String.valueOf(lat);
//#     }
//# 
//#     public String getLongitude() {
//#         return String.valueOf(lon);
//#     }
//# 
//#     public void getCoordinates() throws Exception {
//#         
//#         locationProvider = LocationProvider.getInstance(null);
//#         location = locationProvider.getLocation(60);
//#         coordinates = location.getQualifiedCoordinates();
//#         if (coordinates != null) {
//#             // Use coordinate information
//#             lat = coordinates.getLatitude();
//#             lon = coordinates.getLongitude();
//#         }
//#     }
//# 
//# }
//# 
//#endif
//#endif