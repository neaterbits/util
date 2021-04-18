package org.jutils.process;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.function.Consumer;

import org.jutils.Value;
import org.jutils.threads.ForwardResultToCaller;

public class ProcessRunner {
    
    @FunctionalInterface
    public interface ProcessCompletion {
        
        void onComplete(int status, Runnable join);
    }

    public static void run(
            String [] commandLine,
            Consumer<String> onStdout,
            Consumer<String> onStderr,
            ProcessCompletion processCompletion,
            ForwardResultToCaller forwarder) throws IOException {
        
        Objects.requireNonNull(processCompletion);
        Objects.requireNonNull(forwarder);
        
        final ProcessBuilder processBuilder = new ProcessBuilder(commandLine);
        
        final Process process = processBuilder.start();

        Thread stdoutThread;
        Thread stderrThread;
        
        if (onStdout != null) {
            stdoutThread = new IOThread(process.getInputStream(), onStdout, forwarder);
            
            stdoutThread.start();
        }
        else {
            stdoutThread = null;
        }

        if (onStderr != null) {
            stderrThread = new IOThread(process.getErrorStream(), onStderr, forwarder);

            stderrThread.start();
        }
        else {
            stderrThread = null;
        }
        
        final Value<Thread> processWatcherValue = new Value<>();
        
        final Thread processWatcher = new Thread(() -> {
           
            for (;;) {
                try {
                    final int status = process.waitFor();
                    
                    try {
                        
                        if (stdoutThread != null) {
                            stdoutThread.join();
                        }
                        
                        if (stderrThread != null) {
                            stderrThread.join();
                        }
                    }
                    finally {
                        final Runnable join = () -> {
                            try {
                                processWatcherValue.get().join();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        };
    
                        forwarder.forward(() -> processCompletion.onComplete(status, join));
                    }
                    break;

                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        processWatcherValue.set(processWatcher);
        
        processWatcher.start();
    }
    
    private static class IOThread extends Thread {

        private final InputStream inputStream;
        private final Consumer<String> listener;
        private final ForwardResultToCaller forwarder;
        
        IOThread(InputStream inputStream, Consumer<String> listener, ForwardResultToCaller forwarder) {
        
            Objects.requireNonNull(inputStream);
            Objects.requireNonNull(listener);
            Objects.requireNonNull(forwarder);
            
            this.inputStream = inputStream;
            this.listener = listener;
            this.forwarder = forwarder;
        }


        @Override
        public void run() {

            final byte [] array = new byte[10000];
            
            try {
                for (;;) {
                    
                    int bytesRead = inputStream.read(array);
                    
                    if (bytesRead < 0) {
                        break;
                    }
    
                    final String string = new String(array, 0, bytesRead, Charset.defaultCharset());
                    
                    forwarder.forward(() -> listener.accept(string));
                }
            }
            catch (IOException ex) {
                
            }
        }
    }
}
