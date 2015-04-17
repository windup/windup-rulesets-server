package org.jboss.windup.rules.server.ident.recog.api;

import java.util.Objects;
import org.jboss.windup.rules.server.ident.recog.IProjectAndProductVersionBidiMapper;
import org.jboss.windup.rules.server.ident.recog.JBossEAP5VersionComparer;

/**
 * Some versions represent products.
 * Products have own versioning scheme, but are mappable to project versions.
 *
 * TODO: Get rid of this project/product schism within Version - use 2 Version instances instead.
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class Version
{
    private String verProject;
    private String verProduct;

    public Version( String version ) {
        this.verProject = version;
    }

    public Version( String projectVer, String productVer ) {
        this.verProject = projectVer;
        this.verProduct = productVer;
    }

    /**
     *  Auto-fills the product version by looking it up through given mapper.
     */
    public Version( String version, IProjectAndProductVersionBidiMapper mapper ) {
        this.verProject = version;
        this.verProduct = mapper.getProjectToProductVersion( version );
    }

    /**
     * Compares using project version.
     * // FIXME! Needs to go out of Version class to allow various comparison strategies.
     */
    public int compare( Version other )
    {
        return new JBossEAP5VersionComparer().compareVersions( this.verProject, other.verProject );
    }


    @Override
    public String toString()
    {
        if( verProduct == null )  return verProject;
        return verProject + " (" + verProduct + ')';
    }


    public Object toString_preferProduct()
    {
        return this.verProduct != null ? this.verProduct : this.verProject;
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode( this.verProject );
        hash = 61 * hash + Objects.hashCode( this.verProduct );
        return hash;
    }


    @Override
    public boolean equals( Object obj ) {
        if( obj == null )  return false;
        if( getClass() != obj.getClass() )  return false;

        final Version other = (Version) obj;
        if( ! Objects.equals( this.verProject, other.verProject ) )  return false;
        if( ! Objects.equals( this.verProduct, other.verProduct ) )  return false;

        return true;
    }


    private Version() { } // JAXB


    public String getVerProject()
    {
        return verProject;
    }


    public void setVerProject(String verProject)
    {
        this.verProject = verProject;
    }


    public String getVerProduct()
    {
        return verProduct;
    }


    public void setVerProduct(String verProduct)
    {
        this.verProduct = verProduct;
    }

}
