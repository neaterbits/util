package org.jutils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class IdentityKeyTest {

    @Test
    public void testEquals() {

        final Object obj1 = new Object();
        final Object obj2 = new Object();

        assertThat(new IdentityKey<>(obj1).equals(null)).isFalse();
        assertThat(new IdentityKey<>(obj1).equals(new IdentityKey<>(""))).isFalse();
        assertThat(new IdentityKey<>(obj1).equals(new IdentityKey<>(obj2))).isFalse();
        assertThat(new IdentityKey<>(obj1).equals(new IdentityKey<>(obj1))).isTrue();
    }
}
