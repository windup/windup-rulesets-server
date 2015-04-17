package org.jboss.windup.rules.serverIdent;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import org.jboss.windup.config.AbstractRuleProvider;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.Iteration;
import org.jboss.windup.config.operation.iteration.AbstractIterationOperation;
import org.jboss.windup.config.phase.MigrationRulesPhase;
import org.jboss.windup.config.query.Query;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.rules.server.ident.recog.api.ServerIdentification;
import org.jboss.windup.rules.server.ident.recog.api.ServerRecognizerService;
import org.jboss.windup.rules.serverIdent.model.ServerDirModel;
import org.jboss.windup.rules.serverIdent.model.ServerIdentModel;
import org.jboss.windup.util.Logging;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;


/**
 * Server identification rules.
 *
 * @author <a href="mailto:ozizka@redhat.com">Ondrej Zizka</a>
 */
@RuleMetadata(tags = { "java", "server" }, phase = MigrationRulesPhase.class)
public class ServerIdentRules extends AbstractRuleProvider
{
    private static final Logger log = Logging.get(ServerIdentRules.class);

    @Inject ServerRecognizerService recognizer;


    // @formatter:off
    @Override
    public Configuration getConfiguration(final GraphContext grCtx)
    {
        GraphService<ServerDirModel> serverDirGS = new GraphService(grCtx, ServerDirModel.class);
        final GraphService<ServerIdentModel> serverIdentGS = new GraphService(grCtx, ServerIdentModel.class);


        return ConfigurationBuilder.begin()

        // Check the server directories.
        .addRule()
        .when(Query.fromType(ServerDirModel.class))
        .perform(Iteration.over(ServerDirModel.class) // TODO: Use IteratingRuleProvider?
            .perform(new AbstractIterationOperation<ServerDirModel>() {
                    @Override
                    public void perform(GraphRewrite event, EvaluationContext context, ServerDirModel serverDirM) {
                        log.info("Recognizing server: " + serverDirM.getFilePath());

                        try {
                            ServerIdentification info = recognizer.recognize(new File(serverDirM.getFilePath()));
                            if (info == null)
                            {
                                log.info("No server recognized.");
                                return;
                            }

                            log.info("\tServer in " + serverDirM.getFilePath() + " identified as: " + info.toStringFormatted());
                            ServerIdentModel identM = serverIdentGS.create()
                                // E.g. JBoss
                                .setVendor(info.getVendor())
                                // E.g. EAP
                                .setModel(info.getModel())
                                // E.g. 5.1.3.CP03
                                .setVersion(info.getVersionRange().getFrom().toString());
                            identM.setIdentifies(serverDirM);
                        }
                        catch (Exception ex) {
                            log.log(Level.SEVERE, "Error when recognizing " + serverDirM.getFilePath() + ": " + ex.getMessage(), ex);
                        }
                        serverIdentGS.commit();
                    }


                    @Override
                    public String toString() {
                        return "Server directory identification";
                    }
                }
            ).endIteration()
        ).withId("ServerIdent");
    }
    // @formatter:on
}
