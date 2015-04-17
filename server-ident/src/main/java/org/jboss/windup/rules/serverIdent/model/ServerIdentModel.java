package org.jboss.windup.rules.serverIdent.model;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;
import org.jboss.windup.graph.model.WindupVertexFrame;

/**
 * Server identification - Vendor, model, version [range].
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
@TypeValue(ServerIdentModel.TYPE)
public interface ServerIdentModel extends WindupVertexFrame
{
    static final String TYPE = "w:serverIdent";
    static final String VENDOR = "serverIdent:vendor";
    static final String MODEL = "serverIdent:model";
    static final String VERSION = "serverIdent:version";
    static final String VERSION_TOP = "serverIdent:versionTop";
    static final String IDENTIFIES = "serverIdent:identifies";


    @Property(VENDOR)
    public String getVendor();

    @Property(VENDOR)
    public ServerIdentModel setVendor(String vendor);

    @Property(MODEL)
    public String getModel();

    @Property(MODEL)
    public ServerIdentModel setModel(String model);

    @Property(VERSION)
    public String getVersion();

    @Property(VERSION)
    public ServerIdentModel setVersion(String version);

    @Property(VERSION_TOP)
    public String getVersionTop();

    @Property(VERSION_TOP)
    public ServerIdentModel setVersionTop(String version);

    @Adjacency(label = IDENTIFIES, direction = Direction.OUT)
    public ServerIdentModel setIdentifies(ServerDirModel serverDirM);

    @Adjacency(label = IDENTIFIES, direction = Direction.OUT)
    public ServerDirModel getIdentifies();

}
