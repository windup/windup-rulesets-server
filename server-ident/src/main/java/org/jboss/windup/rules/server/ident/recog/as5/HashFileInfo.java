package org.jboss.windup.rules.server.ident.recog.as5;


public class HashFileInfo
{
    private String path;
    private String version;


    public HashFileInfo(String path, String version)
    {
        this.path = path;
        this.version = version;
    }


    public String getPath()
    {
        return path;
    }

    public String getVersion()
    {
        return version;
    }
}
