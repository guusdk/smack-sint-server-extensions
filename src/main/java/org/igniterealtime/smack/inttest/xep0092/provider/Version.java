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
package org.igniterealtime.smack.inttest.xep0092.provider;

import org.igniterealtime.smack.inttest.xep0363.element.SlotRaw;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IqData;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.provider.IqProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jxmpp.JxmppContext;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * A 'version' extension that allows for invalid data (such that this can explicitly be asserted in a test).
 *
 * @author Guus der Kinderen, guus@goodbytes.nl
 */
public class Version extends IQ
{
    public static final String ELEMENT_NAME = "query";
    public static final String NAMESPACE = "jabber:iq:version";

    private final String name;
    private final String version;
    private final String os;

    public Version()
    {
        this(null, null, null);
    }

    public Version(String name, String version, String os)
    {
        super(ELEMENT_NAME, NAMESPACE);
        this.name = name;
        this.version = version;
        this.os = os;
    }

    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return version;
    }

    public String getOs()
    {
        return os;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        if (name != null) {
            xml.element("name", name);
        }
        if (version != null) {
            xml.element("version", version);
        }
        if (os != null) {
            xml.element("os", os);
        }
        return xml;
    }

    public static class Provider extends IqProvider<Version>
    {
        public Provider() {
        }

        public Version parse(XmlPullParser parser, int initialDepth, IqData iqData, XmlEnvironment xmlEnvironment, JxmppContext jxmppContext) throws XmlPullParserException, IOException
        {
            boolean done = false;
            String name = null;
            String version = null;
            String os = null;

            while (!done)
            {
                final XmlPullParser.Event eventType = parser.next();

                if (eventType == XmlPullParser.Event.START_ELEMENT) {
                    switch (parser.getName()) {
                        case "name": name = parser.nextText(); break;
                        case "version": version = parser.nextText(); break;
                        case "os": os = parser.nextText(); break;
                    }
                }
                if (eventType == XmlPullParser.Event.END_ELEMENT && ELEMENT_NAME.equals(parser.getName())) {
                    done = true;
                }
            }
            return new Version(name, version, os);
        }
    }
}
