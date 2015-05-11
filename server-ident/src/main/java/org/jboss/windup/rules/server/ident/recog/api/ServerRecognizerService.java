package org.jboss.windup.rules.server.ident.recog.api;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jboss.windup.rules.server.ident.recog.as5.JBossAS5ServerRecognizer;
import org.jboss.windup.rules.server.ident.recog.as7.JBossAS7ServerRecognizer;

/**
 * Class responsible for gathering all available recognizers, and trying them on given directory.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class ServerRecognizerService {
    public static final Logger log = Logger.getLogger(ServerRecognizerService.class.getName());

    @Inject private Instance<ServerRecognizerFactory> recognizerInstances;

    private Set<ServerRecognizerFactory> recognizers = new HashSet<>();


    private void transferRecognizers()
    {
        if (this.recognizerInstances != null)
            for (ServerRecognizerFactory recognizerInstance : recognizerInstances)
                this.recognizers.add(recognizerInstance);
    }


    /**
     *  Ask all known implementations of ServerRecognizer whether their server is in the directory.
     */
    public ServerRecognizer selectAppropriateRecognizer( File serverRootDir ) throws Exception
    {
        transferRecognizers();

        for(ServerRecognizerFactory recognizerFactory : recognizers)
        {
            log.fine("    Trying " + recognizerFactory.getClass().getSimpleName());
            ServerRecognizer serverRecognizer = recognizerFactory.withServerRoot(serverRootDir);
            if(serverRecognizer.canRecognizeDir() )
                return serverRecognizer;
        }
        return null;
    }


    /**
     *  All-in-one: Queries all recognizers which one can recognize given directory;
     *  and if some is found, it is asked for the version.
     */
    public ServerIdentification recognize( File serverRootDir ) throws Exception{
        ServerRecognizer recognizer = selectAppropriateRecognizer( serverRootDir );
        if( recognizer == null )
        {
            log.warning("No recognizer found for this server.");
            return null;
        }
        return recognizer.recognize();
    }


    /**
     *  Finds classes implementing ServerRecognizer.
     *  Currently static.
     */
    private static Collection<Class<? extends ServerRecognizer>> findServerRecognizerClasses() {
        return (List) Arrays.asList(
                JBossAS5ServerRecognizer.class,
                JBossAS7ServerRecognizer.class
        );
    }

    public ServerRecognizerService addRecognizer(ServerRecognizerFactory recognizer)
    {
        this.recognizers.add(recognizer);
        return this;
    }

}// class
