package org.jboss.windup.rules.server.ident.recog.as5;


public class HashFileInfo
{
    private String path;
    private String version;


    public HashFileInfo(String fName, String version)
    {
        this.path = fName;
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
