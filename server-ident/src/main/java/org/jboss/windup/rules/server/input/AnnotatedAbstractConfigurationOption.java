package org.jboss.windup.rules.server.input;

import org.jboss.windup.config.AbstractConfigurationOption;
import org.jboss.windup.config.InputType;


/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public abstract class AnnotatedAbstractConfigurationOption extends AbstractConfigurationOption
{
    private Option optionAnnotation;


    public AnnotatedAbstractConfigurationOption()
    {
        this.optionAnnotation = this.getClass().getAnnotation(Option.class);
        if (null == optionAnnotation)
            throw new IllegalStateException(AnnotatedAbstractConfigurationOption.class.getSimpleName()
                    + " must have @Option annotation.");
    }

    @Override
    public String getName()
    {
        return this.optionAnnotation.name();
    }


    @Override
    public String getLabel()
    {
        return this.optionAnnotation.label();
    }


    @Override
    public String getDescription()
    {
        return this.optionAnnotation.description();
    }


    @Override
    public Class<?> getType()
    {
        return this.optionAnnotation.type();
    }


    @Override
    public InputType getUIType()
    {
        return this.optionAnnotation.uiType();
    }


    @Override
    public boolean isRequired()
    {
        return this.optionAnnotation.required();
    }

}
