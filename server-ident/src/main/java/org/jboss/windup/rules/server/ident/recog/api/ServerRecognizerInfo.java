package org.jboss.windup.rules.server.ident.recog.api;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.jboss.windup.rules.server.ident.recog.as5.HashFile;


/**
 * This makes the ServerRecognizerInfo classes shorter and more readable.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
@Retention(RUNTIME)
@Target( { METHOD, FIELD, PARAMETER, TYPE })
public @interface ServerRecognizerInfo
{
    String description();
    String vendor();
    String model();
    String[] mustHave() default {};
    String versionUniqueFile() default "";
    HashFile[] hashFiles() default {};
}
