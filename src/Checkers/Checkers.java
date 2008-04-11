/*
 * Checkers.java
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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
 *
 */

package Checkers;
import Client.Contact;
import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;

public class Checkers extends Canvas implements CommandListener, JabberBlockListener {
    public static final int BLACK = 0;
    public static final int WHITE = 0xffffff;
    public static final int RED = 0xf96868;
    public static final int GREY = 0x969696;
    
    public static final int GREEN = 0x00ff00;
    public static final int YELLOW = 0xffFF00;
    public static final int BLUE = 0x0000ff;
    
    public static final int LT_GREY = 0xBEFF00;
    public static final int GRID_WIDTH = 8;
    
    public static final byte START_GAME_REQUEST_FLAG = -6;
    public static final byte START_GAME_FLAG = -5;
    public static final byte END_GAME_FLAG = -4;
    public static final byte END_TURN_FLAG = -3;
    public static final byte OPPONENT_MOVE = -2;
    public static final byte WAIT_FLAG = -1;    

    private Display display;
    private Displayable parentView;

    private int mySquareSize;

    private int myMinSquareSize = 15;

    private boolean myIsWaiting; // = true;

    
    public boolean isEndGame = false;
    private boolean myTurnIsDone = true;
            
    StaticData sd=StaticData.getInstance();
    
    private Command myEndCommand = new Command("End", Command.SCREEN, 97);
    private Command myExitCommand = new Command("Exit", Command.EXIT, 99);

    private Contact contact;

    public Checkers(Display display, Contact contact) {
//#ifdef DEBUG
//#         System.out.println("checkers started");
//#endif
        this.display = display;
        this.contact = contact;
        parentView=display.getCurrent();
        
        sd.roster.theStream.addBlockListener(this);
        
        //create the canvas and set up the commands:
        addCommand(myEndCommand);
        addCommand(myExitCommand);
        setCommandListener(this);
        display.setCurrent(this);
                
        int width = getWidth();
        int height = getHeight();

        int screenSquareWidth = height;
        if(width < height) {
          screenSquareWidth = width;
        }
        mySquareSize = screenSquareWidth / GRID_WIDTH;
        
        //if(mySquareSize < myMinSquareSize) System.out.println("Display too small");
        
        start();
    }

    void setWaitScreen(boolean wait) { myIsWaiting = wait; }

    protected void paint(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        g.setColor(WHITE);
        g.fillRect(0, 0, width, height);

        if(myIsWaiting) {
            // perform some calculations to place the text correctly:
            Font font = g.getFont();
            int fontHeight = font.getHeight();
            int fontWidth = font.stringWidth("waiting for another player");
            g.setColor(WHITE);
            g.fillRect((width - fontWidth)/2, (height - fontHeight)/2, fontWidth + 2, fontHeight);
            // write in black
            g.setColor(BLACK);
            g.setFont(font);
            g.drawString("waiting for another player", (width - fontWidth)/2, (height - fontHeight)/2, g.TOP|g.LEFT);
            return;
        }
        // draw the checkerboard:
        // dark squares:
        byte offset = 0;
        for(byte i = 0; i < 4; i++) {
            for(byte j = 0; j < 8; j++) {
                if(j % 2 != 0) {
                    offset = 1;
                } else {
                    offset = 0;
                }
                // selected square
                if(isSelected(i, j)) {
                    g.setColor(LT_GREY);
                    g.fillRect((2*i + offset)*mySquareSize, j*mySquareSize,  mySquareSize, mySquareSize);
                } else {
                    // not selected
                    g.setColor(GREY);
                    g.fillRect((2*i + offset)*mySquareSize, j*mySquareSize, mySquareSize, mySquareSize);
                }
                // put the pieces
                g.setColor(RED);
                int piece = getPiece(i, j);
                int circleOffset = 2;
                int circleSize = mySquareSize - 2*circleOffset;
                if(piece < 0) {
                    // black
                    g.setColor(BLACK);
                    g.fillRoundRect((2*i + offset)*mySquareSize + circleOffset, j*mySquareSize + circleOffset, circleSize, circleSize, circleSize, circleSize);
                    // king
                    if(piece < -1) {
                        g.setColor(YELLOW);
                        g.fillRoundRect((2*i + offset)*mySquareSize + circleOffset, j*mySquareSize + circleOffset, circleSize, circleSize, circleSize, circleSize);
                    }
                } else if(piece > 0) {
                    // red
                    g.fillRoundRect((2*i + offset)*mySquareSize + circleOffset, j*mySquareSize + circleOffset, circleSize, circleSize, circleSize, circleSize);
                    // player king
                    if(piece > 1) {
                        g.setColor(BLUE);
                        g.fillRoundRect((2*i + offset)*mySquareSize + circleOffset, j*mySquareSize + circleOffset, circleSize, circleSize, circleSize, circleSize);
                    }
                }
            }
        }
    // blank squares
    g.setColor(WHITE);
    for(int i = 0; i < 4; i++) {
      for(int j = 0; j < 8; j++) {
          if(j % 2 == 0) {
            offset = 1;
          } else {
            offset = 0;
          }
          g.fillRect((2*i + offset)*mySquareSize, j*mySquareSize,  mySquareSize, mySquareSize);
      }
    }
        if(getGameOver()) {
            // perform some calculations to place the text correctly:
            Font font = g.getFont();
            int fontHeight = font.getHeight();
            int fontWidth = font.stringWidth("Game Over");
            g.setColor(WHITE);
            g.fillRect((width - fontWidth)/2, (height - fontHeight)/2,
            fontWidth + 2, fontHeight);
            // write in black
            g.setColor(BLACK);
            g.setFont(font);
            g.drawString("Game Over", (width - fontWidth)/2, (height - fontHeight)/2, g.TOP|g.LEFT);
        }
    }

