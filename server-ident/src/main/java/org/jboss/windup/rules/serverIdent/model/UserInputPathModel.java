package org.jboss.windup.rules.serverIdent.model;

import com.tinkerpop.frames.modules.typedgraph.TypeValue;
import org.jboss.windup.graph.model.resource.FileModel;

/**
 * Marks a directory or file that the user passed as input.
 * It is later used to perform certain operations, like, unzipping archives, creating hashes, recognizing file types, etc.
 *
 * @author <a href="mailto:jesse.sightler@gmail.com">Jesse Sightler</a>
 */
@TypeValue(UserInputPathModel.TYPE)
public interface UserInputPathModel extends FileModel
{
    public static final String TYPE = "UserInputPath";
}
