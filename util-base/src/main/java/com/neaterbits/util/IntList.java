package com.neaterbits.util;

import java.util.Arrays;

public final class IntList {
    
    private final int [] EMPTY_ARRAY = new int[0];

    private int [] values;
    private int num;
    
    public void add(int value) {
        
        if (values == null) {
            values = new int[10];
        }
        else if (num == values.length) {
            this.values = Arrays.copyOf(values, values.length * 3);
        }
        
        values[num ++] = value;
    }
    
    public int [] toArray() {

        return values != null ? Arrays.copyOf(values, num) : EMPTY_ARRAY;
    }
}
