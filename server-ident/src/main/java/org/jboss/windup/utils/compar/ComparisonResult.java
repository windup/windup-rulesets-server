package org.jboss.windup.utils.compar;


import java.io.File;
import java.nio.file.Path;
import java.util.Map;

/**
 *  Holds the result of comparison of a list of hashes against a directory.
 *
 *  This is used to compare the existing server directory against known releases.
 *
 *  @see FileHashCoparer
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class ComparisonResult {

    public final File dir;
    public File hashesFile;

    //@XmlElementWrapper(name = "matches")
    //@XmlJavaTypeAdapter(ComparisonResultsMapAdapter.class)
    Map<Path, FileHashComparer.MatchResult> matches;

    // Counts
    private int countMatches;
    private int countMismatches;
    private int countMisses;
    private int countEmpty;
    private boolean recount = true;


    // Const

    ComparisonResult(){ dir = null; } // For JAXB.

    public ComparisonResult( File dir ) {
        this.dir = dir;
    }

    public Map<Path, FileHashComparer.MatchResult> getMatches() { return matches; }
    public ComparisonResult setMatches( Map<Path, FileHashComparer.MatchResult> matches ) {
        this.matches = matches; recount = true; return this;
    }

    public File getHashesFile() { return hashesFile; }
    public ComparisonResult setHashes( File hashes ) {
        this.hashesFile = hashes; return this;
    }



    public int getCountTotal() {
        return this.getCountMatches() + this.getCountMismatches() + this.getCountMisses();
    }

    public int getCountMatches() {
        this.doCountIfNeeded();
        return countMatches;
    }

    public int getCountMismatches() {
        this.doCountIfNeeded();
        return countMismatches;
    }

    public int getCountMisses() {
        this.doCountIfNeeded();
        return countMisses;
    }

    private void doCountIfNeeded() {
        if( ! this.recount ) return;
        if( this.matches == null )
            throw new IllegalStateException("Nothing to count - matches were not set yet.");

        this.countMatches = this.countMismatches = this.countMisses = 0;

        for( FileHashComparer.MatchResult res : this.matches.values()) {
            switch( res ) {
                case MATCH:    this.countMatches++; break;
                case MISMATCH: this.countMismatches++; break;
                case MISSING:  this.countMisses++; break;
                case EMPTY:    this.countEmpty++; break;
            }
        }
    }

    public String toString() {
        return new StringBuilder("Hash file ")
                .append(this.hashesFile)
                .append(" against dir ").append(this.dir).append(": ")
                .append(this.formatStats())
                .toString();
    }


    public String formatStats() {
        StringBuilder sb = new StringBuilder();
        if( this.matches == null )
            return sb.append("Matches were not set yet.").toString();

        this.doCountIfNeeded();
        return sb
            .append(this.countMatches).append(" match, ")
            .append(this.countMismatches).append(" mism, ")
            .append(this.countMisses).append(" miss, ")
            .append(this.countEmpty).append(" empty")
            .toString();
    }

}
