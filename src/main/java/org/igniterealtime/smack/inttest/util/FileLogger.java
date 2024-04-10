package org.igniterealtime.smack.inttest.util;

import org.igniterealtime.smack.inttest.SmackIntegrationTestFramework;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.debugger.AbstractDebugger;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.debugger.SmackDebuggerFactory;
import org.jivesoftware.smack.util.ExceptionUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogger extends AbstractDebugger
{
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.S");
    private final Path logDir;

    public FileLogger(XMPPConnection connection) {
        super(connection);

        final String logDir = System.getProperty("logDir");
        if (logDir != null) {
            final Path logDirPath = Paths.get(logDir);
            try {
                Files.createDirectories(logDirPath);
            } catch (IOException e) {
                throw new IllegalStateException("Logging location does not exist or is not writable: " + logDirPath.toAbsolutePath(), e);
            }
            this.logDir = logDirPath;
            System.out.println("Saving debug logs in " + logDirPath.toAbsolutePath());
        } else {
            this.logDir = null;
        }
    }

    @Override
    protected void log(String logMessage) {
        String formatedDate;
        synchronized (dateFormatter) {
            formatedDate = dateFormatter.format(new Date());
        }

        String filename;
        final SmackIntegrationTestFramework.ConcreteTest testUnderExecution = SmackIntegrationTestFramework.getTestUnderExecution();
        if (testUnderExecution != null) {
            filename = testUnderExecution.toString();
        } else {
            filename = "output";
        }
        filename += ".log";

        Path logPath;
        if (logDir != null) {
            logPath = logDir.resolve(filename);
        } else {
            logPath = Paths.get(filename);
        }

        try (final Writer writer = new OutputStreamWriter(new FileOutputStream(logPath.toFile(), true), StandardCharsets.UTF_8);
             final BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            bufferedWriter.write(formatedDate + ' ' + logMessage + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Unable to write log to file " + filename);
            e.printStackTrace();
        }
    }

    @Override
    protected void log(String logMessage, Throwable throwable) {
        String stacktrace = ExceptionUtil.getStackTrace(throwable);
        log(logMessage + '\n' + stacktrace);
    }

    public static final class Factory implements SmackDebuggerFactory
    {

        public static final SmackDebuggerFactory INSTANCE = new FileLogger.Factory();

        public Factory() {
        }

        @Override
        public SmackDebugger create(XMPPConnection connection) throws IllegalArgumentException {
            return new FileLogger(connection);
        }
    }
}
