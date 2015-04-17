package org.jboss.windup.rules.server.ident.recog.api;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.jboss.windup.rules.server.ident.recog.IProjectAndProductVersionBidiMapper;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@XmlRootElement
public class VersionRange
{

    @XmlAttribute
    private final Version from;

    @XmlAttribute
    private final Version to;


    public VersionRange() {
        this.from = null;
        this.to = null;
    }

    public VersionRange( String from, String to )
    {
        this( new Version( from ), new Version( to ) );
    }

    public VersionRange( Version from, Version to )
    {
        this.from = from;
        this.to = to;
    }

    @Deprecated
    public String getFrom_preferProduct()
    {
        return this.from.getVerProduct() != null ? this.from.getVerProduct() : this.from.getVerProject();
    }

    public String getTo_preferProduct()
    {
        return this.to.getVerProduct() != null ? this.to.getVerProduct() : this.to.getVerProject();
    }

    public static VersionRange forProduct( String from, String to, IProjectAndProductVersionBidiMapper mapper )
    {
        return new VersionRange(
            new Version( mapper.getProductToProjectVersion( from ), from ),
            new Version( mapper.getProductToProjectVersion( to ),   to )
        );
    }


    public boolean isExactVersion() {
        return to == null || from.equals( to );
    }


    public Version getFrom()
    {
        return from;
    }


    public Version getTo()
    {
        return to;
    }

}
