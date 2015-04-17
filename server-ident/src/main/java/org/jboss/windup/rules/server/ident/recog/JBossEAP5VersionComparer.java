package org.jboss.windup.rules.server.ident.recog;


import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jboss.windup.rules.server.api.VersionComparer;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class JBossEAP5VersionComparer implements VersionComparer
{
     private static final Logger log = Logger.getLogger(JBossEAP5VersionComparer.class.getName() );


    /**
     * Strings after release:  SP, CP
     * Strings for release:    GA, GOLD, Final, 0
     * Latest before release:  SNAPSHOT
     * Before release:         (any other)
     */
    private static int getStringLevel(String s)
    {
        if( "SP".equalsIgnoreCase(s)  ||  "CP".equalsIgnoreCase(s) )
             return 1;
        if( "GA".equalsIgnoreCase(s)  ||  "GOLD".equalsIgnoreCase(s)  ||  "Final".equalsIgnoreCase(s)  ||  "0".equals(s) )
             return 0;
        if( "SNAPSHOT".equalsIgnoreCase(s) )
             return -1;
        return -2;
    }

    /**
     * Compares two string parts of the version string, e.g. "GA" vs. "SNAPSHOT".
     * @returns like compareTo() does.
     */
    protected static int compareStringParts(String a, String b)
    {
        int aLev = getStringLevel( a );
        int bLev = getStringLevel( b );

        if( aLev == bLev )
            return a.compareToIgnoreCase(b);
        else
            return aLev - bLev;
    }


    /**
     * Compares two version strings, like compareTo() does.
     * TODO: Maven comparison algorithm works bad; Code own one.
     *
     * Strings after release:  SP, CP
     * Strings for release:    GA, GOLD
     * Latest before release:  SNAPSHOT
     * Before release:         (any other)
     */
    @Override
    public int compareVersions(String aStr, String bStr)
    {

        log.finer( String.format("Comparing versions:  %s  and  %s ...", aStr, bStr) );

        final String[] aParts = StringUtils.split(aStr.toUpperCase(), ".-_");
        final String[] bParts = StringUtils.split(bStr.toUpperCase(), ".-_");

        //log.debug( String.format(" aLen = %d, bLen = %d", aParts.length, bParts.length) );

        int aOff = 0;
        int bOff = 0;

        //int longer = 0;
        //for( int i = v1parts.length-1; i >= 0; i--){ }


        parts: do {

            boolean aIsLast = aOff >= aParts.length - 1;
            boolean bIsLast = bOff >= bParts.length - 1;

            String a = aParts[aOff];
            String b = bParts[bOff];

            // a pseudo-loop - just to use "continue".
            thisPart: do {

                if( StringUtils.equals(a, b) )
                    continue;

                log.finer( String.format("A[%d] = '%s', B[%d] = '%s'", aOff, a, bOff, b) );

                if( "GA".equals(a) )   a = "0";
                if( "GA".equals(b) )   b = "0";

                boolean aIsNum = NumberUtils.isDigits( a );
                boolean bIsNum = NumberUtils.isDigits( b );
                Integer aNum = aIsNum ? NumberUtils.createInteger( a ) : null;
                Integer bNum = bIsNum ? NumberUtils.createInteger( b ) : null;

                log.finer( String.format("A[%d] = '%s', B[%d] = '%s'", aOff, a, bOff, b) );


                if( aIsNum && bIsNum ){
                    log.finer("aIsNum && bIsNum");
                    int diff = aNum - bNum;
                    // Different numbers - return the diff.
                    if( diff != 0 ) return diff;
                    // The same numbers - skip to next position.
                    break thisPart;
                }

                else if( aIsNum ^ bIsNum ){
                    log.finer("aIsNum ^ bIsNum");
                    if( aIsNum ){
                        // Skip aditional "decimal" zeros - e.g.  2.0.0.FOO vs. 2.0.BAR .
                        if ( aNum == 0 ){
                            if( ! aIsLast ){ aOff++; continue parts; }
                            return compareStringParts("0", b); // or: +getStringLevel(a);
                        }
                        return 1;
                    }
                    else /* vice versa for ( bIsNum ) */ {
                        // Skip aditional "decimal" zeros - e.g.  2.0.0.FOO vs. 2.0.BAR .
                        if ( bNum == 0 ){
                            if( ! bIsLast ){ bOff++; continue parts; }
                            return compareStringParts(a, "0"); // or: -getStringLevel(b);
                        }
                        return -1;
                    }
                }

                // Neither is a number -> Compare two strings.
                {
                    log.finer("Comparing strings produces" + compareStringParts(a, b) );
                    int strDiff = compareStringParts(a, b);
                    if( strDiff != 0 )
                         return strDiff;
                }

            } while( false ); // label thisPart:


            // Advance to the next part, unless we're at the last one..
            //if( aOff < aParts.length -1 )  aOff++;
            //if( bOff < bParts.length -1 )  bOff++;
            log.finer( String.format("Incrementing... %s %s", aIsLast, bIsLast) );
            if( ! aIsLast )  aOff++;
            if( ! bIsLast )  bOff++;

            if( aIsLast && bIsLast ) break;

        // End when we reached the last parts of both versions.
        } while ( true );
        //} while(  aOff < aParts.length     -1  ||  bOff < bParts.length -1  );

        log.finer("Reached comparison loop end.");

        return 0;  // TODO: Fix

    }

}
