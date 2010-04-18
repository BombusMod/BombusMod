/*
 * Location.java
 *
 * Created on 19 Апрель 2010 г., 1:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package PEP;
//#ifdef PEP_LOCATION
//# import Client.StaticData;
//# import com.alsutton.jabber.JabberDataBlock;
//# import com.alsutton.jabber.datablocks.Iq;
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import javax.microedition.location.Coordinates;
//# import javax.microedition.location.Location;
//# import javax.microedition.location.LocationListener;
//# import javax.microedition.location.LocationProvider;
//# import locale.SR;
//# import ui.controls.form.DefForm;
//# import ui.controls.form.SimpleString;
//# 
//# /**
//#  *
//#  * @author Vitaly
//#  */
//# public class LocationForm extends DefForm implements LocationListener {
//#ifdef PLUGINS
//#     public static String plugin = "PLUGIN_PEP";
//#endif
//# 
//#     SimpleString loc;
//#     Coordinates coord;
//#     SimpleString wait;
//#     /** Creates a new instance of Location
//#      * @param display
//#      * @param parent
//#      */
//#     public LocationForm(Display display, Displayable parent) {
//#         super(display, parent, "Publish location");
//#         SimpleString location = new SimpleString("Current location", true);
//#         wait = new SimpleString("Please, wait...", false);
//#         wait.selectable = true;
//#         itemsList.addElement(location);
//#         itemsList.addElement(wait);
//#         new GeoRetriever(this).start();
//#         attachDisplay(display);
//#         parentView = parent;
//#     }
//# 
//#     public void locationUpdated(LocationProvider lp, Location lctn) {
//#         Coordinates c;
//#         String value = null;
//#         if (lctn != null && (c = lctn.getQualifiedCoordinates()) != null) {
//#             this.coord = c;
//#             value = c.getLatitude() + ", " + c.getLongitude();
//#         } else {
//#             this.coord = null;
//#             value = SR.MS_ERROR;
//#         }
//#         itemsList.removeElement(wait);
//#         loc = new SimpleString(value, false);
//#         loc.selectable = true;
//#         itemsList.addElement(loc);
//#     }
//# 
//#     public void providerStateChanged(LocationProvider lp, int i) {
//#     }
//# 
//#     public void cmdOk() {
//#         String sid="publish-location";
//#         JabberDataBlock setActivity=new Iq(null, Iq.TYPE_SET, sid);
//#         JabberDataBlock action=setActivity.addChildNs("pubsub", "http://jabber.org/protocol/pubsub") .addChild("publish", null);
//#         action.setAttribute("node", "http://jabber.org/protocol/geoloc");
//#         JabberDataBlock item=action.addChild("item", null);
//#         JabberDataBlock geoloc=item.addChildNs("geoloc", "http://jabber.org/protocol/geoloc");
//#         if (!loc.toString().equals(SR.MS_ERROR)) {
//#         geoloc.addChild("lat", String.valueOf(coord.getLatitude()));
//#         geoloc.addChild("lon", String.valueOf(coord.getLongitude()));
//#         }
//# 
//#         try {
//#             //todo: refactor theStream call; send notification to JabberBlockListener if stream was terminated
//#             StaticData.getInstance().roster.theStream.addBlockListener(new PepPublishResult(display, sid));
//#             StaticData.getInstance().roster.theStream.send(setActivity);
//#         } catch (Exception e) {e.printStackTrace(); }
//# 
//#     }
//#ifdef MENU_LISTENER
//#     public String touchLeftCommand() { return SR.MS_PUBLISH; }
//#endif
//# }
//# 
//# /**
//#  *
//#  * @author ugnich
//#  */
//# class GeoRetriever extends Thread {
//# 
//#     private LocationListener returnto;
//# 
//#     public GeoRetriever(LocationListener returnto) {
//#         this.returnto = returnto;
//#     }
//# 
//#     public void run() {
//#         try {
//#             retrieveLocation();
//#         } catch (Exception ex) {
//#             ex.printStackTrace();
//#             returnto.locationUpdated(null, null);
//#         }
//#     }
//# 
//#     public void retrieveLocation() throws Exception {
//#         LocationProvider lp = LocationProvider.getInstance(null);
//#         Location l = lp.getLocation(60);
//#         returnto.locationUpdated(lp, l);
//#     }
//# }
//#endif
