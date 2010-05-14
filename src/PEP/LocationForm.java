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
//# import PEP.location.LocationImpl;
//# import PEP.location.LocationListenerImpl;
//# import com.alsutton.jabber.JabberDataBlock;
//# import com.alsutton.jabber.datablocks.Iq;
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import javax.microedition.lcdui.TextField;
//# import locale.SR;
//# import ui.controls.AlertBox;
//# import ui.controls.form.DefForm;
//# import ui.controls.form.LinkString;
//# import ui.controls.form.SimpleString;
//# import ui.controls.form.TextInput;
//# 
//# /**
//#  *
//#  * @author Vitaly
//#  */
//# public class LocationForm extends DefForm implements LocationListenerImpl {
//#ifdef PLUGINS
//#     public static String plugin = "PLUGIN_PEP";
//#endif
//# 
//#     SimpleString loc;
//#     TextInput location, descr;
//#     TextInput lat, lon;
//#     LinkString detect;
//# 
//#     /** Creates a new instance of Location
//#      * @param display
//#      * @param parent
//#      */
//#     public LocationForm(Display display, Displayable parent) {
//#         super(display, parent, SR.MS_USERLOCATION);
//#         location = new TextInput(display, SR.MS_LOCATION_NAME, null, null, TextField.ANY);
//#         descr = new TextInput(display, SR.MS_LOCATION_DESCRIPTION, null, null, TextField.ANY);
//#         lat = new TextInput(display, SR.MS_LATITUDE, null, null, TextField.DECIMAL);
//#         lon = new TextInput(display, SR.MS_LONGITUDE, null, null, TextField.DECIMAL);
//# 
//#         detect = new LinkString(SR.MS_RETRIEVE_LOCATION) {
//#             public void doAction() {
//#                 detectLocation();
//#             }
//#         };
//#         itemsList.addElement(lat);
//#         itemsList.addElement(lon);
//#         itemsList.addElement(location);
//#         itemsList.addElement(descr);
//#         itemsList.addElement(detect);
//#         
//#         attachDisplay(display);
//#         parentView = parent;
//#     }
//# 
//#     public void detectLocation() {
//#         new GeoRetriever(this).start();
//#     }
//# 
//#     public void locationUpdated(LocationImpl lctn) {
//#         if (lctn != null) {
//#             lat.setValue(lctn.getLatitude());
//#             lon.setValue(lctn.getLongitude());
//#         } else {
//#             new AlertBox(SR.MS_ERROR, "Error retrieving coordinates", display, this) {
//#                 public void yes() {}
//#                 public void no() {}
//#             };
//#         }
//#         redraw();
//#     }
//#     
//#     public void cmdOk() {
//#         String sid="publish-location";
//#         JabberDataBlock setActivity=new Iq(null, Iq.TYPE_SET, sid);
//#         JabberDataBlock action=setActivity.addChildNs("pubsub", "http://jabber.org/protocol/pubsub") .addChild("publish", null);
//#         action.setAttribute("node", "http://jabber.org/protocol/geoloc");
//#         JabberDataBlock item=action.addChild("item", null);
//#         JabberDataBlock geoloc=item.addChildNs("geoloc", "http://jabber.org/protocol/geoloc");
//#         try {
//#             if (!lat.getValue().equals("")) {
//#             geoloc.addChild("lat", lat.getValue());
//#             geoloc.addChild("lon", lon.getValue());
//#             geoloc.addChild("description", location.getValue());
//#             geoloc.addChild("text", descr.getValue());
//#             }
//#             //todo: refactor theStream call; send notification to JabberBlockListener if stream was terminated
//#             StaticData.getInstance().roster.theStream.addBlockListener(new PepPublishResult(display, sid));
//#             StaticData.getInstance().roster.theStream.send(setActivity);
//#         } catch (Exception e) {e.printStackTrace(); }
//#         destroyView();
//#     }
//#ifdef MENU_LISTENER
//#     public String touchLeftCommand() { return SR.MS_PUBLISH; }
//#endif
//# }
//# 
//# 
//# class GeoRetriever extends Thread {
//# 
//#     private LocationListenerImpl returnto;
//# 
//#     public GeoRetriever(LocationListenerImpl returnto) {
//#         this.returnto = returnto;
//#     }
//# 
//#     public void run() {
//#         try {
//#             retrieveLocation();
//#         } catch (Exception ex) {
//#             ex.printStackTrace();
//#             returnto.locationUpdated(null);
//#         }
//#     }
//# 
//#     public void retrieveLocation() throws Exception {
//#         LocationImpl lp = LocationImpl.getInstance();
//#         lp.getCoordinates();
//#         returnto.locationUpdated(lp);
//#     }
//# }
//#endif
