package com.neaterbits.util.concurrency.dependencyresolution.model;

public final class TargetDebug {

    private final String identifier;
    private final String localIdentifier;
    private final String description;

    public TargetDebug(
            String identifier,
            String localIdentifier,
            String description,
            boolean verifyDebugStrings) {
        
        if (verifyDebugStrings) {
            verifyIsNotToString(identifier);
            verifyIsNotToString(localIdentifier);
        }

        this.identifier = identifier;
        this.localIdentifier = localIdentifier;
        this.description = description;
    }
    
    String getIdentifier() {
        return identifier;
    }

    String getLocalIdentifier() {
        return localIdentifier;
    }

    String getDescription() {
        return description;
    }

    private static void verifyIsNotToString(String debugString) {
        
        if (debugString.contains("@")) {
            throw new IllegalArgumentException();
        }
    }
}
