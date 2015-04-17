package org.jboss.windup.rules.server.ident.recog.as5;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@Retention(RUNTIME)
@Target( { METHOD, FIELD, PARAMETER, TYPE })
public @interface HashFile
{
    String path();
    String version();
}
