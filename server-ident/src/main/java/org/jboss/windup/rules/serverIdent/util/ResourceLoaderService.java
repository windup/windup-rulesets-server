package org.jboss.windup.rules.serverIdent.util;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Wraps the loading of resources from a class.
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class ResourceLoaderService
{

    public InputStream load(Path path, Class cls)
    {
        return cls.getClassLoader().getResourceAsStream(path.toString());
    }

    /**
     * Convenience method - loads the resource from the classloader of the calling class.
     */
    public InputStream load(Path path)
    {
        Class cls = Thread.currentThread().getStackTrace()[2].getClass();
        return cls.getClassLoader().getResourceAsStream(path.toString());
    }

}
