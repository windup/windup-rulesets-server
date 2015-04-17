package org.jboss.windup.rules.server.test;

import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.windup.engine.predicates.EnumeratedRuleProviderPredicate;
import org.jboss.windup.exec.WindupProcessor;
import org.jboss.windup.exec.configuration.WindupConfiguration;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.GraphContextFactory;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.rules.apps.java.config.SourceModeOption;
import org.jboss.windup.rules.server.input.ServerDirOption;
import org.jboss.windup.rules.serverIdent.ServerIdentInitRules;
import org.jboss.windup.rules.serverIdent.ServerIdentRules;
import org.jboss.windup.rules.serverIdent.model.ServerDirModel;
import org.jboss.windup.rules.serverIdent.model.ServerIdentModel;
import org.jboss.windup.util.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for the Victims ruleset.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
@RunWith(Arquillian.class)
public class ServerIdentRulesetTest
{
    private static final Logger log = Logging.get(ServerIdentRulesetTest.class);

    @Deployment
    @AddonDependencies({
        @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
        @AddonDependency(name = "org.jboss.windup.utils:windup-utils"),
        //@AddonDependency(name = "org.jboss.windup.rules.apps:windup-rules-java"),
        @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
        @AddonDependency(name = "org.jboss.windup.rules.server:windup-rules-server-ident"),
    })
    public static AddonArchive getDeployment()
    {
        final AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
            .addBeansXML();
        return archive;
    }


    @Inject
    private WindupProcessor processor;

    @Inject
    private GraphContextFactory contextFactory;

    @Test
    public void testFindEap520() throws Exception
    {
        try (GraphContext ctx = contextFactory.create())
        {
            // Windup config.
            WindupConfiguration wc = new WindupConfiguration();
            wc.setGraphContext(ctx);
            wc.setOptionValue(SourceModeOption.NAME, false);
            wc.setOptionValue(ServerDirOption.NAME, ServerIdentLibTest.EAP_520_PATH);
            // Only run this ruleset's rules and those it needs.
            wc.setRuleProviderFilter(new EnumeratedRuleProviderPredicate(
                    ServerIdentInitRules.class, ServerIdentRules.class));
            wc.setInputPath(Paths.get("src/test/resources/"));
            wc.setOutputDirectory(Paths.get("target/WindupReport"));

            processor.execute(wc);

            // Check the results. The server should be identified as EAP 5.2.0.
            GraphService<ServerDirModel> serverDirGS = new GraphService(ctx, ServerDirModel.class);

            boolean found = false;
            for (ServerDirModel serverDirM : serverDirGS.findAll())
            {
                final ServerIdentModel identM = serverDirM.getIdentification();
                if (null == identM)
                    Assert.fail("Server was not identified: " + serverDirM.getFilePath());

                log.info(serverDirM.getFilePath() + " identified as " + identM);
                Assert.assertEquals("Identified as EAP 5.2.0: " + identM, "EAP", identM.getModel());
                Assert.assertEquals("Identified as EAP 5.2.0: " + identM, "5.2.0.GA (5.2.0)", identM.getVersion());
                Assert.assertEquals("Identified as EAP 5.2.0: " + identM, "JBoss", identM.getVendor());
                found = true;
            }
            Assert.assertTrue("A server was found.", found);
        }
    }
}
