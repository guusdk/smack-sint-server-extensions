/*
 * Copyright 2025 Guus der Kinderen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.igniterealtime.smack.inttest.xep0092;

import org.igniterealtime.smack.inttest.AbstractSmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTestEnvironment;
import org.igniterealtime.smack.inttest.TestNotPossibleException;
import org.igniterealtime.smack.inttest.annotations.AfterClass;
import org.igniterealtime.smack.inttest.annotations.BeforeClass;
import org.igniterealtime.smack.inttest.annotations.SmackIntegrationTest;
import org.igniterealtime.smack.inttest.annotations.SpecificationReference;
import org.igniterealtime.smack.inttest.xep0092.provider.Version;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IqProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jxmpp.jid.Jid;

import static org.junit.jupiter.api.Assertions.*;

@SpecificationReference(document = "XEP-0092", version = "1.1")
public class VersionServerIntegrationTest extends AbstractSmackIntegrationTest {

    public VersionServerIntegrationTest(SmackIntegrationTestEnvironment environment) throws TestNotPossibleException, XMPPErrorException, NotConnectedException, NoResponseException, InterruptedException
    {
        super(environment);

        if (!ServiceDiscoveryManager.getInstanceFor(conOne).serverSupportsFeature(Version.NAMESPACE)) {
            throw new TestNotPossibleException("Server does not advertise support for " + Version.NAMESPACE);
        }
    }

    private IqProvider smackProvider = null;

    @BeforeClass
    public void setup() {
        // Swap out Smack's Provider with one of our own, that's easier to test against.
        smackProvider = ProviderManager.getIQProvider(Version.ELEMENT_NAME, Version.NAMESPACE);
        if (smackProvider != null) {
            ProviderManager.removeIQProvider(Version.ELEMENT_NAME, Version.NAMESPACE);
        }
        ProviderManager.addIQProvider(Version.ELEMENT_NAME, Version.NAMESPACE, new Version.Provider());
    }

    @AfterClass
    public void teardown()
    {
        ProviderManager.removeIQProvider(Version.ELEMENT_NAME, Version.NAMESPACE);
        // Restore Smack's Version handler.
        if (smackProvider != null) {
            ProviderManager.addIQProvider(Version.ELEMENT_NAME, Version.NAMESPACE, smackProvider);
        }
    }

    @SmackIntegrationTest(section = "2", quote = "The following children of the <query/> are allowed in an IQ result: [...] <name/> -- The natural-language name of the software. This element is REQUIRED in a result.")
    public void testResultContainsName() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException
    {
        // Setup test fixture.
        final Jid target = conOne.getXMPPServiceDomain();
        final IQ request = new Version();
        request.setType(IQ.Type.get);
        request.setTo(target);

        // Execute System under Test.
        final Version result;
        try {
            result = conOne.sendIqRequestAndWaitForResponse(request);
        } catch (XMPPErrorException e) {
            fail("Expected '" + conOne.getUser() + "' to receive a non-error response after querying '" + target + "' for its version. Received: " + e.getStanzaError(), e);
            return;
        }
        // Verify result.
        assertNotNull(result.getName(), "Expected the 'version' response from '" + target + "' received by '" + conOne.getUser() + "' to contain a name (but it did not)");
    }

    @SmackIntegrationTest(section = "2", quote = "The following children of the <query/> are allowed in an IQ result: [...] <version/> -- The specific version of the software. This element is REQUIRED in a result.")
    public void testResultContainsVersion() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException
    {
        // Setup test fixture.
        final Jid target = conOne.getXMPPServiceDomain();
        final IQ request = new Version();
        request.setType(IQ.Type.get);
        request.setTo(target);

        // Execute System under Test.
        final Version result;
        try {
            result = conOne.sendIqRequestAndWaitForResponse(request);
        } catch (XMPPErrorException e) {
            fail("Expected '" + conOne.getUser() + "' to receive a non-error response after querying '" + target + "' for its version. Received: " + e.getStanzaError(), e);
            return;
        }
        // Verify result.
        assertNotNull(result.getVersion(), "Expected the 'version' response from '" + target + "' received by '" + conOne.getUser() + "' to contain a version (but it did not)");
    }
}
