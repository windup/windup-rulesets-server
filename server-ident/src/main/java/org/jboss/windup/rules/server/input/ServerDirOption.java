package org.jboss.windup.rules.server.input;

import java.nio.file.Path;
import org.jboss.windup.config.InputType;
import org.jboss.windup.config.ValidationResult;

/**
 * Indicates that all operations should function in "Offline" mode (without accessing the internet).
 *
 * @author Ondrej Zizka
 */
@Option(name = ServerDirOption.NAME, type = Path.class, label = "Server directory", description = "Path to a server on local filesystem.",
        uiType = InputType.SINGLE)
public class ServerDirOption extends AnnotatedAbstractConfigurationOption
{
    public static final String NAME = "serverDir";

    @Override
    public String getLabel()
    {
        return "? Server directory";
    }

    @Override
    public String getDescription()
    {
        return "? Path to a server on local filesystem.";
    }

    @Override
    public String getName()
    {
        return NAME;
    }


    @Override
    public Class<?> getType()
    {
        return Boolean.class;
    }

    @Override
    public InputType getUIType()
    {
        return InputType.SINGLE;
    }

    @Override
    public boolean isRequired()
    {
        return false;
    }

    @Override
    public ValidationResult validate(Object valueObj)
    {
        return ValidationResult.SUCCESS;
    }


    @Override
    public Object getDefaultValue()
    {
        return true;
    }
}
