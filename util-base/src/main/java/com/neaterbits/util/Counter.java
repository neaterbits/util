package com.neaterbits.util;

public final class Counter {

    private int value;
    
    public Counter(int value) {
        this.value = value;
    }

    public void increase() {
        ++ value;
    }
    
    public void decrease() {
        -- value;
    }
    
    public int get() {
        return value;
    }
    
}
