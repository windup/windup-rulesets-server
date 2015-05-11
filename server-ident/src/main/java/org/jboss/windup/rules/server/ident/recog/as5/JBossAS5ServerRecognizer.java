package org.jboss.windup.rules.server.ident.recog.as5;


import org.jboss.windup.rules.server.ident.recog.api.ServerRecognizerInfo;
import org.jboss.windup.rules.server.ident.recog.api.ServerRecognizerBase;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.jboss.windup.rules.server.ident.recog.HasHashes;
import org.jboss.windup.rules.server.ident.recog.api.Version;
import org.jboss.windup.rules.server.ident.recog.api.VersionRange;
import org.jboss.windup.utils.compar.ComparisonResult;
import org.jboss.windup.utils.compar.FileHashComparer;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@ServerRecognizerInfo(
        description = "JBoss AS 5.x or 6.x, or JBoss EAP 5.x",
        vendor = "JBoss",
        model = "EAP",
        mustHave = {JBossAS5ServerRecognizer.JAR_VERSIONS_XML, "bin/run.sh", "lib/jboss-main.jar"},
        versionUniqueFile = JBossAS5ServerRecognizer.JAR_VERSIONS_XML,
        // TODO: versionUniqueFileHashes
        hashFiles = {
            @HashFile(path = JBossAS5ServerRecognizer.HASH_FILES_PATH + "jboss-eap-5.0.0-crc32.txt", version = "5.0.0"),
            @HashFile(path = JBossAS5ServerRecognizer.HASH_FILES_PATH + "jboss-eap-5.0.0-unsigned-crc32.txt", version = "5.0.0"),
            @HashFile(path = JBossAS5ServerRecognizer.HASH_FILES_PATH + "jboss-eap-5.0.1-crc32.txt", version = "5.0.1"),
            @HashFile(path = JBossAS5ServerRecognizer.HASH_FILES_PATH + "jboss-eap-5.1.0-crc32.txt", version = "5.1.0"),
            @HashFile(path = JBossAS5ServerRecognizer.HASH_FILES_PATH + "jboss-eap-5.1.0-unsigned-crc32.txt", version = "5.1.0"),
            @HashFile(path = JBossAS5ServerRecognizer.HASH_FILES_PATH + "jboss-eap-5.1.1-crc32.txt", version = "5.1.1"),
            @HashFile(path = JBossAS5ServerRecognizer.HASH_FILES_PATH + "jboss-eap-5.1.1-unsigned-crc32.txt", version = "5.1.1"),
            @HashFile(path = JBossAS5ServerRecognizer.HASH_FILES_PATH + "jboss-eap-5.1.2-crc32.txt", version = "5.1.2"),
            @HashFile(path = JBossAS5ServerRecognizer.HASH_FILES_PATH + "jboss-eap-5.2.0-crc32.txt", version = "5.2.0"),
        }
)
public class JBossAS5ServerRecognizer extends ServerRecognizerBase implements  HasHashes
{
    private static final Logger log = Logger.getLogger(JBossAS5ServerRecognizer.class.getName());

    @Override public String getDescription() { return "JBoss AS 5.x or 6.x, or JBoss EAP 5.x"; }

    protected static final String JAR_VERSIONS_XML = "jar-versions.xml";
    protected static final String HASH_FILES_PATH = "/fileHashes/as5/";



    /**
     * First checks jar-versions.xml. If that's not present, then compares checksums of all jars.
     */
    @Override
    public VersionRange recognizeVersion()
    {
        if( ! isCanRecognizeDir( getServerRootDir() ) )
            return new VersionRange();

        // Check jar-versions.xml.
        File jarVersionsFile = new File( getServerRootDir(), JAR_VERSIONS_XML );
        try {
            // Check if we know that file's CRC32; if so, use that version.
            long jarVerCrc = FileHashComparer.computeCrc32(jarVersionsFile);
            String ver = getJarVersionsXmlCrcToVersionsMap().get( jarVerCrc );
            if( ver != null )
                return VersionRange.forProduct( ver, ver, new AsToEapMap() );
        }
        catch (Exception ex)
        {
            log.log(Level.SEVERE, "Failed computing CRC32 of " + jarVersionsFile.getPath() + ": " + ex.getMessage(), ex);
        }


        // No match - check .jars.
        IOFileFilter filter = FileFilterUtils.suffixFileFilter(".jar");

        int minMismatches = Integer.MAX_VALUE;
        HashFileInfo minMisHF = null;

        // Compare the directory against each hash file.
        for( HashFileInfo hashFile : getHashFiles()) {
            try {
                InputStream is = this.getClass().getResourceAsStream(hashFile.getPath());

                ComparisonResult result = FileHashComparer.compareHashesAndDir( is, getServerRootDir(), filter );
                log.finer(String.format("   Comparison of .jar's in %s against %s: %d of %d match.", getServerRootDir().getPath(), hashFile.getPath(),
                        result.getCountMatches(), result.getCountTotal() ));
                int curMismatches = result.getCountMismatches();
                if( curMismatches < minMismatches){
                     minMisHF = hashFile;
                     minMismatches = curMismatches;
                }
            }
            catch( IOException ex ) {
                throw new RuntimeException("Failed comparing dir " + getServerRootDir().getPath() + " against hashfile " + hashFile.getPath() + ": " + ex.getMessage(), ex);
            }
        }

        // If there's some almost certain match, return that as recognized version.
        if( minMisHF != null )
            return VersionRange.forProduct( minMisHF.getVersion(), minMisHF.getVersion(), new AsToEapMap() );

        // Default range - all we know - AS 5 to AS 6.
        return new VersionRange("5.0.0", "6");
    }


