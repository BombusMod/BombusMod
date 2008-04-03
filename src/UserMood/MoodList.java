/*
 * MoodList.java
 *
 * Created on 5 ���� 2008 �., 22:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package UserMood;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import util.StringLoader;

/**
 *
 * @author ad
 */
public class MoodList {
    
    // Singleton
    private static MoodList instance;
    
    public static MoodList getInstance(){
        if (instance==null) {
            instance=new MoodList();
            init();
        }
        return instance;
    }

    public static Vector moodList=new Vector();
    public static Vector moodNames=new Vector();
    
    public static final String initMoods = "afraid.amazed.angry.annoyed.anxious.aroused.ashamed.bored.brave.calm.cold.confused.contented.cranky.curious.depressed.disappointed.disgusted.distracted.embarrassed.excited.flirtatious.frustrated.grumpy.guilty.happy.hot.humbled.humiliated.hungry.hurt.impressed.in_awe.in_love.indignant.interested.intoxicated.invincible.jealous.lonely.mean.moody.nervous.neutral.offended.playful.proud.relieved.remorseful.restless.sad.sarcastic.serious.shocked.shy.sick.sleepy.stressed.surprised.thirsty.worried.";
   
    private static void init() {
        if (moodNames.size()<1)
            fillMoods();
        
        try {
            int id=0;
            for (Enumeration moods=moodNames.elements(); moods.hasMoreElements(); )  {
                String mood=(String) moods.nextElement();
                moodList.addElement(new Mood(id, mood, loadString(mood), null));
                id++;                
            }
        } catch (Exception ex) { }
        localeMood=null;
    }
    
    private static void fillMoods() {
        try {
            int p=0; int pos=0;
            while (pos<initMoods.length()) {
               p=initMoods.indexOf('.', pos);
               String mood=initMoods.substring(pos, p);
               moodNames.addElement((String)mood);
               pos=p+1;
            }
        } catch (Exception ex) { }
    }

    private static Hashtable localeMood;
    
    public static String loadString(String key) {
        if (localeMood==null) {
            localeMood=new StringLoader().hashtableLoader("/images/moods.txt");
        }
        String value=(String)localeMood.get(key);
        return (value==null)?key:value;
    }
    
    public static int getId(String mood) {
        return moodNames.indexOf(mood);
    }
    
    public static Mood getMood(String mood, String text) {
        if (moodNames.size()<1)
            fillMoods();
        
        return new Mood(getId(mood), mood, loadString(mood), text);
        
    }
}
