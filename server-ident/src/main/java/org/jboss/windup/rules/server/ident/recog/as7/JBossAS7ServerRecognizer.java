package org.jboss.windup.rules.server.ident.recog.as7;

import java.io.File;
import java.util.logging.Logger;
import org.jboss.windup.rules.server.ident.recog.api.ServerRecognizer;
import org.jboss.windup.rules.server.ident.recog.api.VersionRange;
import org.jboss.windup.rules.server.ident.recog.api.ServerRecognizerBase;
import org.jboss.windup.rules.server.ident.recog.api.ServerRecognizerInfo;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@ServerRecognizerInfo(
        description = "JBoss AS 7.x, or WildFly 8+, or JBoss EAP 6+",
        vendor = "JBoss",
        model = "EAP",
        mustHave = {"bin/standalone.sh", "modules/"},
        versionUniqueFile = "",
        hashFiles = {
            //@HashFile(path = JBossAS7ServerRecognizer.HASH_FILES_PATH + "jboss-eap-5.0.0-crc32.txt", version = "5.0.0"),
        }
)
public class JBossAS7ServerRecognizer extends ServerRecognizerBase implements ServerRecognizer
{
    private static final Logger log = Logger.getLogger(JBossAS7ServerRecognizer.class.getName());

    @Override public String getDescription() { return "JBoss AS 7+ or JBoss EAP 6+"; }


    protected static final String HASH_FILES_PATH = "/fileHashes/as7/";



    @Override
    public VersionRange recognizeVersion(  ) {
        if( isCanRecognizeDir( getServerRootDir() ) )
            return new VersionRange( "7.0.0", null );
        return new VersionRange();
    }


    @Override
    public boolean canRecognizeDir)
    {
        if( ! new File(getServerRootDir(), "jboss-modules.jar").exists() )
            return false;
        if( ! new File(getServerRootDir(), "standalone/configuration").exists() )
            return false;
        if( ! new File(getServerRootDir(), "bin/standalone.sh").exists() )
            return false;

        return true;
    }


    @Override
    public String format( VersionRange versionRange ) {
        StringBuilder sb = new StringBuilder("JBoss ");

        // Version unknown
        if( versionRange == null || versionRange.getFrom() == null )
            return sb.append("AS 7 or EAP 6, or WildFly 8").toString();

        // AS or EAP?
        sb.append( versionRange.getFrom().getVerProduct() == null ? "AS " : "EAP ");
        sb.append( versionRange.getFrom_preferProduct() );

        // Range?
        if( versionRange.getTo() == null )
            return sb.toString();

        sb.append(" - ").append( versionRange.getTo_preferProduct() );
        return sb.toString();
    }

}
