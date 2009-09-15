/*
 * Uptecs Jakar standard library.
 *
 * Copyright(c)2006-2007, Uptecs.
 */
package org.uptecs.email;

import java.io.*;

/**
 * Simple code to do base 64 encoding. Useful when wishing to avoid
 * big bulky library to do the job.
 */
public class Base64Encode {

    /*
     * Lookup table of base 64 characters, this is quicker than
     * calculating mathematically (I hope, this should be tested really).
     */
    protected static final byte[] lookupMap = {
        'A','B','C','D','E','F','G','H',
        'I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X',
        'Y','Z','a','b','c','d','e','f',
        'g','h','i','j','k','l','m','n',
        'o','p','q','r','s','t','u','v',
        'w','x','y','z','0','1','2','3',
        '4','5','6','7','8','9','+','/',
    };

    /*
     * End of stream marker
     */
    private static final int END_OF_INPUT = -1;

    /**
     * Encode a String in Base64 using default character encoding.
     *
     * @param string The string to encode.
     * @return A base 64 encoded String.
     */
    public static String encode(String string){
        return encode(string.getBytes());
    }

    /**
     * Encode bytes in Base64.
     *
     * @param bytes The data to encode.
     * @return Encoded base 64 string.
     */
    public static String encode(byte[] bytes){
        //USED
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        /*
         * Number of bytes will be equal to (string * (4/3)) rounded
         * upwards.
         */
        int mod;
        int length = bytes.length;
        if ((mod = length % 3) != 0){
            length += 3 - mod;
        }
        length = length * 4 / 3;
        ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        try {
            encode(in, out);
        } catch (IOException x){
            /* This should never happen! */
            throw new RuntimeException(x);
        }
        return new String(out.toByteArray());
    }

    /**
     * Encode data from the InputStream to the OutputStream in Base64.
     *
     * @param in Stream from which to read data that needs to be encoded.
     * @param out Stream to which to write encoded data.
     * @throws IOException if there is a problem reading or writing.
     *
     */
    public static void encode(InputStream in, OutputStream out)
    throws IOException {
        int[] inBuffer = new int[3];
        
        boolean done = false;
        while (!done && (inBuffer[0] = in.read()) != END_OF_INPUT){
            // Fill the buffer
            inBuffer[1] = in.read();
            inBuffer[2] = in.read();
            
            /*
             * The first byte of input is valid but must check other two bytes
             * are not equal to END_OF_INPUT. Each set of three bytes add up to
             * 24 bits, the 24 bits are split up into 4 bytes of 6 bits and
             * converted to ascii characters.
             *
             * If there are not enough bytes to make a 24 bit group, the
             * remaining ascii characters are converted to the = character.
             */
            
            // Byte 1: first six bits of first byte
            out.write(lookupMap[inBuffer[0]>>2 ]);
            if (inBuffer[1]!=END_OF_INPUT){
                // Byte 2: last two bits of first byte, first four bits of second byte
                out.write(lookupMap[((inBuffer[0]<<4)&0x30)|(inBuffer[1]>>4)]);
                if (inBuffer[2] != END_OF_INPUT){
                    // Byte 3: last four bits of second byte, first two bits of third byte
                    out.write(lookupMap[((inBuffer[1]<<2)&0x3c)|(inBuffer[2]>>6) ]);
                    // Byte 4: last six bits of third byte
                    out.write(lookupMap[inBuffer[2]&0x3F]);
                } else {
                    // Byte 3: last four bits of second byte
                    out.write(lookupMap[((inBuffer[1]<<2)&0x3c)]);
                    // Output = if no more characters to write
                    out.write('=');
                    done = true;
                }
            } else {
                // Byte 2: last two bits of first byte
                out.write(lookupMap[((inBuffer[0]<<4)&0x30)]);
                // Output = if no more characters to write
                out.write('=');
                out.write('=');
                done=true;
            }
        }
        out.flush();
    }  
    
}
