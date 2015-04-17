package org.jboss.windup.rules.server.test;

import java.io.File;
import java.util.logging.Logger;
import org.jboss.windup.rules.server.ident.recog.api.ServerIdentification;
import org.jboss.windup.rules.server.ident.recog.api.ServerRecognizerService;
import org.jboss.windup.rules.server.ident.recog.as5.JBossAS5ServerRecognizer;
import org.jboss.windup.util.Logging;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the library itself.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class ServerIdentLibTest
{
    private static final Logger log = Logging.get(ServerIdentLibTest.class);

    public static final String EAP_520_PATH = "testdata/as5configs/02_EAP-520";


    @Test
    public void testIdentifyServer() throws Exception
    {
        try {
            ServerRecognizerService recognizers = new ServerRecognizerService();
            recognizers.addRecognizer(new JBossAS5ServerRecognizer());

            ServerIdentification info = recognizers.recognize(new File(EAP_520_PATH));
            Assert.assertEquals("Identified as EAP 5.2.0: " + info, "EAP", info.getModel());
            Assert.assertEquals("Identified as EAP 5.2.0: " + info, "5.2.0.GA (5.2.0)", info.getVersionRange().getFrom().toString());
            Assert.assertEquals("Identified as EAP 5.2.0: " + info, "JBoss", info.getVendor());
        }
        catch (Exception ex){
            throw ex; // To catch InvocationTargetException easily.
        }
    }
}
