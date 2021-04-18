package org.jutils.di;

import javax.annotation.concurrent.Immutable;

@Immutable
final class CollectedComponentRequirement extends BaseComponentRequirement {

    private final String role;
    private final String fieldName;

    CollectedComponentRequirement(String role, Object roleHint, String fieldName) {
        super(roleHint);

        this.role = role;
        this.fieldName = fieldName;
    }

    String getRole() {
        return role;
    }

    String getFieldName() {
        return fieldName;
    }
}

