/*
 * ServiceDiscoveryListener.java
 *
 * Created on 4 Июнь 2005 г., 21:51
 */

package com.alsutton.jabber;

/**
 *
 * @author Evg_S
 */
public interface JabberBlockListener {
   public final static int BLOCK_REJECTED=0;
   public final static int BLOCK_PROCESSED=1;
   public final static int NO_MORE_BLOCKS=2;
  /**
   * Method to handle an incomming block.
   *
   * @parameter data The incomming block
   */

  public int blockArrived(JabberDataBlock data);
    
}
