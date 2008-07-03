/*
 * ServiceDiscoveryListener.java
 *
 * Created on 4.06.2005 г., 21:51
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
