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
//# import PEP.location.LocationIO;
//# import PEP.location.LocationListener;
//# import com.alsutton.jabber.JabberDataBlock;
//# import com.alsutton.jabber.datablocks.Iq;
//# import javax.microedition.lcdui.TextField;
//# import locale.SR;
//# import ui.VirtualList;
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
//# public class LocationForm extends DefForm implements LocationListener {
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
//#      * @param parent
//#      */
//#     public LocationForm(VirtualList parent) {
//#         super("Publish location");
//#         location = new TextInput(sd.canvas, "Location name", null, null, TextField.ANY);
//#         descr = new TextInput(sd.canvas, "Location description", null, null, TextField.ANY);
//#         lat = new TextInput(sd.canvas, "Latitude", null, null, TextField.DECIMAL);
//#         lon = new TextInput(sd.canvas, "Longitude", null, null, TextField.DECIMAL);
//# 
//#         detect = new LinkString("Retrieve location") {
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
//#     
//#         parentView = parent;
//#     }
//# 
//#     public void detectLocation() {
//#         GeoRetriever geo = new GeoRetriever(this);
//#         new Thread(geo).start();
//#     }
//# 
//#     public void locationUpdated(LocationIO lctn) {
//#         if (lctn != null) {
//#             lat.setValue(lctn.getLatitude());
//#             lon.setValue(lctn.getLongitude());
//#         } else {
//#             new AlertBox(SR.MS_ERROR, "Error retrieving coordinates") {
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
//#             StaticData.getInstance().roster.theStream.addBlockListener(new PepPublishResult( sid));
//#             StaticData.getInstance().roster.theStream.send(setActivity);
//#         } catch (Exception e) { }
//#         destroyView();
//#     }
//#     public String touchLeftCommand() { return SR.MS_PUBLISH; }
//# }
//# 
//# 
//# class GeoRetriever implements Runnable {
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
//#             returnto.locationUpdated(null);
//#         }
//#     }
//# 
//#     public void retrieveLocation() throws Exception {
//#         LocationIO lp = LocationIO.getInstance();
//#         lp.getCoordinates();
//#         returnto.locationUpdated(lp);
//#     }
//# }
//#endif
