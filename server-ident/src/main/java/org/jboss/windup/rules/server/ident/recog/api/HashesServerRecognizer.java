package org.jboss.windup.rules.server.ident.recog.api;


import java.util.List;
import org.jboss.windup.rules.server.ident.recog.HasHashes;
import org.jboss.windup.rules.server.ident.recog.as5.HashFileInfo;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public abstract class HashesServerRecognizer extends ServerRecognizerBase implements HasHashes
{

    protected abstract List<HashFileInfo> getHashFiles();


}
