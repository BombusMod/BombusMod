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

package com.alsutton.xmlparser;

/**
 * The main XML Parser class.
 */

import io.Utf8IOStream;
import java.io.*;

import java.util.*;

public class XMLParser
{
  /** The reader from which the stream is being read  */
    Utf8IOStream iostream;

  /** The handler for XML Events. */

  private XMLEventListener eventHandler;

  /** The root tag for the document. */

  private String rootTag = null;
  
  public static final int MAX_BLOCK_SIZE=8192; //anti-flood limiter

  private final static int MAX_BIN_DATASIZE=64*1024; //64 KB - experimental

  /** Constructor, Used to override default dispatcher.
   *
   * @param _eventHandler The event handle to dispatch events through.
   */

  public XMLParser( XMLEventListener _eventHandler )
  {
    eventHandler = _eventHandler;
  }

  private StringBuffer streamData = new StringBuffer(16);
  /**
   * Method to read until an end condition.
   *
   * @param checker The class used to check if the end condition has occurred.
   * @return A string representation of the data read.
   */
  
  private String readUntilEnd( int tagBracket )
    throws IOException, EndOfXMLException
  {
    //StringBuffer streamData = new StringBuffer(16);
    streamData.setLength(0);
    StringBuffer xmlChar = null;
    int inQuote = 0;    // 0 or " or '
    boolean inXMLchar=false;

    int nextChar;
    
    do {
        nextChar = iostream.getNextCharacter();
        if (nextChar==-1) break;
     
        if (nextChar==tagBracket)
            if (inQuote==0) break;
        
        if (nextChar==' ')
            if (inQuote==0 && tagBracket=='>') break;
        
        switch (nextChar) {
            case '\'': // '
            case '\"': // "
		if (tagBracket=='<') break; //bypassing quotes to output
                if (inQuote==0) { inQuote=nextChar; continue; } //extracting quoted attribute
                if (inQuote==nextChar) { inQuote=0; continue; } //quoted string complete
                break; //bypassing non-paired quote to output
            case '&':
                inXMLchar=true;
                xmlChar=new StringBuffer(6);
                continue;
            case ';':
                if (inXMLchar) {
                    inXMLchar=false;
                    String s=xmlChar.toString();
                    if (s.equals("amp")) nextChar='&'; else
                    if (s.equals("apos")) nextChar='\''; else
                    if (s.equals("quot")) nextChar='\"'; else
                    if (s.equals("gt")) nextChar='>'; else
                    if (s.equals("lt")) nextChar='<'; else
                    if (xmlChar.charAt(0)=='#') 
                        try {
                            xmlChar.deleteCharAt(0);
                            nextChar=Integer.parseInt(xmlChar.toString());
                        } catch (Exception e) {
                            nextChar=' ';
                        }
                }
        }
        
        if (inXMLchar) {
            xmlChar.append( (char) nextChar );
            continue;
        }
        
        //appending character
        if (streamData.length()<MAX_BLOCK_SIZE)
            streamData.append( (char) nextChar );
        else if (streamData.length()==MAX_BLOCK_SIZE)
            streamData.append("...");
        
    } while (true);
     
    if( nextChar == -1 && streamData.length()==0)
      throw new EndOfXMLException();
    
    if( nextChar != '<' && nextChar != '>')
      streamData.append( (char) '\n' );

    //String returnData = streamData.toString();
    //System.out.println(returnData);
    xmlChar=null;
    return streamData.toString();
  }

  
  /**
   * Method to handle the reading and dispatch of tag data.
   */

