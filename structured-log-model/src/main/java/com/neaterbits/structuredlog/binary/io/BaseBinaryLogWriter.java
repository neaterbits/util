package com.neaterbits.structuredlog.binary.io;

import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogCommand;
import com.neaterbits.structuredlog.binary.logging.Loggable;
import com.neaterbits.structuredlog.binary.logging.ScalarType;

public abstract class BaseBinaryLogWriter extends BaseBinaryLogIO {

	private static final Map<Class<?>, ScalarType> SCALAR_TYPES;
	
	static {
		SCALAR_TYPES = new HashMap<>(ScalarType.values().length);
		
		for (ScalarType scalarType : ScalarType.values()) {
			SCALAR_TYPES.put(scalarType.getObjectType(), scalarType);
		}
	}
	
	private int commandNo = 0;

	
	protected abstract DataOutput getDataStream();
	
	protected final int writeHeader(LogCommand logCommand, Loggable loggable) {
		
		final int sequenceNo = writeLogCommand(logCommand);
		
		writeIdentifier(loggable);
		
		return sequenceNo;
	}

	protected final int writeFieldHeader(LogCommand logCommand, int objectSequenceNo) {
		
		final int sequenceNo = writeLogCommand(logCommand);

		writeSequenceNo(objectSequenceNo);
		
		return sequenceNo;
	}

	protected final int writeLogCommand(LogCommand logCommand) {
		
		try {
			getDataStream().writeByte((logCommand.ordinal()));
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		
		return commandNo ++;
	}
	
	protected final void writeInteger(int integer) {
		try {
			getDataStream().writeInt(integer);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	protected final void writeTypeId(int typeId) {
		writeInteger(typeId);
	}
	
	protected final void writeString(String string) {
		try {
			getDataStream().writeUTF(string);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	 protected final void writeIdentifier(Loggable loggable) {
		
		final String identifier = loggable != null ? loggable.getLogIdentifier() : null;
		
		writeIdentifier(identifier);
	}
	
	protected final void writeIdentifier(String identifier) {
		
		writeString(identifier != null ? identifier : "");
	}
	
	protected final void writeDescription(String descripion) {
		
		writeString(descripion != null ? descripion : "");
	}
	
	protected final void writeSequenceNo(int sequenceNo) {
		
		if (sequenceNo < 0) {
			throw new IllegalArgumentException();
		}
		
		try {
			getDataStream().writeInt(sequenceNo);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	protected final void writeScalar(Object value) {
		
		Objects.requireNonNull(value);
		
		final ScalarType scalarType = SCALAR_TYPES.get(value.getClass());
		
		if (scalarType == null) {
			throw new IllegalArgumentException();
		}
		
		final DataOutput dataStream = getDataStream();
		
		try {
			dataStream.writeByte(scalarType.ordinal());
			
			switch (scalarType) {
			
			case BYTE:
				dataStream.writeByte((Byte)value);
				break;
				
			case SHORT:
				dataStream.writeShort((Short)value);
				break;
				
			case INT:
				dataStream.writeInt((Integer)value);
				break;
				
			case LONG:
				dataStream.writeLong((Long)value);
				break;
				
			case FLOAT:
				dataStream.writeFloat((Float)value);
				break;
				
			case DOUBLE:
				dataStream.writeDouble((Double)value);
				break;
				
			case CHAR:
				dataStream.writeChar((Character)value);
				break;
				
			case STRING:
				dataStream.writeUTF((String)value);
				break;
				
			default:
				throw new UnsupportedOperationException();
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
}
