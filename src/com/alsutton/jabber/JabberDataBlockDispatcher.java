/*
  Copyright (c) 2000,2001 Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.

  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.alsutton.jabber;
import com.alsutton.jabber.datablocks.Iq;
import java.util.*;
import xmpp.XmppError;

/**
 * The dispatcher for blocks that have arrived. Adds new blocks to the
 * dispatch queue, and then dispatches waiting blocks in their own thread to
 * avoid holding up the stream reader.
 */

public class JabberDataBlockDispatcher extends Thread
{
  /**
   * The recipient waiting on this stream
   */

  private JabberListener listener = null;
  private JabberStream stream;

  private Vector blockListeners=new Vector();

  /**
   * The list of messages waiting to be dispatched
   */

  private Vector waitingQueue = new Vector();

  /**
   * Flag to watch the dispatching loop
   */

  private boolean dispatcherActive;

  boolean isActive() { return dispatcherActive; }

  /**
   * Constructor to start the dispatcher in a thread.
   */

  public JabberDataBlockDispatcher(JabberStream stream)  {
      this.stream=stream;
      start();
  }

  /**
   * Set the listener that we are dispatching to. Allows for switching
   * of clients in mid stream.
   *
   * @param _listener The listener to dispatch to.
   */

  public void setJabberListener( JabberListener _listener )
  {
    listener = _listener;
  }

  public void addBlockListener(JabberBlockListener listener) {
      synchronized (blockListeners) { 
          if (blockListeners.indexOf(listener) > 0) return;
          blockListeners.addElement(listener); 
      }
  }
  public void cancelBlockListener(JabberBlockListener listener) {
      synchronized (blockListeners) { 
          try { blockListeners.removeElement(listener); }
          catch (Exception e) {
              e.printStackTrace(); 
          }
      }
  }
  
  /**
   * Method to add a datablock to the dispatch queue
   *
   * @param datablock The block to add
   */

  public void broadcastJabberDataBlock( JabberDataBlock dataBlock )
  {
    waitingQueue.addElement( dataBlock );
        while( !waitingQueue.isEmpty() ) {
            try {
                Thread.sleep( 50L );
            } catch( InterruptedException e ) { }
    }
  }

  /**
   * The thread loop that handles dispatching any waiting datablocks
   */

    public void run(){
        dispatcherActive = true;
        while( dispatcherActive ) {
            while( waitingQueue.isEmpty() ) {
                try {
                    Thread.sleep( 100L );
                } catch( InterruptedException e ) { }
            }

            JabberDataBlock dataBlock = (JabberDataBlock) waitingQueue.elementAt(0);
            waitingQueue.removeElementAt( 0 );

            try {
                int processResult=JabberBlockListener.BLOCK_REJECTED;
                synchronized (blockListeners) {
                    int i=0;
                    while (i<blockListeners.size()) {
                        processResult=((JabberBlockListener)blockListeners.elementAt(i)).blockArrived(dataBlock);
                        if (processResult==JabberBlockListener.BLOCK_PROCESSED) break;
                        if (processResult==JabberBlockListener.NO_MORE_BLOCKS) {
                            blockListeners.removeElementAt(i); break;
                        }
                        i++;
                    }
                }
                if (processResult==JabberBlockListener.BLOCK_REJECTED)
                    if( listener != null )
                        processResult=listener.blockArrived( dataBlock );
                
                if (processResult==JabberBlockListener.BLOCK_REJECTED) {
                    if (!(dataBlock instanceof Iq)) continue;
                    
                    String type=dataBlock.getTypeAttribute();
                    if (type.equals("get") || type.equals("set")) {
                        dataBlock.setAttribute("to", dataBlock.getAttribute("from"));
                        dataBlock.setAttribute("from", null);
                        dataBlock.setTypeAttribute("error");
                        dataBlock.addChild(new XmppError(XmppError.FEATURE_NOT_IMPLEMENTED, null).construct());
                        stream.send(dataBlock);
                    }
                    //TODO: reject iq stansas where type =="get" | "set"
                }
//#ifdef CONSOLE
//#                 stream.addLog(dataBlock.toString(), 10);
//#endif
            } catch (Exception e) { }
        }
    }
    
  public void cancelBlockListenerByClass(Class removeClass){
      synchronized (blockListeners) {
          int index=0;
          while (index<blockListeners.size()) {
              Object list=blockListeners.elementAt(index);
              if (list.getClass().equals(removeClass)) blockListeners.removeElementAt(index); 
              else index++;
          }
      }
  }

  public void rosterNotify(){
    listener.rosterItemNotify();
  }

  /**
   * Method to stop the dispatcher
   */
  
  public void halt()
  {
    dispatcherActive = false;
  }

  /**
   * Method to tell the listener the connection has been terminated
   *
   * @param exception The exception that caused the termination. This may be
   * null for the situtations where the connection has terminated without an
   * exception.
   */

  public void broadcastTerminatedConnection( Exception exception )
  {
    halt();
    if( listener != null ) listener.connectionTerminated( exception );
  }

  /**
   * Method to tell the listener the stream is ready for talking to.
   */

  public void broadcastBeginConversation()
  {
    if( listener != null ) listener.beginConversation();
  }
}
