package org.jboss.windup.rules.server.ident.recog;

/**
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public interface IProjectAndProductVersionBidiMapper {
    
    public String getProjectToProductVersion( String str );
    
    public String getProductToProjectVersion( String str );
    
}
