package org.jboss.windup.rules.server.ident.recog.api;

import java.io.File;

/**
 * Building one concrete instance of the ServerRecognizer
 *
 * Created by mbriskar on 5/11/15.
 */
public interface ServerRecognizerFactory {
    ServerRecognizer withServerRoot(File serverRoot);
}
