package org.igniterealtime.smack.inttest.util;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.debugger.SmackDebuggerFactory;

public final class FileLoggerFactory implements SmackDebuggerFactory
{
    public static final SmackDebuggerFactory INSTANCE = new FileLoggerFactory();

    public FileLoggerFactory()
    {
    }

    @Override
    public SmackDebugger create(XMPPConnection connection) throws IllegalArgumentException
    {
        return new FileLogger(connection);
    }
}
