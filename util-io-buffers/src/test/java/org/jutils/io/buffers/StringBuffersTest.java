package org.jutils.io.buffers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.jutils.io.loadstream.LoadStream;
import org.jutils.io.loadstream.SimpleLoadStream;

public class StringBuffersTest {
    
    private StringBuffers prepareTokenizer(String input) throws IOException {
        
        final LoadStream loadStream = new SimpleLoadStream(input);
        
        final StringBuffers buffers = new StringBuffers(loadStream);

        return buffers;
    }
    
    @Test
    public void testGetStringRefAtSomePosition() throws IOException {
        
        final StringBuffers buffers = prepareTokenizer("a test string");
        
        buffers.readNext();
        
        final long startPos = buffers.getReadPos();
        
        buffers.readNext();
        buffers.readNext();
        buffers.readNext();
        
        final long endPos = buffers.getReadPos();
        
        // Read past to advance read pos past endPos
        buffers.readNext();
        buffers.readNext();

        final long ref = buffers.getStringRef(startPos, endPos, 0, 0);
        
        assertThat(buffers.asString(ref)).isEqualTo(" te");
    }
}
