package org.jboss.windup.rules.serverIdent;

import java.util.Map;
import java.util.logging.Logger;
import javax.inject.Inject;

import org.jboss.windup.config.AbstractRuleProvider;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.GraphOperation;
import org.jboss.windup.config.phase.InitializationPhase;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.rules.server.ident.recog.api.ServerRecognizerService;
import org.jboss.windup.rules.server.input.ServerDirOption;
import org.jboss.windup.rules.serverIdent.model.ServerDirModel;
import org.jboss.windup.util.Logging;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;


/**
 * Server identification initialization rules.
 *
 * @author <a href="mailto:ozizka@redhat.com">Ondrej Zizka</a>
 */
@RuleMetadata(tags = { "java", "server" }, phase = InitializationPhase.class, before = ServerIdentRules.class)
public class ServerIdentInitRules extends AbstractRuleProvider
{
    private static final Logger log = Logging.get(ServerIdentInitRules.class);

    @Inject ServerRecognizerService recognizer;


    // @formatter:off
    @Override
    public Configuration getConfiguration(final GraphContext grCtx)
    {
        // TODO: grCtx.getService(ServerDirModel.class)
        final GraphService<ServerDirModel> serverDirGS = new GraphService(grCtx, ServerDirModel.class);

        return ConfigurationBuilder.begin()
        .addRule()
        .perform(
            new GraphOperation()
            {
                @Override
                public void perform(GraphRewrite event, EvaluationContext context)
                {
                    Map<String, Object> config = grCtx.getOptionMap();
                    String serverDir = (String) config.get(ServerDirOption.NAME);
                    serverDirGS.create().setFilePath(serverDir);
                    log.finer("Added server dir: " + serverDir);
                }
            }
        ).withId("ServerIdentInit");
    }
    // @formatter:on
}
