/**
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
package org.igniterealtime.smack.inttest.xep0199;

import org.igniterealtime.smack.inttest.AbstractSmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTestEnvironment;
import org.igniterealtime.smack.inttest.TestNotPossibleException;
import org.igniterealtime.smack.inttest.annotations.SmackIntegrationTest;
import org.igniterealtime.smack.inttest.annotations.SpecificationReference;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.jxmpp.jid.Jid;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration tests for the XEP-0199: XMPP Ping
 *
 * Note that this is a different implementation from {@link org.jivesoftware.smackx.ping.PingIntegrationTest}, which
 * primarily tests Smack's behavior when getting ping requests. The tests in this class instead verify server behavior.
 *
 * @see <a href="https://xmpp.org/extensions/xep-0199.html">XEP-0199: XMPP Ping</a>
 */
@SpecificationReference(document = "XEP-0199", version = "2.0.1")
public class PingServerIntegrationTest extends AbstractSmackIntegrationTest
{
    public PingServerIntegrationTest(SmackIntegrationTestEnvironment environment)
    {
        super(environment);
    }

    @SmackIntegrationTest(section = "4.2", quote = "A client may also ping its server by sending an IQ-get over the stream between the two entities. [...] The client MAY include a 'to' address of the client's bare JID <localpart@domain.tld> [...] If the server supports the ping namespace, it MUST return an IQ-result")
    public void pingServerWithSupportToBareClientJid() throws SmackException.NotConnectedException, InterruptedException, TestNotPossibleException, XMPPException.XMPPErrorException, SmackException.NoResponseException
    {
        if (!ServiceDiscoveryManager.getInstanceFor(conOne).serverSupportsFeature(Ping.NAMESPACE)) {
            throw new TestNotPossibleException("Server does not advertise support for " + Ping.NAMESPACE);
        }

        // Setup test fixture.
        final Jid target = conOne.getUser().asBareJid();
        final Ping request = new Ping(conOne, target);

        // Execute System under test.
        try (final StanzaCollector collector = connection.createStanzaCollectorAndSend(request)) {
            // Verify result.
            collector.nextResultOrThrow();
        } catch (XMPPException.XMPPErrorException e) {
            fail("As server advertises support for XMPP Ping, expected server to respond with a non-error response after '" + conOne.getUser() + "' queried its own bare JID. Received: " + e.getStanzaError());
        } catch (SmackException.NoResponseException e) {
            fail("Expected server to respond with any IQ response, after '" + conOne.getUser() + "' queried its own bare JID (but no response was received");
        }
    }

    @SmackIntegrationTest(section = "4.2", quote = "A client may also ping its server by sending an IQ-get over the stream between the two entities. [...] The client [...] MAY include no 'to' address (this signifies that the stanza shall be handled by the server on behalf of the connected user's bare JID, which in the case of <iq/> stanzas is equivalent to directing the IQ-get to the server itself) [...] If the server supports the ping namespace, it MUST return an IQ-result")
    public void pingServerWithSupportToDomainJid() throws SmackException.NotConnectedException, InterruptedException, TestNotPossibleException, XMPPException.XMPPErrorException, SmackException.NoResponseException
    {
        if (!ServiceDiscoveryManager.getInstanceFor(conOne).serverSupportsFeature(Ping.NAMESPACE)) {
            throw new TestNotPossibleException("Server does not advertise support for " + Ping.NAMESPACE);
        }

        // Setup test fixture.
        final Jid target = conOne.getXMPPServiceDomain();
        final Ping request = new Ping(conOne, target);

        // Execute System under test.
        try (final StanzaCollector collector = connection.createStanzaCollectorAndSend(request)) {
            // Verify result.
            collector.nextResultOrThrow();
        } catch (XMPPException.XMPPErrorException e) {
            fail("As server advertises support for XMPP Ping, expected server to respond with a non-error response after '" + conOne.getUser() + "' queried the server domain. Received: " + e.getStanzaError());
        } catch (SmackException.NoResponseException e) {
            fail("Expected server to respond with any IQ response, after '" + conOne.getUser() + "' queried the server domain (but no response was received");
        }
    }

    @SmackIntegrationTest(section = "4.2", quote = "A client may also ping its server by sending an IQ-get over the stream between the two entities. [...] The client [...] MAY include no 'to' address [...] If the server supports the ping namespace, it MUST return an IQ-result")
    public void pingServerWithSupportNoToAddress() throws SmackException.NotConnectedException, InterruptedException, TestNotPossibleException, XMPPException.XMPPErrorException, SmackException.NoResponseException
    {
        if (!ServiceDiscoveryManager.getInstanceFor(conOne).serverSupportsFeature(Ping.NAMESPACE)) {
            throw new TestNotPossibleException("Server does not advertise support for " + Ping.NAMESPACE);
        }

        // Setup test fixture.
        final Jid target = null;
        final Ping request = new Ping(conOne, target);

        // Execute System under test.
        try (final StanzaCollector collector = connection.createStanzaCollectorAndSend(request)) {
            // Verify result.
            collector.nextResultOrThrow();
        } catch (XMPPException.XMPPErrorException e) {
            fail("As server advertises support for XMPP Ping, expected server to respond with a non-error response after '" + conOne.getUser() + "' queried without using a 'to' address. Received: " + e.getStanzaError());
        } catch (SmackException.NoResponseException e) {
            fail("Expected server to respond with any IQ response, after '" + conOne.getUser() + "' queried without using a 'to' address (but no response was received");
        }
    }

