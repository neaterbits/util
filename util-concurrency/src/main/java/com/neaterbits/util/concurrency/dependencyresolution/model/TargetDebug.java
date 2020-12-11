package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.Objects;

public final class TargetDebug {

    private final String semanticType;
    
    private final String identifier;
    private final String localIdentifier;
    
    private final String semanticAction;
    
    private final String description;

    public TargetDebug(
            String semanticType,
            String identifier,
            String localIdentifier,
            String semanticAction,
            String description,
            boolean verifyDebugStrings) {
        
        if (verifyDebugStrings) {
            verifyIsNotToString(identifier);
            verifyIsNotToString(localIdentifier);
        }
        
        Objects.requireNonNull(semanticType);

        this.semanticType = semanticType;
        this.identifier = identifier;
        this.localIdentifier = localIdentifier;
        this.semanticAction = semanticAction;
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
    
    private static final char SEP = '%';
    
    String getDebugString(String implType) {

        Objects.requireNonNull(implType);

        return implType + SEP + semanticType + SEP + identifier + (semanticAction != null ? SEP + semanticAction : ""); 
    }

    private static void verifyIsNotToString(String debugString) {
        
        if (debugString.contains("@")) {
            throw new IllegalArgumentException();
        }
    }
}