    public void keyPressed(int keyCode) {  
        if(isMyTurn()) {
          int action = getGameAction(keyCode);   
          switch (action) {
              case LEFT:
                  leftPressed();
                  break;
              case RIGHT:
                  rightPressed();
                  break;
              case UP:
                  upPressed();
                  break;
              case DOWN:
                  deselect();
                  break;
          }
          repaint();
          //serviceRepaints();
        }
    }

    public void commandAction(Command c, Displayable displayable) {
        if(c == myExitCommand) {
            destroy();
        }
        if(c == myEndCommand) {
            destroyView();
        }
    }
    
    public void destroy() {
        endGame();
        System.gc();
        destroyView();
    }

    void errorMsg(Exception e) {
        e.printStackTrace();
        if(e.getMessage() == null) {
            errorMsg(e.getClass().getName());
        } else {
            errorMsg(e.getMessage());
        }
    }

    void errorMsg(String msg) {
//#ifdef DEBUG
//#         System.out.println("!!!Error: "+msg);
//#endif
        //Alert errorAlert = new Alert("error",  msg, null, AlertType.ERROR);
        //errorAlert.setCommandListener(this);
        //errorAlert.setTimeout(Alert.FOREVER);
        //display.setCurrent(errorAlert);
    }
    
    public void destroyView(){
        if (display!=null)
            display.setCurrent(parentView);
    }
    
    void endGame() {
//#ifdef DEBUG
//#         System.out.println("end game");
//#endif
        sendCommand(END_GAME_FLAG, 0, 0, 0, 0);
        isEndGame = true;
        
        setGameOver();
    }
    
    void move(byte sourceX, byte sourceY, byte destinationX, byte destinationY) {
//#ifdef DEBUG
//#         System.out.println("move x("+sourceX+"->"+destinationX+") y("+sourceY+"->"+destinationY+")");
//#endif       
        sendCommand(OPPONENT_MOVE, sourceX, sourceY, destinationX, destinationY);
        
        myTurnIsDone = true; //false
    }
    
    void endTurn() {
//#ifdef DEBUG
//#         System.out.println("end turn");
//#endif
        myTurnIsDone = true;
    }

