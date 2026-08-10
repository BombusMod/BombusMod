/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//#ifdef PEP

package PEP;

import Client.Config;
import Client.StaticData;
import locale.SR;
import ui.NativeScreenCommand;
import ui.NativeScreenItem;
import ui.NativeScreenModel;
import ui.VirtualListController;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.LinkString;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import ui.controls.form.TextInput;

/**
 *
 * @author Vitaly
 */
public class PepForm extends DefForm {

//#ifdef PEP
    private CheckBox sndrcvmood;
//#ifdef PEP_TUNE
   private CheckBox rcvtune;
//#if android
   private CheckBox updatetune;
//#endif   
//#endif
//#ifdef PEP_ACTIVITY
   private CheckBox rcvactivity;
//#endif

//#endif
    DropChoiceBox activity;
    TextInput ti;
    LinkString updmood, updact;
    
    public PepForm() {
        super(SR.MS_PEP);
        if (!buildNativeModel()) return;
            itemsList.addElement(new SimpleString("Receive events", true));
            sndrcvmood = new CheckBox(SR.MS_USERMOOD, Config.getInstance().sndrcvmood);
            itemsList.addElement(sndrcvmood);
            updmood = new LinkString(SR.MS_USERMOOD) {
                public void doAction() {
                     Config.getInstance().sndrcvmood = true;
                     new MoodList();
                }
            }; 
            
//#ifdef PEP_TUNE
            rcvtune = new CheckBox(SR.MS_USERTUNE, Config.getInstance().rcvtune); 
            itemsList.addElement(rcvtune);
//#endif
//#ifdef PEP_ACTIVITY
            rcvactivity = new CheckBox(SR.MS_USERACTIVITY, Config.getInstance().rcvactivity);
            itemsList.addElement(rcvactivity);
            updact = new LinkString(SR.MS_USERACTIVITY) {
                public void doAction() {
                     Config.getInstance().rcvactivity = true;
                     new ActivityList(midlet.BombusMod.getInstance().getDisplay());
                }
            };
//#endif

            itemsList.addElement(new SpacerItem(10));
            itemsList.addElement(new SimpleString("Publish events", true));
            itemsList.addElement(updmood);
//#ifdef PEP_ACTIVITY
            itemsList.addElement(updact);
//#endif

//#ifdef PEP_TUNE
//#if android               
            itemsList.addElement(new SpacerItem(10));
            updatetune = new CheckBox("Scrobbled song", Config.getInstance().updatetune);
            itemsList.addElement(updatetune);
//#endif        
//#endif                
    }
    public void cmdOk() {        
        //publish(activity.getSelectedIndex(), ti.getText());
        Config.getInstance().sndrcvmood=sndrcvmood.getValue();
//#ifdef PEP_TUNE
        Config.getInstance().rcvtune=rcvtune.getValue();
//#endif
//#ifdef PEP_ACTIVITY
        Config.getInstance().rcvactivity=rcvactivity.getValue();
//#endif
//#ifdef PEP_TUNE
//#if android
        Config.getInstance().updatetune = updatetune.getValue();
//#endif        
//#endif        
        Config.getInstance().saveToStorage();
        parentView = sd.roster;
        destroyView();
    }

    private boolean buildNativeModel() {
        if (!VirtualListController.getInstance().isActive()) return true;
        NativeScreenModel m = new NativeScreenModel();
        Config cf = Config.getInstance();
        m.addPar(new NativeScreenItem() {{ setAsHeader("Receive events"); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_USERMOOD, cf.sndrcvmood); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_USERTUNE, cf.rcvtune); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_USERACTIVITY, cf.rcvactivity); }});
        m.addPar(new NativeScreenItem() {{ setAsSpacer(10); }});
        m.addPar(new NativeScreenItem() {{ setAsHeader("Publish events"); }});
        m.addPar(new NativeScreenItem() {{ setAsLink(SR.MS_USERMOOD); }});
        m.addPar(new NativeScreenItem() {{ setAsLink(SR.MS_USERACTIVITY); }});
        m.addPar(new NativeScreenItem() {{ setAsSpacer(10); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox("Scrobbled song", cf.updatetune); }});
        m.addCommand("ok", "OK", NativeScreenCommand.OK, -1);
        final PepForm self = this;
        VirtualListController.getInstance().setOnDismiss(new Runnable() { public void run() { self.dismissNative(); } });
        VirtualListController.getInstance().setCaption(SR.MS_PEP);
        VirtualListController.getInstance().setModel(m);
        VirtualListController.getInstance().notifyUpdate();
        return false;
    }
    public void dismissNative() { VirtualListController.getInstance().setModel(null); VirtualListController.getInstance().notifyUpdate(); destroyView(); }
    public void destroyView() { VirtualListController.getInstance().setModel(null); VirtualListController.getInstance().notifyUpdate(); super.destroyView(); }
}

//#endif
