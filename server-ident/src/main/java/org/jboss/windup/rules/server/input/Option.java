package org.jboss.windup.rules.server.input;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.jboss.windup.config.InputType;


/**
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface Option
{
    String label();
    String description();
    String name();
    Class type();
    public InputType uiType();
    boolean required() default false;
    //String defaultValue() default "";
}
