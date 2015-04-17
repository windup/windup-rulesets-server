package org.jboss.windup.rules.server.api;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public interface VersionComparer
{
    /**
     * Compares two version strings. Returns similarly to JDK's compareTo():
     * Negative: version1 &lt; version2.
     *        0: version1 is equal to version2.
     * Positive: version1 &gt; version2.
     */
    int compareVersions(String version1, String version2);
}