    // TODO: Move to annotations and base class.
    @Override
    public boolean isCanRecognizeDir( )
    {
        if( ! new File(getServerRootDir(), JAR_VERSIONS_XML).exists() )
            return false;
        if( ! new File(getServerRootDir(), "bin/run.sh").exists() )
            return false;
        if( ! new File(getServerRootDir(), "lib/jboss-main.jar").exists() )
            return false;

        return true;
    }


    protected InputStream getHashFileForVersion( String ver ){
        for( HashFileInfo hashFile : getHashFiles() ) {
            if( hashFile.getVersion().equals(ver) ){
                String path = hashFile.getPath();
                InputStream is = JBossAS5ServerRecognizer.class.getResourceAsStream(path);
                if( is != null ) return is;
                throw new IllegalStateException("Hash file not found on classpath: " + path);
            }
        }
        return null;
    }

    // jar-versions.xml CRC32 -> versions.
    private static Map<Long, String> getJarVersionsXmlCrcToVersionsMap()
    {
        return JAR_VERSIONS_XML_CRC_TO_VERSION_MAP;
    }

    private static final Map<Long, String> JAR_VERSIONS_XML_CRC_TO_VERSION_MAP = new HashMap();

    // TODO: versionUniqueFileHashes
    static {
        Map<Long, String> map = JAR_VERSIONS_XML_CRC_TO_VERSION_MAP;
        map.put( 0x9f12a476L, "5.0.0");
        map.put( 0x9e98373eL, "5.0.1");
        map.put( 0x2b9c02cbL, "5.1.0");
        map.put( 0x52e957e7L, "5.1.1");
        map.put( 0x10c95871L, "5.1.2");
        map.put( 0xb7414c39L, "5.2.0");
    }


    @Override
    public ComparisonResult compareHashes(Version version) throws Exception
    {
        if( version.getVerProduct() == null )
            throw new Exception("Comparing file hashes is only supported for EAP, not AS. Supplied version was: " + version.getVerProject());

        InputStream hashFile = getHashFileForVersion(version.getVerProduct());
        if( null == hashFile )
            throw new Exception("No hash files for EAP version: " + version.getVerProduct());
        try
        {
            return FileHashComparer.compareHashesAndDir(hashFile, getServerRootDir(), null);
        }
        catch( Exception ex )
        {
            String msg = String.format("Failed comparing hashes of %s against dir %s:%n    ", this.format(version), getServerRootDir());
            throw new Exception(msg + ex.getMessage(), ex);
        }
    }


    /**
     *  Formats a string like "JBoss AS 5.1.0" or "JBoss EAP 5.2.0+" etc.
     */
    @Override
    public String format(VersionRange versionRange)
    {
        StringBuilder sb = new StringBuilder("JBoss ");
        // Version unknown
        if( versionRange == null )
            return sb.append("AS or EAP 5").toString();
        // AS or EAP?
        sb.append(versionRange.getFrom().getVerProduct() == null ? "AS " : "EAP ");
        sb.append(versionRange.getFrom().toString_preferProduct());
        // Range?
        if( versionRange.isExactVersion() )
            return sb.toString();
        sb.append(" - ").append(versionRange.getTo_preferProduct());
        return sb.toString();
    }


    public String format(Version version)
    {
        StringBuilder sb = new StringBuilder("JBoss ");
        // Version unknown
        if( version == null )
            return sb.append("AS or EAP 5").toString();
        // AS or EAP?
        sb.append(version.getVerProduct() == null ? "AS " + version.getVerProject() : "EAP " + version.getVerProduct());
        return sb.toString();
    }


}// class
