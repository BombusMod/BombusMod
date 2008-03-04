/*
 * RotateImage.java
 *
 * Created on 12 Январь 2008 г., 1:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author ad
 */

public class RotateImage {
    // конструктор
   private static int ImW, ImH,xc,yc;
   private static int phi=3;
   private static int sin_t[]={0,174,342,500,643,766,866,940,985,1000};
   
    public static int sinus(int t) {
        int k;
        k=(int) (t/10);	
        if (t%10==0) {
            return sin_t[k];
        }
        
        return (int) ((sin_t[k+1]-sin_t[k])*(t%10)/10+sin_t[k]);
    }

    public static int sin(int t) {
	int sign=1;
	t=t%360;	//Учтем период синуса
	if (t<0) {	//Учтем нечетность синуса
            t=-t;
            sign=-1;
	}
	if (t<=90){ return sign*sinus(t);}
	else if (t<=180){ return sign*sinus(180-t);}
	else if (t<=270){ return -sign*sinus(t-180);}
	else {return -sign*sinus(360-t);}
    }
	
    public static int cos(int t) {
	t=t%360;	//Учтем период синуса
	if (t<0) {t=-t;} //Учтем четность косинуса		
	if (t<=90){ return sinus(90-t);}
	else if (t<=180){ return -sinus(t-90);}
	else if (t<=270){ return -sinus(270-t);}
	else {return sinus(t-270);}
    }
    
    public static int[] ARGB_Img_rot(int phi, int[] img) {
	int x0,y0,x1,y1;
	int sn=sin(phi);
	int cs=cos(phi);
	for (y1=0;y1<ImH;y1++){
            for (x1=0;x1<ImW;x1++) {
		img[y1*ImW+x1]=0x00FF0000; //На всякий случай заполняем картинку цветом фона
		x0=(int) ((cs*(x1-xc)+sn*(y1-yc))/1000+xc);
		y0=(int) (-(sn*(x1-xc)-cs*(y1-yc))/1000+yc);
		if (x0>-1) //Проверяем, не выходит ли точка за пределы области
                    if (x0<ImW) 
			if (y0>-1)
                            if(y0<ImH) //Получаем точку
				img[y1*ImW+x1]=img[y0*ImW+x0];
            }
        }
        return img;
    }
	

    public static int[] img(Image img) {
        Image image1 = img;
        
        //создаем массив
        ImW=image1.getWidth();
        ImH=image1.getHeight();
        xc=(int) (ImW/2)+(ImW)%2;
        yc=(int) (ImH/2)+(ImH)%2;
        int [] newImg = new int[ImW*ImH];
        //Загружаем в него картинку
        image1.getRGB(newImg,0,ImW,0,0,ImW,ImH);
        
        phi=phi+10;
        if (phi==360){phi=0;}
        newImg = ARGB_Img_rot(phi, newImg); 	//Поворачиваем картинку на заданный угол
        
        //g.drawRGB(newImg,0,ImW,20+ImW,10,ImW,ImH,true);
        
        return newImg;
    }
}

/*
     // конструктор
   private Image image1;
   private int GrH, GrW, ImW, ImH,xc,yc;
   private int phi=3;
   private int ARGB_Img0[],ARGB_Img1[];
   private int bgcol=0x00FF0000;
   private int alpha[]={0,10,20,30,40,50,60,70,80,90};
   private int sin_t[]={0,174,342,500,643,766,866,940,985,1000};
   
   
    public int sinus(int t) {
        int k;
        k=(int) (t/10);	
        if (t%10==0) {
            return sin_t[k];
        }
        
        return (int) ((sin_t[k+1]-sin_t[k])*(t%10)/10+sin_t[k]);
    }

    public int sin(int t) {
	int sign=1;
	t=t%360;	//Учтем период синуса
	if (t<0) {	//Учтем нечетность синуса
            t=-t;
            sign=-1;
	}
	if (t<=90){ return sign*sinus(t);}
	else if (t<=180){ return sign*sinus(180-t);}
	else if (t<=270){ return -sign*sinus(t-180);}
	else {return -sign*sinus(360-t);}
    }
	
    public int cos(int t) {
	t=t%360;	//Учтем период синуса
	if (t<0) {t=-t;} //Учтем четность косинуса		
	if (t<=90){ return sinus(90-t);}
	else if (t<=180){ return -sinus(t-90);}
	else if (t<=270){ return -sinus(270-t);}
	else {return sinus(t-270);}
    }

    public void ARGB_Img_rot(int phi) {
	int x0,y0,x1,y1;
	int sn=sin(phi);
	int cs=cos(phi);
	for (y1=0;y1<ImH;y1++){
            for (x1=0;x1<ImW;x1++) {
		ARGB_Img1[y1*ImW+x1]=bgcol; //На всякий случай заполняем картинку цветом фона
		x0=(int) ((cs*(x1-xc)+sn*(y1-yc))/1000+xc);
		y0=(int) (-(sn*(x1-xc)-cs*(y1-yc))/1000+yc);
		if (x0>-1) //Проверяем, не выходит ли точка за пределы области
                    if (x0<ImW) 
			if (y0>-1)
                            if(y0<ImH) //Получаем точку
				ARGB_Img1[y1*ImW+x1]=ARGB_Img0[y0*ImW+x0];
            }
        }
    }
	

    public Image img() {
        
        return null;
    }
    
    public void start() {
        //Получаем размер высоту и ширину экрана
        GrH=getHeight();
        GrW=getWidth();
        try{
            image1 = Image.createImage("/1.png");
        }catch(java.io.IOException io){}

        //Загружаем в массив картинку

        //создаем массив
        ImW=image1.getWidth();
        ImH=image1.getHeight();
        xc=(int) (ImW/2)+(ImW)%2;
        yc=(int) (ImH/2)+(ImH)%2;
        ARGB_Img0 = new int[ImW*ImH];
        ARGB_Img1= new int[ImW*ImH];
        //Загружаем в него картинку
        image1.getRGB(ARGB_Img0,0,ImW,0,0,ImW,ImH);

        Thread t=new Thread(this);
        t.start();
    }

    public void run() {
        while(true) {
            repaint();
            phi=phi+10;
            if (phi==360){phi=0;}
            ARGB_Img_rot(phi); 	//Поворачиваем картинку на заданный угол
            try{
                Thread.sleep(100);
            }catch(java.lang.InterruptedException io){}
        }
    }
	
    public void paint(Graphics g) {
        g.setColor(150,150,150);
        //Очищаем экран
        g.fillRect(0,0,GrW,GrH);

        g.drawImage(image1,10,10,Graphics.TOP|Graphics.LEFT);
        g.drawRGB(ARGB_Img1,0,ImW,10,20+ImH,ImW,ImH,false);
        g.drawRGB(ARGB_Img1,0,ImW,20+ImW,10,ImW,ImH,true);
    }
 */
