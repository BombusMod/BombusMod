/*
 * Item.java
 *
 * Created on 24 январь 2008 г., 20:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IE;

/**
 *
 * @author ad
 */
public class keyValue {
    String value; String key;

    public keyValue(String key, String value) { this.key=key; this.value=value; }
    
    public String getKey() { return key; }
    public String getValue() { return value; }
}