  private boolean handleTag()
    throws IOException, EndOfXMLException
  {
    boolean startTag = true,
            emptyTag = false,
            hasMoreData = true;
    String tagName = null;
    Vector attributes = null;

    do
    {
      String data = readUntilEnd ( '>' );
      int substringStart = 0,
          substringEnd = data.length();

      if( data.startsWith( "/" )  ) if (data.length()>1) {
        startTag = false;
        substringStart++;
      }

      if( data.endsWith( "/" ) ) {
        emptyTag = true;
        substringEnd--;
      }

      hasMoreData = data.endsWith( "\n" );
      if( hasMoreData )
        substringEnd--;

      data = data.substring( substringStart, substringEnd );

      if( tagName == null )
      {
        tagName = data;//.toLowerCase();
        continue;
      }

      if( attributes == null )
        attributes = new Vector();

      int stringLength = data.length();
      int equalitySign = data.indexOf( '=' );
      if( equalitySign == -1 )
      {
        if( hasMoreData )
          continue;
        else
          break;
      }

      String attributeName = data.substring(0, equalitySign);
      int valueStart = equalitySign+1;
      if( valueStart >= data.length() ) {
        attributes.addElement(attributeName);
        attributes.addElement("");
        continue;
      }

      //substringStart = valueStart;
      //char startChar = data.charAt( substringStart );
      //if( startChar  == '\"' || startChar  == '\'' )
      //  substringStart++;

      //substringEnd = stringLength;
      //char endChar = data.charAt( substringEnd-1 );
      //if( substringEnd > substringStart && endChar  == '\"' || endChar  == '\'' )
      //  substringEnd--;

      attributes.addElement(attributeName);
      attributes.addElement(data.substring( valueStart, stringLength ));
    } while( hasMoreData );

    if( tagName.startsWith( "?") )
      return false;

    //tagName = tagName;//.toLowerCase();
    
    boolean binflag=false;
    if( startTag ) {
       if( rootTag == null )
          rootTag = tagName;
      if (attributes!=null) attributes.trimToSize();
       binflag=eventHandler.tagStarted( tagName, attributes);
     }

    if( emptyTag || !startTag )
    {
      eventHandler.tagEnded( tagName );
      if( rootTag != null && tagName.equals( rootTag ) )
        throw new EndOfXMLException();
    }
    
    return binflag;
  }

  /**
   * Method to handle the reading in and dispatching of events for plain text.
   */

  private void handlePlainText()
    throws IOException, EndOfXMLException
  {
    String data = readUntilEnd ( '<' );
    eventHandler.plaintextEncountered( data );
  }

  private void handleBinValue() 
    throws IOException, EndOfXMLException
  {
      int padding=0;
      int ibuf=1;
      ByteArrayOutputStream baos=new ByteArrayOutputStream(2048);
      while (true) {
          int nextChar = iostream.getNextCharacter();
          if( nextChar == -1 )
              throw new EndOfXMLException();
          int base64=-1;
          if (nextChar>'A'-1 && nextChar<'Z'+1) base64=nextChar-'A';
          else if (nextChar>'a'-1 && nextChar<'z'+1) base64=nextChar+26-'a';
          else if (nextChar>'0'-1 && nextChar<'9'+1) base64=nextChar+52-'0';
          else if (nextChar=='+') base64=62;
          else if (nextChar=='/') base64=63;
          else if (nextChar=='=') {base64=0; padding++;}
          else if (nextChar=='<') break;
          if (base64>=0) ibuf=(ibuf<<6)+base64;
          if (baos.size()<MAX_BIN_DATASIZE) {
              if (ibuf>=0x01000000){
                  baos.write((ibuf>>16) &0xff);
                  if (padding<2) baos.write((ibuf>>8) &0xff);
                  if (padding==0) baos.write(ibuf &0xff);
                  //len+=3;
                  ibuf=1;
              }
          }
      }
      baos.close();
      //System.out.println(ibuf);
      //System.out.println(baos.size());
      if (baos.size()<MAX_BIN_DATASIZE) 
          eventHandler.binValueEncountered( baos.toByteArray() );
  }
  /**
   * The main parsing loop.
   *
   * @param _inputReader The reader for the XML stream.
   */

  public void  parse ( Utf8IOStream iostream )
    throws IOException, EndOfXMLException
  {
    this.iostream=iostream;
    boolean binval=false;
    
    //try {
        while( true ) {
            if (binval)
                handleBinValue();
            else
                handlePlainText();
            binval=handleTag();
        }
    //} catch( EndOfXMLException x ) {
        // The EndOfXMLException is purely used to drop out of the
        // continuous loop.
    //} catch ( Exception e ) {
    //    e.printStackTrace();
    //}
 }
}
