package org.jboss.windup.rules.server.ident.recog.as5;

import java.nio.file.Path;
import java.util.Map;
import org.jboss.windup.utils.compar.FileHashComparer;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class HashFileMatch
{
    public HashFileInfo hashFile;
    public Map<Path, FileHashComparer.MatchResult> matches;
}
