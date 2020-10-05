package com.neaterbits.structuredlog.binary.logging;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.io.BaseBinaryLogWriter;

public final class LogContext extends BaseBinaryLogWriter {

	private final Map<Class<?>, ClassLogInfo> classes;
	private final Map<String, Integer> fieldNames;

	private final ByteArrayOutputStream baos;
	private final DataOutput dataStream;
	
	@Override
	protected DataOutput getDataStream() {
		return dataStream;
	}

	public LogContext() {
		this.classes = new HashMap<>();
		this.fieldNames = new HashMap<>();
		
		this.baos = new ByteArrayOutputStream(100000);
		this.dataStream = new DataOutputStream(baos);
	}
	
	private int writeGetOrAllocateTypeId(Class<? extends Loggable> type) {
		
		Objects.requireNonNull(type);

		final int typeId;

		final ClassLogInfo logInfo = classes.get(type);

		if (logInfo != null) {
			typeId = logInfo.getLogTypeId();
			
			writeTypeId(typeId);
		}
		else {
			typeId = classes.size();
		
			classes.put(type, new ClassLogInfo(typeId));
			
			writeTypeId(typeId | 1 << 31);
			writeString(type.getName());
		}

		return typeId;
	}

	private int writeGetOrAllocateFieldName(String field) {
		
		Objects.requireNonNull(field);

		Integer fieldIdx = fieldNames.get(field);

		if (fieldIdx != null) {
			writeInteger(fieldIdx);
		}
		else {
			fieldIdx = fieldNames.size();
		
			fieldNames.put(field, fieldIdx);
			
			writeInteger(fieldIdx | 1 << 31);
			writeString(field);
		}

		return fieldIdx;
	}

	public void writeLogBufferToOutput(OutputStream outputStream) throws IOException {
		
		final byte [] data = baos.toByteArray();
		
		outputStream.write(data);
	}
	
	<T extends Loggable> T setLoggableField(Loggable parent, String field, T sub) {

		if (parent.getConstructorLogSequenceNo() < 0) {
			throw new IllegalArgumentException();
		}
		
		final int sequenceNo = writeFieldHeader(LogCommand.SET_LOGGABLE_FIELD, parent.getConstructorLogSequenceNo());
		
		debugWrite(
				sequenceNo,
				LogCommand.SET_LOGGABLE_FIELD,
				"objectSequenceNo", String.valueOf(parent.getConstructorLogSequenceNo()),
				"field", field,
				"sub", sub != null ? sub.toString() : null);
		
		writeGetOrAllocateFieldName(field);
		
		writeIdentifier(sub);
		
		return sub;
	}

	
	void logRootObject(int constructorLogSequenceNo, String identifier) {

		final int sequenceNo = writeLogCommand(LogCommand.ROOT_OBJECT);
		writeSequenceNo(constructorLogSequenceNo);
		writeIdentifier(identifier);

		debugWrite(sequenceNo, LogCommand.ROOT_OBJECT, "sequenceNo", String.valueOf(sequenceNo), "identifier", identifier);
	}

	int logConstructor(Object object, Class<? extends Loggable> type, String identifier, String localIdentifier, String description) {

		final int sequenceNo = writeLogCommand(LogCommand.CONSTRUCTOR);

		writeSequenceNo(sequenceNo);

		final int identityHashCode = System.identityHashCode(object);
		final int hashCode = object.hashCode();
		
		final int typeId = writeGetOrAllocateTypeId(type);

		writeInteger(identityHashCode);
		writeInteger(hashCode);

		debugWrite(
				sequenceNo,
				LogCommand.CONSTRUCTOR,
				"identityHashCode", String.valueOf(identityHashCode),
				"hashCode", String.valueOf(hashCode),
				"type", type.getName(),
				"typeId", String.valueOf(typeId),
				"identifier", identifier,
				"localIdentifier", localIdentifier,
				"description", description);

		writeIdentifier(identifier);
		writeIdentifier(localIdentifier);
		writeDescription(description);
		
		return sequenceNo;
	}

	
	void logConstructorScalarField(Loggable parent, String field, Object value) {
		
		
		final int sequenceNo = writeFieldHeader(LogCommand.CONSTRUCTOR_SCALAR_FIELD, parent.getConstructorLogSequenceNo());
		
		final int fieldIdx = writeGetOrAllocateFieldName(field);

		debugWrite(
				sequenceNo,
				LogCommand.CONSTRUCTOR_SCALAR_FIELD,
				"objectSequenceNo", String.valueOf(parent.getConstructorLogSequenceNo()),
				"field", field,
				"fieldIdx", String.valueOf(fieldIdx),
				"value", value != null ? value.toString() : null);

		writeScalar(value);
	}

