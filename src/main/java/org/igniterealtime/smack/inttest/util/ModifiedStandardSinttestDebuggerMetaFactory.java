package org.igniterealtime.smack.inttest.util;

import org.igniterealtime.smack.inttest.debugger.SinttestDebuggerFactory;
import org.igniterealtime.smack.inttest.debugger.SinttestDebuggerMetaFactory;
import org.igniterealtime.smack.inttest.debugger.SinttestDebugger;

import java.time.ZonedDateTime;

public class ModifiedStandardSinttestDebuggerMetaFactory implements SinttestDebuggerMetaFactory
{
    @Override
    public SinttestDebuggerFactory create(String debuggerOptions)
    {
        return new ModifiedStandardSintDebuggerFactory(debuggerOptions);
    }

    class ModifiedStandardSintDebuggerFactory implements SinttestDebuggerFactory
    {
        final String debuggerOptions;

        public ModifiedStandardSintDebuggerFactory(String debuggerOptions)
        {
            this.debuggerOptions = debuggerOptions;
        }

        @Override
        public SinttestDebugger create(ZonedDateTime testRunStart, String testRunId)
        {
            return new ModifiedStandardSinttestDebugger(testRunStart, testRunId, debuggerOptions);
        }
    }
}
