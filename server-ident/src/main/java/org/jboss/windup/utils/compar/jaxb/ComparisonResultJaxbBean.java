package org.jboss.windup.utils.compar.jaxb;


import org.jboss.windup.utils.compar.*;
import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.oxm.annotations.XmlReadOnly;

/**
 *  JAXB wrapper for ComparisonResult.
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@XmlRootElement(name="comparisonResult")
@XmlAccessorType( XmlAccessType.NONE )
public class ComparisonResultJaxbBean extends ComparisonResult {

    ComparisonResultJaxbBean(){ super(null); } // For JAXB.

    public ComparisonResultJaxbBean( File dir ) {
        super(dir);
    }

    @XmlAttribute(name = "hashesFile")
    @Override
    public File getHashesFile() { return super.getHashesFile(); }


    @XmlAttribute(name="serverDir")  @XmlReadOnly
    private String getServerDir(){ return dir.getPath(); }

    @XmlAttribute(name="hashesFile") @XmlReadOnly
    private String getHashesPath(){ return hashesFile == null ? "" : hashesFile.getPath(); }

    @XmlElementWrapper(name = "matches")
    @XmlElement(name="match")
    private List<Match> getMatchesAsList(){
        if( this.getMatches() == null )
            return null;

        final LinkedList<Match> matchesList = new LinkedList();
        for( Map.Entry<Path, FileHashComparer.MatchResult> entry : this.getMatches().entrySet() ) {
            matchesList.add( new Match( entry.getKey().toString(),  entry.getValue().name() ) );
        }
        return matchesList;
    }
    @XmlType(propOrder = {"result", "path"})
    public static class Match {
        @XmlAttribute String path;
        @XmlAttribute String result;
        public Match() { }
        public Match( String path, String match ) {
            this.path = path;
            this.result = match;
        }
    }

}// class
