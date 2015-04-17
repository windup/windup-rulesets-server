package org.jboss.windup.utils.compar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * Compares a list of hashes with actual files in a directory tree.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class FileHashComparer
{
    private static final Logger log = Logger.getLogger( FileHashComparer.class.getName() );


    public static enum MatchResult
    {
        MATCH("MATCH   "), MISMATCH("MISMATCH"), MISSING("MISSING "), EMPTY("EMPTY   ");
        final String padded;
        private MatchResult( String padded ) { this.padded = padded; }
        public final String rightPad(){ return this.padded; }
    };


    /**
     *  Reads the hashes from file and compares each entry to the respective file in given base $dir.
     *  Hashes file format is:
     *       92ae740a ./bin/twiddle.bat
     *
     *  The filter is applied to the path from the hash file, so it must only work with name, not the actual file -
     *  so e.g. fileFileFilter() can't be used.
     */
    public static ComparisonResult compareHashesAndDir( File hashes, File dir, IOFileFilter filter ) throws FileNotFoundException, IOException{
        Map<Path, MatchResult> results = compareHashesAndDir( readHashes(hashes), dir, filter );
        return new ComparisonResult( dir ).setMatches( results ).setHashes( hashes );
    }

    public static ComparisonResult compareHashesAndDir( InputStream hashesIS, File dir, IOFileFilter filter ) throws FileNotFoundException, IOException{
        Map<Path, MatchResult> results = compareHashesAndDir( readHashes(hashesIS), dir, filter );
        return new ComparisonResult( dir ).setMatches( results );
    }

    /**
     *  This is the method which actually creates the map keys and values.
     *
     *  @returns  A map with keys being paths in the server dir (not including path to that),
     *            and values being MatchResult values (MISSING, MATCH, MISMATCH, EMPTY).
     */
    private static Map<Path, MatchResult> compareHashesAndDir( Map<String, Long> hashes, File dir, IOFileFilter filter ) throws IOException {
        Map<Path, MatchResult> matches = new HashMap();

        // Iterate through hashes and compare with files.
        for( Map.Entry<String, Long> entry : hashes.entrySet() ) {
            String path = entry.getKey();

            // Apply the filter.
            if( filter != null && ! filter.accept( new File(path) ) )
                continue;

            File file = new File(dir, path);
            //Path pathNormFull = file.toPath().normalize();
            Path pathNormSub  = new File(".", path).toPath().normalize();

            if( ! file.exists() ){
                matches.put( pathNormSub, MatchResult.MISSING );
                continue;
            }
            if( file.length() == 0 ){
                matches.put( pathNormSub, MatchResult.EMPTY );
                continue;
            }
            long hashReal = computeCrc32(file);
            Long hash     = entry.getValue();
            matches.put( pathNormSub, hash == hashReal ? MatchResult.MATCH : MatchResult.MISMATCH );
        }
        return matches;
    }


    /**
     *  Reads a file of format
     *    92ae740a ./bin/twiddle.bat
     *  and returns a map of paths -> hashes.
     *  The paths are normalized, while kept relative. I.e. ./foo/../bar/a results in bar/a .
     */
    public static Map<String, Long> readHashes( File file ) throws FileNotFoundException {
        return readHashes( new FileInputStream(file) );
    }

    public static Map<String, Long> readHashes( InputStream hashesIS ) throws FileNotFoundException {

        Scanner sc = new Scanner( hashesIS );
        Map<String, Long> hashes = new HashMap();

        //  92ae740a ./bin/twiddle.bat
        while( sc.hasNextLine() ){
            try {
                long hash = sc.nextLong(16);
                String path = sc.nextLine();
                path = path.trim();
                path = Paths.get(path).normalize().toString();
                hashes.put( path, hash );
            }
            catch( NoSuchElementException ex ){
                log.log(Level.WARNING, "Failed parsing line: " + sc.nextLine(), ex);
                sc.nextLine();
            }
        }
        return hashes;
    }


   public static long computeCrc32(File file)
   {
        try(CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new CRC32()))
        {
            byte[] buf = new byte[1024];
            while(cis.read(buf) >= 0)
                ; // Intended.

            return cis.getChecksum().getValue();
        }
        catch( Exception ex )
        {
            throw new RuntimeException("Can't compute CRC32 checksum - " + ex.getMessage() + ": " + file.getPath());
        }
    }


}// class
