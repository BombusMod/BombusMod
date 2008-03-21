/*
 * Time.java
 *
 * Created on 20.02.2005, 13:03
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ui;
import Client.Config;
import java.util.*;
import locale.SR;

/**
 *
 * @author Eugene Stahov
 */
public class Time {
    
    private static Calendar c=Calendar.getInstance( TimeZone.getTimeZone("GMT") );
    private static long utcToLocalOffsetMillis=0; 
    private static long fixupLocalOffsetMills=0;
    private static int tzo=0;
    
    public static int GMTOffset=0;
 
    /** Creates a new instance of Time */
    private Time() { }
    
    public static void setOffset(int tzOffset, int locOffset){
        utcToLocalOffsetMillis=((long)tzOffset)*60*60*1000;
        fixupLocalOffsetMills=((long)locOffset)*60*60*1000;
        tzo=tzOffset;
     }

    public static String lz2(int i){
        if (i<10) return "0"+i; else return String.valueOf(i);
    }
    
    public static String timeLocalString(long date){
        Calendar c=calDate(date);
        return lz2(c.get(Calendar.HOUR_OF_DAY))+':'+lz2(c.get(Calendar.MINUTE));
     }
    
    private static Calendar calDate(long date){
        c.setTime(new Date(date+utcToLocalOffsetMillis));
        return c;
    }
    
    public static String weekDayLocalString(long date){
        int weekDay=calDate(date).get(Calendar.DAY_OF_WEEK);
        String locWeekDay="";
        switch (weekDay) {
            case 1: locWeekDay=SR.MS_SUN; break;
            case 2: locWeekDay=SR.MS_MON; break;
            case 3: locWeekDay=SR.MS_TUE; break;
            case 4: locWeekDay=SR.MS_WED; break;
            case 5: locWeekDay=SR.MS_THU; break;
            case 6: locWeekDay=SR.MS_FRI; break;
            case 7: locWeekDay=SR.MS_SAT; break;            
        }
        return locWeekDay;
     }
    
    public static String dayLocalString(long date){
        Calendar c=calDate(date);
        return lz2(c.get(Calendar.DAY_OF_MONTH))+'.'+
               lz2(c.get(Calendar.MONTH)+1)+'.'+
               lz2(c.get(Calendar.YEAR) % 100)+" ";
     }

    public static long utcTimeMillis(){
        return System.currentTimeMillis()+fixupLocalOffsetMills;
    }
    
    public static String Xep0082UtcTime(){
        long date=utcTimeMillis();
         c.setTime(new Date(date));
         return String.valueOf(c.get(Calendar.YEAR))+
                 lz2(c.get(Calendar.MONTH)+1)+
                 lz2(c.get(Calendar.DAY_OF_MONTH))+
                'T' + lz2(c.get(Calendar.HOUR_OF_DAY))+':'+lz2(c.get(Calendar.MINUTE))+':'+lz2(c.get(Calendar.SECOND));
     }

    public static String utcTime() {
        long date=utcTimeMillis();
        c.setTime(new Date(date));
        return String.valueOf(c.get(Calendar.YEAR)) +
                '-' + lz2(c.get(Calendar.MONTH)+1) + 
                '-' + lz2(c.get(Calendar.DAY_OF_MONTH)) +
                'T' + lz2(c.get(Calendar.HOUR_OF_DAY))+':'+lz2(c.get(Calendar.MINUTE))+':'+lz2(c.get(Calendar.SECOND)) +
                'Z';
    }
     
    public static String tzOffset(){
        StringBuffer tz=new StringBuffer();
        int tzi=tzo;
        char sign='+';
        if (tzo<0) { sign='-'; tzi=-tzo; }
        tz.append(sign);
        tz.append(lz2(tzi));
        tz.append(":00");
        return tz.toString();
    }
    
     public static String dispLocalTime(){
        long utcDate=utcTimeMillis();
         //Calendar c=calDate(date);
        return dayLocalString(utcDate)+timeLocalString(utcDate);
     }
     
     public static String localWeekDay(){
        long utcDate=utcTimeMillis();
        return weekDayLocalString(utcDate);
     }
     