    @SmackIntegrationTest(section = "4.2", quote = "A client may also ping its server by sending an IQ-get over the stream between the two entities. [...] The client MAY include a 'to' address of the client's bare JID <localpart@domain.tld> [...] If the server does not support the ping namespace, it MUST return a <service-unavailable/> error [...] The other error conditions defined in RFC 6120 [1] could also be returned if appropriate.")
    public void pingServerWithoutSupportToBareClientJid() throws SmackException.NotConnectedException, InterruptedException, TestNotPossibleException, XMPPException.XMPPErrorException, SmackException.NoResponseException
    {
        if (ServiceDiscoveryManager.getInstanceFor(conOne).serverSupportsFeature(Ping.NAMESPACE)) {
            throw new TestNotPossibleException("Server advertises support for " + Ping.NAMESPACE + " (which means the no-support use-case cannot be tested)");
        }

        // Setup test fixture.
        final Jid target = conOne.getUser().asBareJid();
        final Ping request = new Ping(conOne, target);

        // Execute System under test.
        try (final StanzaCollector collector = connection.createStanzaCollectorAndSend(request)) {
            // Verify result.
            collector.nextResultOrThrow();
            fail("As server advertises NO support for XMPP Ping, expected server to respond with an error response after '" + conOne.getUser() + "' queried its own bare JID. Received a non-error result instead.");
        } catch (XMPPException.XMPPErrorException e) {
            // expected
        } catch (SmackException.NoResponseException e) {
            fail("Expected server to respond with any IQ response, after '" + conOne.getUser() + "' queried its own bare JID (but no response was received");
        }
    }

    @SmackIntegrationTest(section = "4.2", quote = "A client may also ping its server by sending an IQ-get over the stream between the two entities. [...] The client [...] MAY include no 'to' address (this signifies that the stanza shall be handled by the server on behalf of the connected user's bare JID, which in the case of <iq/> stanzas is equivalent to directing the IQ-get to the server itself) [...] If the server does not support the ping namespace, it MUST return a <service-unavailable/> error [...] The other error conditions defined in RFC 6120 [1] could also be returned if appropriate.")
    public void pingServerWithoutSupportToDomainJid() throws SmackException.NotConnectedException, InterruptedException, TestNotPossibleException, XMPPException.XMPPErrorException, SmackException.NoResponseException
    {
        if (ServiceDiscoveryManager.getInstanceFor(conOne).serverSupportsFeature(Ping.NAMESPACE)) {
            throw new TestNotPossibleException("Server advertises support for " + Ping.NAMESPACE + " (which means the no-support use-case cannot be tested)");
        }

        // Setup test fixture.
        final Jid target = conOne.getXMPPServiceDomain();
        final Ping request = new Ping(conOne, target);

        // Execute System under test.
        try (final StanzaCollector collector = connection.createStanzaCollectorAndSend(request)) {
            // Verify result.
            collector.nextResultOrThrow();
            fail("As server advertises NO support for XMPP Ping, expected server to respond with an error response after '" + conOne.getUser() + "' queried the server domain. Received a non-error result instead.");
        } catch (XMPPException.XMPPErrorException e) {
            // expected
        } catch (SmackException.NoResponseException e) {
            fail("Expected server to respond with any IQ response, after '" + conOne.getUser() + "' queried the server domain (but no response was received");
        }
    }

    @SmackIntegrationTest(section = "4.2", quote = "A client may also ping its server by sending an IQ-get over the stream between the two entities. [...] The client [...] MAY include no 'to' address [...] If the server does not support the ping namespace, it MUST return a <service-unavailable/> error [...] The other error conditions defined in RFC 6120 [1] could also be returned if appropriate.")
    public void pingServerWithoutSupportNoToAddress() throws SmackException.NotConnectedException, InterruptedException, TestNotPossibleException, XMPPException.XMPPErrorException, SmackException.NoResponseException
    {
        if (ServiceDiscoveryManager.getInstanceFor(conOne).serverSupportsFeature(Ping.NAMESPACE)) {
            throw new TestNotPossibleException("Server advertises support for " + Ping.NAMESPACE + " (which means the no-support use-case cannot be tested)");
        }

        // Setup test fixture.
        final Jid target = null;
        final Ping request = new Ping(conOne, target);

        // Execute System under test.
        try (final StanzaCollector collector = connection.createStanzaCollectorAndSend(request)) {
            // Verify result.
            collector.nextResultOrThrow();
            fail("As server advertises NO support for XMPP Ping, expected server to respond with an error response after '" + conOne.getUser() + "' queried without using a 'to' address. Received a non-error result instead.");
        } catch (XMPPException.XMPPErrorException e) {
            // expected
        } catch (SmackException.NoResponseException e) {
            fail("Expected server to respond with any IQ response, after '" + conOne.getUser() + "' queried without using a 'to' address (but no response was received");
        }
    }
}
