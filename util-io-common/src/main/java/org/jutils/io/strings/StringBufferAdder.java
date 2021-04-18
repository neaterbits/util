package org.jutils.io.strings;

import org.jutils.buffers.MapStringStorageBuffer;

public interface StringBufferAdder {

    int addToBuffer(MapStringStorageBuffer buffer, long stringRef);
}