     public static String localTime(){
        long utcDate=utcTimeMillis();
        return timeLocalString(utcDate);
     }
     
     public static int getHour(){
        Calendar c=calDate(utcTimeMillis());
        return c.get(Calendar.HOUR_OF_DAY);
     }
     
     public static int getMin(){
        Calendar c=calDate(utcTimeMillis());
        return c.get(Calendar.MINUTE);
     }
     
    
    private final static int[] calFields=
    {Calendar.YEAR,         Calendar.MONTH,     Calendar.DATE, 
     Calendar.HOUR_OF_DAY,  Calendar.MINUTE,    Calendar.SECOND};
     
    private final static int[] ofsFieldsA=
    { 0, 4, 6, 9, 12, 15 } ; //XEP-0091 - DEPRECATED
    
    private final static int[] ofsFieldsB=
    { 0, 5, 8, 11, 14, 17 } ;//XEP-0203
    
    public static long dateIso8601(String sdate){
        int[] ofs=ofsFieldsA;
        if (sdate.endsWith("Z")) ofs=ofsFieldsB;
        try {
            int l=4;    // yearlen
            for (int i=0; i<calFields.length; i++){
                int begIndex=ofs[i];
                int field=Integer.parseInt(sdate.substring(begIndex, begIndex+l));
                if (i==1) field+=Calendar.JANUARY-1;
                l=2;
                c.set(calFields[i], field);
            }
        } catch (Exception e) {    }
        return c.getTime().getTime(); 
    }
    
    public static long dateStringToLong(String sdate){
        int field=0;
        try {
            field=Integer.parseInt(sdate.substring(0, 2)); c.set(calFields[2], field); //date
            field=Integer.parseInt(sdate.substring(3, 5)); c.set(calFields[1], field-1); //month
            field=Integer.parseInt(sdate.substring(6, 8)); c.set(calFields[0], field+2000); //year
            field=Integer.parseInt(sdate.substring(9, 11))+(Config.getInstance().gmtOffset)+(Config.getInstance().locOffset); c.set(calFields[3], field); //hour
            field=Integer.parseInt(sdate.substring(12, 14)); c.set(calFields[4], field); //min
            c.set(calFields[5], 0); //sec
        } catch (Exception e) {}
        return c.getTime().getTime(); 
    }
    
    public static String secDiffToDate(int seconds){
        String result ="";
        int d = 0,h = 0,m = 0,s = 0;
        if (seconds>86400){
            d=(seconds/86400);
            seconds=seconds-(d*86400);
        }
        if (seconds>3600){
            h=(seconds/3600);
            seconds=seconds-(h*3600);
        }
        if (seconds>60){
            m=(seconds/60);
            seconds=seconds-(m*60);
        }
        s=seconds;
        
        if (d>0) {
            result+= d + " " + goodWordForm (d,3);
        }
        if (h>0) {
            if (d>0) result+=", ";
            result+= h + " " + goodWordForm (h, 2);
        }
        if (m>0) {
            if ((d>0) || (h>0)) result+=", ";
            result+= m + " " + goodWordForm (m, 1);
        }
        if (s>0) {
            if ((d>0) || (h>0) || (m>0))  result+=", ";
            result+= s + " " + goodWordForm (s, 0);
        }
        if (result=="" && s==0)
            result=s + " " + goodWordForm (s, 0);
        return result;
    }
    
    public static String goodWordForm (int d, int field) {
        String [][] suf =  {
            {SR.MS_SEC1, SR.MS_SEC2, SR.MS_SEC3},
            {SR.MS_MIN1, SR.MS_MIN2, SR.MS_MIN3},
            {SR.MS_HOUR1, SR.MS_HOUR2, SR.MS_HOUR3},
            {SR.MS_DAY1, SR.MS_DAY2, SR.MS_DAY3},
        };
        int index;
        if ((d%100>10) && (d%100<20) || (d%10==0) || (d%10>4))
            index=2;
        else if ((d%10>1) && (d%10<5)) 
            index=1;
        else 
            index=0;
        return suf[field][index];
    }
}

