/*
 * LightConfigForm.java
 */ 

package LightControl;

import locale.SR;
import java.util.Vector;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;

public class LightConfigForm
        extends DefForm {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_LIGHT");
//#endif    
    
    private CheckBox config_enabled;

    private NumberInput light_idle;

    private NumberInput light_keypressed;
    private NumberInput light_keypressed_time;

    private NumberInput light_message;
    private NumberInput light_message_time;  

    
    LightConfig light;
    Vector files[];
    Vector fileNames;

    /** Creates a new instance of ConfigForm */
    public LightConfigForm() {
        super(SR.L_CONFIG);
        light=LightConfig.getInstance();

        config_enabled=new CheckBox(SR.L_ENABLED, light.light_control);
        itemsList.addElement(config_enabled);

        light_idle=new NumberInput(sd.canvas, SR.L_IDLE_VALUE, Integer.toString(light.light_idle), 0, 100);
        itemsList.addElement(light_idle);

        light_keypressed=new NumberInput(sd.canvas, SR.L_KEYPRESS_VALUE, Integer.toString(light.light_keypress), 0, 100);
        itemsList.addElement(light_keypressed);
        
        light_keypressed_time=new NumberInput(sd.canvas, SR.L_KEYPRESS_TIMEOUT, Integer.toString(light.light_keypressed_time), 0, 600);
        itemsList.addElement(light_keypressed_time);

        light_message=new NumberInput(sd.canvas, SR.L_MESSAGE_VALUE, Integer.toString(light.light_message), 0, 101);
        itemsList.addElement(light_message);
        
        light_message_time=new NumberInput(sd.canvas, SR.L_MESSAGE_TIMEOUT, Integer.toString(light.light_message_time), 0, 600);
        itemsList.addElement(light_message_time);       
    }
    
    public void cmdOk() {
        light.light_control=config_enabled.getValue();
        light.light_idle=Integer.parseInt(light_idle.getValue());
        light.light_keypress=Integer.parseInt(light_keypressed.getValue());
        light.light_keypressed_time=Integer.parseInt(light_keypressed_time.getValue());
        light.light_message=Integer.parseInt(light_message.getValue());
        light.light_message_time=Integer.parseInt(light_message_time.getValue());
        light.saveToStorage();
        destroyView();
        return;
    }  
}