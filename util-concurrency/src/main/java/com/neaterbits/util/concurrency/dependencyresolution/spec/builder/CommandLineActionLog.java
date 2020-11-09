package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

public final class CommandLineActionLog extends ActionLog {

    private final String commandLine;
    private final int exitCode;
    
    public CommandLineActionLog(String commandLine, int exitCode) {
        this.commandLine = commandLine;
        this.exitCode = exitCode;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public int getExitCode() {
        return exitCode;
    }
}
