package org.jboss.windup.rules.server.ident.recog.api;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.taskdefs.condition.Equals;
import org.jboss.windup.rules.server.ident.recog.as5.HashFile;
import org.jboss.windup.rules.server.ident.recog.as5.HashFileInfo;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public abstract class ServerRecognizerBase implements ServerRecognizer
{
    private ServerRecognizerInfo descriptor;


    public ServerRecognizerBase()
    {
        this.descriptor = getFirstSuperClassAnnotation(this.getClass(), ServerRecognizerInfo.class, ServerRecognizer.class);
        if (this.descriptor == null )
            throw new IllegalArgumentException(
                ServerRecognizerBase.class.getSimpleName() + " subclasses must have the @"
                + ServerRecognizerInfo.class.getSimpleName() + " annotation: " + this.getClass().getName());
    }


    @Override
    public ServerIdentification recognize(File serverRootDir)
    {
        return new ServerIdentification(serverRootDir)
            .setRecognizer(this)
            .setVendor(this.getVendor())
            .setModel(this.getModel())
            .setVersionRange(this.recognizeVersion(serverRootDir));
    }


    public String getVendor()
    {
        return this.descriptor.vendor();
    }

    public String getModel()
    {
        return this.descriptor.model();
    }



    /**
     * @return Hash files as specified in the descriptor annotation.
     */
    protected List<HashFileInfo> getHashFiles()
    {
        ///ServerRecognizerInfo info = this.getClass().getAnnotation(ServerRecognizerInfo.class);
        //ServerRecognizerInfo info = getFirstSuperClassAnnotation(this.getClass(), ServerRecognizerInfo.class, ServerRecognizer.class);
        final List<HashFileInfo>  hashFileInfos = new ArrayList<>(this.descriptor.hashFiles().length);
        for (HashFile hashFile : this.descriptor.hashFiles())
            hashFileInfos.add(new HashFileInfo(hashFile.path(), hashFile.version()));

        return hashFileInfos;
    }


    @Override
    public boolean isCanRecognizeDir( File rootDir )
    {
        for (String mustHave : this.descriptor.mustHave())
            if( ! new File(rootDir, mustHave).exists() )
                return false;

        return true;
    }



    /**
     * Returns the first appearance of given annotation on given class or it's superclasses.
     * @param clazz Class to start At.
     * @param annot Annotation to look for.
     * @param topClass Class to stop at.
     * @return
     */
    public static <T extends Annotation> T getFirstSuperClassAnnotation(Class<?> clazz, Class<T> annot, Class<ServerRecognizer> topClass)
    {
        do {
            T annotation = clazz.getAnnotation(annot);
            if (annotation != null)
                return annotation;
            Class superclass = clazz.getSuperclass();
            if ( clazz == null || ! topClass.isAssignableFrom(superclass))
                return null;
            clazz = superclass;
        }
        while(true);
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof ServerRecognizer))
            return false;
        return obj.getClass().equals(this.getClass());
    }

    public int hashCode(){
        return this.getClass().hashCode();
    }

}