	void logConstructorLoggableField(Loggable parent, String field, Loggable value) {
		
		if (value != null) {
			final int sequenceNo = writeFieldHeader(LogCommand.CONSTRUCTOR_LOGGABLE_FIELD, parent.getConstructorLogSequenceNo());
			
			final int fieldIdx = writeGetOrAllocateFieldName(field);

			debugWrite(
					sequenceNo,
					LogCommand.CONSTRUCTOR_LOGGABLE_FIELD,
					"objectSequenceNo", String.valueOf(parent.getConstructorLogSequenceNo()),
					"field", field,
					"fieldIdx", String.valueOf(fieldIdx),
					"value", value != null ? value.getLogDebugString() : null);

			writeLoggableReference(value);
		}
	}

	<T extends Loggable> Collection<T> logConstructorCollectionField(Loggable parent, String field, Collection<T> values) {

		writeConstructorCollectionField(parent, field, values);
		
		return new CollectionWrapper<>(values);
	}

	<T extends Loggable> List<T> logConstructorListField(Loggable parent, String field, List<T> values) {

		writeConstructorCollectionField(parent, field, values);
		
		return new ListWrapper<>(values);
	}
	
	private void writeConstructorCollectionField(Loggable parent, String field, Collection<? extends Loggable> values) {
		
		final int sequenceNo = writeFieldHeader(LogCommand.CONSTRUCTOR_COLLECTION_FIELD, parent.getConstructorLogSequenceNo());
		
		final int fieldIdx = writeGetOrAllocateFieldName(field);
		
		debugWrite(
				sequenceNo,
				LogCommand.CONSTRUCTOR_COLLECTION_FIELD,
				"objectSequenceNo", String.valueOf(parent.getConstructorLogSequenceNo()),
				"field", field,
				"fieldIdx", String.valueOf(fieldIdx),
				"numEntries", values != null ? String.valueOf(values.size()) : null);

		writeCollection(values);
	}
	

	void logSetScalarField(Loggable parent, String field, Object value) {
		
		final int sequenceNo = writeFieldHeader(LogCommand.SET_SCALAR_FIELD, parent.getConstructorLogSequenceNo());
		
		final int fieldIdx = writeGetOrAllocateFieldName(field);

		debugWrite(
				sequenceNo,
				LogCommand.SET_SCALAR_FIELD,
				"objectSequenceNo", String.valueOf(parent.getConstructorLogSequenceNo()),
				"field", field,
				"fieldIdx", String.valueOf(fieldIdx),
				"value", value != null ? value.toString() : null);

		writeScalar(value);
	}

	
	void logSetLoggableField(Loggable parent, String field, Loggable value) {
		
		final int sequenceNo = writeFieldHeader(LogCommand.SET_LOGGABLE_FIELD, parent.getConstructorLogSequenceNo());
		
		final int fieldIdx = writeGetOrAllocateFieldName(field);

		debugWrite(
				sequenceNo,
				LogCommand.SET_LOGGABLE_FIELD,
				"objectSequenceNo", String.valueOf(parent.getConstructorLogSequenceNo()),
				"field", field,
				"fieldIdx", String.valueOf(fieldIdx),
				"value", value != null ? value.getLogDebugString() : null);

		writeIdentifier(value);
	}
	
	<T extends Loggable> Collection<T> logSetCollectionField(Loggable loggable, String field, Collection<T> collection) {
		
		writeSetCollectionField(loggable, field, collection);
		
		return new CollectionWrapper<>(collection);
	}

	<T extends Loggable> List<T> logSetListField(Loggable loggable, String field, List<T> list) {

		writeSetCollectionField(loggable, field, list);
		
		return new ListWrapper<>(list);
	}
	
	private void writeSetCollectionField(Loggable loggable, String field, Collection<? extends Loggable> collection) {

		final int sequenceNo = writeHeader(LogCommand.SET_COLLECTION_FIELD, loggable);
		
		final int fieldIdx = writeGetOrAllocateFieldName(field);

		debugWrite(
				sequenceNo,
				LogCommand.SET_COLLECTION_FIELD,
				"field", field,
				"fieldIdx", String.valueOf(fieldIdx),
				"numEntries", collection != null ? String.valueOf(collection.size()) : null);

		writeCollection(collection);
	}
	
	
	void error(Loggable loggable, String message) {
		writeLogMessage(LogCommand.ERROR, loggable, message);
	}

	void debug(Loggable loggable, String message) {
		writeLogMessage(LogCommand.DEBUG, loggable, message);
	}
	
	void trace(Loggable loggable, String message) {
		writeLogMessage(LogCommand.TRACE, loggable, message);
	}
	
	private void writeLogMessage(LogCommand logCommand, Loggable loggable, String message) {
		final int sequenceNo = writeLogCommand(logCommand);
		
		debugWrite(sequenceNo, logCommand, "message", message);

		writeSequenceNo(loggable.getConstructorLogSequenceNo());
		
		writeString(message);
	}
	
	private void writeCollection(Collection<? extends Loggable> values) {
		try {
			if (values == null || values.isEmpty()) {
				dataStream.writeInt(0);
			}
			else {
				dataStream.writeInt(values.size());
				
				for (Loggable loggable : values) {
					writeLoggableReference(loggable);
				}
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private void writeLoggableReference(Loggable loggable) {
		writeSequenceNo(loggable.getConstructorLogSequenceNo());
	}
}
