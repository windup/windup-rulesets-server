package org.jboss.windup.rules.server.ident.recog.api;

import java.io.File;
import java.util.logging.Logger;
import org.jboss.windup.utils.compar.ComparisonResult;
import org.jboss.windup.rules.server.ident.recog.HasHashes;

/**
 *  What did we recognize about the server; Vendor, model, version range.
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class ServerIdentification
{
    private static final Logger log = Logger.getLogger(ServerIdentification.class.getName() );

    /**
     * Where was it found.
     */
    private final File serverRootDir;

    /**
     * Which recognizer recognized it.
     */
    private ServerRecognizer recognizer;


    /**
     * Model of the server - e.g. EAP, AS, WildFly.
     */
    private String vendor;

    /**
     * Model of the server - e.g. EAP, AS, WildFly.
     */
    private String model;

    /**
     * Version range that this server can fit in.
     */
    private VersionRange versionRange;


    /**
     * Result of comparison of files, if performed.
     */
    private ComparisonResult comparisonResult;



    public ServerIdentification( File serverRootDir ) {
        this.serverRootDir = serverRootDir;
    }


    /**
     *  Formats a string describing this server.
     */
    public String toStringFormatted(){
        return recognizer.format( versionRange ) + " in " + this.serverRootDir;
    }


    public void compareHashes() throws Exception {
        if( ! ( this.recognizer instanceof HasHashes ) )
            throw new Exception("Comparison of file hashes is not supported for server type '" + this.recognizer.getDescription() + "'.");

        if( ! versionRange.isExactVersion() )
            log.warning("Comparing hashes without knowing exact server version. May produce a lot of mismatches.");

        this.comparisonResult = ((HasHashes)this.recognizer).compareHashes( versionRange.getFrom(), serverRootDir );
    }




    /**
     * Where was it found.
     */
    public File getServerRootDir()
    {
        return serverRootDir;
    }


    /**
     * Organization which made this server. E.g. JBoss.
     */
    public String getVendor()
    {
        return vendor;
    }

    public ServerIdentification setVendor(String vendor)
    {
        this.vendor = vendor;
        return this;
    }



    /**
     * Model of the server - e.g. EAP, AS, WildFly.
     */
    public String getModel()
    {
        return model;
    }

    public ServerIdentification setModel(String model)
    {
        this.model = model;
        return this;
    }


    /**
     * Result of comparison of files, if performed.
     */
    public ComparisonResult getComparisonResult()
    {
        return comparisonResult;
    }

    public void setComparisonResult(ComparisonResult comparisonResult)
    {
        this.comparisonResult = comparisonResult;
    }


    /**
     * Which recognizer recognized it.
     */
    public ServerRecognizer getRecognizer()
    {
        return recognizer;
    }
    public ServerIdentification setRecognizer( ServerRecognizer type ) {
        this.recognizer = type; return this;
    }

    /**
     * Version range that this server can fit in.
     */
    public VersionRange getVersionRange() { return versionRange; }
    public ServerIdentification setVersionRange( VersionRange versionRange ) {
        this.versionRange = versionRange; return this;
    }

}
