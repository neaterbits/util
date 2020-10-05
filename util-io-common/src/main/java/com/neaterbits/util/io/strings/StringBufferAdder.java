package com.neaterbits.util.io.strings;

import com.neaterbits.util.buffers.MapStringStorageBuffer;

public interface StringBufferAdder {

    int addToBuffer(MapStringStorageBuffer buffer, long stringRef);
}
