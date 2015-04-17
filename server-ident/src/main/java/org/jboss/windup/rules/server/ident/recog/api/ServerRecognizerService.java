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

    @Inject private Instance<ServerRecognizer> recognizerInstances;

    private Set<ServerRecognizer> recognizers = new HashSet<>();


    private void transferRecognizers()
    {
        if (this.recognizerInstances != null)
            for (ServerRecognizer recognizerInstance : recognizerInstances)
                this.recognizers.add(recognizerInstance);
    }


    /**
     *  Ask all known implementations of ServerRecognizer whether their server is in the directory.
     */
    public /*static*/ ServerRecognizer selectAppropriateRecognizer( File serverRootDir ) throws Exception
    {
        /* ///
        for( Class<? extends ServerRecognizer> recognizerClass : findServerRecognizerClasses() ){
            log.fine("    Trying " + recognizerClass.getSimpleName());
            ServerRecognizer recognizer = instantiate(recognizerClass);
            if( recognizer.isCanRecognizeDir(serverRootDir) )
                return recognizer;
        }
        /**/

        transferRecognizers();

        for(ServerRecognizer recognizer : recognizers)
        {
            log.fine("    Trying " + recognizer.getClass().getSimpleName());
            System.out.println("Trying " + recognizer.getClass().getSimpleName());///
            if( recognizer.isCanRecognizeDir(serverRootDir) )
                return recognizer;
        }
        return null;
    }

    /**
     *  Asks given ServerRecognizer what version is in the given directory.
     *  TODO: Make method of ServerRecognizer?
     *  @deprecated  Use IServerType.recognizeVersion();
     */
    private VersionRange recognizeVersion( Class<? extends ServerRecognizer> typeClass, File serverRootDir ) throws Exception
    {
        ServerRecognizer type = instantiate( typeClass );
        return type.recognizeVersion( serverRootDir );
        // TODO: Could be called statically?
    }

    /**
     *  All-in-one: Queries all recognizers which one can recognize given directory;
     *  and if some is found, it is asked for the version.
     */
    public ServerIdentification recognize( File serverRootDir ) throws Exception{
        ServerRecognizer recognizer = selectAppropriateRecognizer( serverRootDir );
        if( recognizer == null )
        {
            System.out.println("No recognizer found for this server.");///
            return null;
        }
        /*return new ServerIdentification(serverRootDir) ///
                .setRecognizer(recognizer)
                .setVersionRange(recognizer.recognizeVersion(serverRootDir));
        /**/
        return recognizer.recognize(serverRootDir);
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


    /**
     *  Just wraps the potential exception.
     */
    private static ServerRecognizer instantiate( Class<? extends ServerRecognizer> typeClass ) throws Exception {
        try {
            return typeClass.newInstance();
        } catch( InstantiationException | IllegalAccessException ex ) {
            throw new Exception("Failed instantiating ServerType "+typeClass.getSimpleName()+": " + ex.getMessage(), ex);
        }
    }



    public ServerRecognizerService addRecognizer(ServerRecognizer recognizer)
    {
        this.recognizers.add(recognizer);
        return this;
    }

}// class
