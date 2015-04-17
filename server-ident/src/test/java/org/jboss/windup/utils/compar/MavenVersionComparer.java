package org.jboss.windup.utils.compar;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jboss.windup.rules.server.api.VersionComparer;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class MavenVersionComparer implements VersionComparer
{
    @Override
    public int compareVersions(String version1, String version2)
    {
        DefaultArtifactVersion v1 = new DefaultArtifactVersion(version1);
        DefaultArtifactVersion v2 = new DefaultArtifactVersion(version2);
        return v1.compareTo(v2);
    }
}