    void sendCommand(int state, int sourceX, int sourceY, int destX, int destY) {
        JabberDataBlock iq=new Iq(contact.getJid(), Iq.TYPE_RESULT, "setCheckers");
        iq.setAttribute("i0", Integer.toString(state)); //state;
        iq.setAttribute("i1", Integer.toString(sourceX)); //sourceX;
        iq.setAttribute("i2", Integer.toString(sourceY)); //sourceY;
        iq.setAttribute("i3", Integer.toString(destX)); //destinationX;
        iq.setAttribute("i4", Integer.toString(destY)); //destinationY;

        //roster.theStream.addBlockListener(this);
        
        //System.out.println(iq.toString());

        if (!sd.roster.isLoggedIn()) 
            return;
        sd.roster.theStream.send(iq);
    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Iq) {
            if (data.getTypeAttribute().equals("result") && data.getAttribute("from").equals(contact.getJid())) {
                
                if (!data.getAttribute("id").equals("setCheckers")) 
                    return BLOCK_REJECTED;
                
                int i0 = Integer.parseInt(data.getAttribute("i0"));
                int i1 = Integer.parseInt(data.getAttribute("i1"));
                int i2 = Integer.parseInt(data.getAttribute("i2"));
                int i3 = Integer.parseInt(data.getAttribute("i3"));
                int i4 = Integer.parseInt(data.getAttribute("i4"));

                switch (i0) {
                        case START_GAME_FLAG:
//#ifdef DEBUG
//#                             System.out.println("game started");
//#endif
                            //myCanvas.setWaitScreen(false);
                            //myCanvas.repaint();
                            break;
                        case END_GAME_FLAG:
//#ifdef DEBUG
//#                             System.out.println("end game flag");
//#endif
                            //myCanvas.setWaitScreen(true);
                            endGame();
                            break;
                        case END_TURN_FLAG:
//#ifdef DEBUG
//#                             System.out.println("end turn flag");
//#endif
                            //myGame.endOpponentTurn();
                            break;
                        case WAIT_FLAG:
                            break;
                        case OPPONENT_MOVE:
                            if (myTurnIsDone) {
//#ifdef DEBUG
//#                                 System.out.println("opponent move");
//#endif
                                moveOpponent(i1, i2, i3, i4);
                                endOpponentTurn();
                                //myCanvas.repaint();
                            } else {
//#ifdef DEBUG
//#                                 System.out.println("don`t touch! it`s my move!");
//#endif
                            }
                    }
                    repaint();

                    return BLOCK_PROCESSED;
            }
        }
        return BLOCK_REJECTED;
    }

  /**
   * 0 = empty
   * 1 = local player's piece
   * 2 = local player's king
   * -1 = remote player's piece
   * -2 = remote player's king
   */
    
    public static final byte X_LENGTH = 4;
    public static final byte Y_LENGTH = 8;
    private byte[][] myGrid;

    private byte mySelectedX = -1;
    private byte mySelectedY = -1;
    private byte myDestinationX = -1;
    private byte myDestinationY = -1;

    private Vector myPossibleMoves = new Vector(4);

    private boolean myGameOver = false;

    private boolean myTurn = false;

    private boolean myIsJumping = false;

    byte getPiece(byte x, byte y) {
        return(myGrid[x][y]);
    }

    boolean isSelected(byte x, byte y) {
        if((x == mySelectedX) && (y == mySelectedY)) {
            return true;
        } else if((x == myDestinationX) && (y == myDestinationY)) {
            return true;
        }
        return false;
    }

    boolean isMyTurn() {
        if((!myGameOver) && ((myTurn) || (myIsJumping))) {
            return true;
        }
        return false;
    }

    boolean getGameOver() {
        return myGameOver;
    }

    void setGameOver() {
        myGameOver = true;
    }

    void start() {
        myGrid = new byte[X_LENGTH][];
        for(byte i = 0; i < myGrid.length; i++) {
            myGrid[i] = new byte[Y_LENGTH];
            for(byte j = 0; j < myGrid[i].length; j++) {
                if(j < 3) {
                    myGrid[i][j] = -1; // fill the top of the board with remote players
                } else if(j > 4) {
                    myGrid[i][j] = 1; // fill the bottom of the board with local players
                }
            }
        }
        
        mySelectedX = 0;
        mySelectedY = 5;
        myTurn = true;
        getMoves(mySelectedX, mySelectedY, myPossibleMoves, false);
    }

    void moveOpponent(int sourceX, int sourceY, int destX, int destY) { //moveOpponent(opMove);
        sourceX = (new Integer(X_LENGTH - sourceX - 1)).byteValue();
        destX = (new Integer(X_LENGTH - destX - 1)).byteValue();
        sourceY = (new Integer(Y_LENGTH - sourceY - 1)).byteValue();
        destY = (new Integer(Y_LENGTH - destY - 1)).byteValue();
        myGrid[destX][destY] = myGrid[sourceX][sourceY];
        myGrid[sourceX][sourceY] = 0;

        if((sourceY - destY > 1) ||  (destY - sourceY > 1)) {
            int jumpedY = (sourceY + destY)/2;
            int jumpedX = sourceX;
            int parity = sourceY % 2;
            if((parity > 0) && (destX > sourceX)) {
                jumpedX++;
            } else if((parity == 0) && (sourceX > destX)) {
                jumpedX--;
            }
            myGrid[jumpedX][jumpedY] = 0;
//#ifdef DEBUG
//#             System.out.println("opponent`s move x("+sourceX+"->"+jumpedX+") y("+sourceY+"->"+jumpedY+")");
//#endif
        }
        // if the opponent reaches the far side, 
        // make him a king:
        if(destY == Y_LENGTH - 1) {
            myGrid[destX][destY] = -2;
        }
    }

    void endOpponentTurn() {
        soundPlay(0);
        myTurn = true;
        mySelectedX = 0; mySelectedY = 0; myDestinationX = -1; myDestinationY = -1;
        rightPressed();
    }

    void leftPressed() {
        if(myDestinationX == -1) {
            selectPrevious();
            if(myPossibleMoves.size() == 0)
                endGame();
        } else {
            for(byte i = 0; i < myPossibleMoves.size(); i++) {
                byte[] coordinates = (byte[])myPossibleMoves.elementAt(i);
                if((coordinates[0] == myDestinationX) &&  (coordinates[1] == myDestinationY)) {
                    i++;
                    i = (new Integer(i % myPossibleMoves.size())).byteValue();
                    coordinates = (byte[])myPossibleMoves.elementAt(i);
                    myDestinationX = coordinates[0]; myDestinationY = coordinates[1];
                    break;
                }
            }
        }
    }

    void rightPressed() {
        if(myDestinationX == -1) {
            selectNext();
            if(myPossibleMoves.size() == 0) {
                endGame();
            }
        } else {
            for(byte i = 0; i < myPossibleMoves.size(); i++) {
                byte[] coordinates = (byte[])myPossibleMoves.elementAt(i);
                if((coordinates[0] == myDestinationX) &&  (coordinates[1] == myDestinationY)) {
                    i++;
                    i = (new Integer(i % myPossibleMoves.size())).byteValue();
                    coordinates = (byte[])myPossibleMoves.elementAt(i);
                    myDestinationX = coordinates[0];
                    myDestinationY = coordinates[1];
                    break;
                }
            }
        }
    }

    void upPressed() {
        if(myDestinationX == -1) {
            fixSelection();
        } else {
            move();
        }
    }

    void deselect() {
        if(myIsJumping) {
            mySelectedX = -1; mySelectedY = -1; myDestinationX = -1; myDestinationY = -1;
            myIsJumping = false; myTurn = false;
            endTurn();
        } else {
            myDestinationX = -1; myDestinationY = -1;
        }
    }

    private void fixSelection() {
        byte[] destination = (byte[])myPossibleMoves.elementAt(0);
        myDestinationX = destination[0]; myDestinationY = destination[1];
    }

    private void selectNext() {
        byte testX = mySelectedX;
        byte testY = mySelectedY;
        while(true) {
            testX++;
            if(testX >= X_LENGTH) {
                testX = 0;
                testY++;
                testY = (new Integer(testY % Y_LENGTH)).byteValue();
            }
            getMoves(testX, testY, myPossibleMoves, false);
            if((myPossibleMoves.size() != 0) ||  ((testX == mySelectedX) && (testY == mySelectedY))) {
                mySelectedX = testX;
                mySelectedY = testY;
                break;
            }
        }
    }

    private void selectPrevious() {
        byte testX = mySelectedX;
        byte testY = mySelectedY;
        while(true) {
            testX--;
            if(testX < 0) {
                testX += X_LENGTH;
                testY--;
                if(testY < 0) {
                    testY += Y_LENGTH;
                }
            }
            getMoves(testX, testY, myPossibleMoves, false);
            if((myPossibleMoves.size() != 0) ||  ((testX == mySelectedX) && (testY == mySelectedY))) {
                mySelectedX = testX;
                mySelectedY = testY;
                break;
            }
        }
    }

    private void move() {
        myGrid[myDestinationX][myDestinationY]  = myGrid[mySelectedX][mySelectedY];

        myGrid[mySelectedX][mySelectedY] = 0;
        if(myDestinationY == 0)
            myGrid[myDestinationX][myDestinationY] = 2;

        move(mySelectedX, mySelectedY,  myDestinationX, myDestinationY);

        if((mySelectedY - myDestinationY > 1) ||  (myDestinationY - mySelectedY > 1)) {
            int jumpedY = (mySelectedY + myDestinationY)/2;
            int jumpedX = mySelectedX;
            int parity = mySelectedY % 2;

            if((parity > 0) && (myDestinationX > mySelectedX)) {
                jumpedX++;
            } else if((parity == 0) && (mySelectedX > myDestinationX)) {
                jumpedX--;
            }

            myGrid[jumpedX][jumpedY] = 0;
            
            soundPlay(1);

            mySelectedX = myDestinationX;
            mySelectedY = myDestinationY;
            myDestinationX = -1;
            myDestinationY = -1;

            getMoves(mySelectedX, mySelectedY, myPossibleMoves, true);

            if(myPossibleMoves.size() != 0) {
                myIsJumping = true;
                byte[] landing = (byte[])myPossibleMoves.elementAt(0);
                myDestinationX = landing[0];
                myDestinationY = landing[1];
            } else {
                myTurn = false;
                endTurn();
            }
        } else {
            mySelectedX = -1; mySelectedY = -1; myDestinationX = -1; myDestinationY = -1;
            myPossibleMoves.removeAllElements();
            myTurn = false;
            endTurn();
        }
    }

    private byte[] getCornerCoordinates(byte x, byte y, byte corner) {
        byte[] retArray = null;
        if(corner < 2) {
            y--;
        } else {
            y++;
        }
        if((corner % 2 == 0) && (y % 2 != 0)) {
            x--;
        } else if((corner % 2 != 0) && (y % 2 == 0)) {
            x++;
        }
        try {
            if(myGrid[x][y] > -15) {
                retArray = new byte[2];
                retArray[0] = x;
                retArray[1] = y;
            }
        } catch(ArrayIndexOutOfBoundsException e) { }
        return(retArray);
    }

    private void getMoves(byte x, byte y, Vector toFill, boolean jumpsOnly) {
        toFill.removeAllElements();
        if(myGrid[x][y] <= 0)
            return;
        for(byte i = 0; i < 4; i++) {
            byte[] coordinates = getCornerCoordinates(x, y, i);
            if((coordinates != null) && ((myGrid[x][y] > 1) || (i < 2))) {
                if((myGrid[coordinates[0]][coordinates[1]] == 0) && (! jumpsOnly)) {
                    toFill.addElement(coordinates);
                } else if(myGrid[coordinates[0]][coordinates[1]] < 0) {
                    byte[] jumpLanding = getCornerCoordinates(coordinates[0], coordinates[1], i);
                    if((jumpLanding != null) &&  (myGrid[jumpLanding[0]][jumpLanding[1]] == 0)) {
                        toFill.addElement(jumpLanding);
                    }
                }
            }
        }
    }
    
    
    public void soundPlay(int state){
        String tone=(state>0)?"E6A6":"A6";
        try {
            for (int i=0; i<tone.length(); ) {
                int note=(tone.charAt(i++)-'A')+12*(tone.charAt(i++)-'0');
                int duration=150;
                Manager.playTone(note, duration, 100);
                Thread.sleep(duration);
            }
        } catch (Exception e) { }
    }
}
