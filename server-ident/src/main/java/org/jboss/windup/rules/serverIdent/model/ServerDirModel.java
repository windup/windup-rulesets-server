package org.jboss.windup.rules.serverIdent.model;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@TypeValue(ServerDirModel.TYPE)
public interface ServerDirModel extends UserInputPathModel
{
    static final String TYPE = "w:serverDir";
    static final String DIR = "serverDir:Path";


    //@Property(DIR)
    //public String getDir();

    //@Property(DIR)
    //public ServerDirModel setDir(String cve);

    @Adjacency(direction = Direction.IN, label = ServerIdentModel.IDENTIFIES)
    public ServerIdentModel getIdentification();

    @Adjacency(direction = Direction.IN, label = ServerIdentModel.IDENTIFIES)
    public ServerDirModel setIdentification(ServerIdentModel identification);

    // Further characteristics, like vendor and version identification, is up to other rules and their models.

}// class
