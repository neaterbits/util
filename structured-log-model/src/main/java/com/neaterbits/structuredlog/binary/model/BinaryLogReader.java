package com.neaterbits.structuredlog.binary.model;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.neaterbits.structuredlog.binary.io.BaseBinaryLogIO;
import com.neaterbits.structuredlog.binary.logging.LogCommand;

public class BinaryLogReader extends BaseBinaryLogIO {

	public LogModel readLog(File file) throws IOException {
		

		final LogModel logModel;
		
		try (InputStream inputStream = new FileInputStream(file)) {
			logModel = readLog(inputStream);
		}

		return logModel;
	}
	
	public LogModel readLog(InputStream inputStream) throws IOException {
			
		final Map<Integer, String> typeNames = new HashMap<>();
		final Map<Integer, String> fieldNames = new HashMap<>();
		
		final Map<Integer, LogObject> objectsByConstructorSequenceNo = new HashMap<>();
		final List<Integer> logRoots = new ArrayList<>();
		
		final List<LogMessage> logMessages = new ArrayList<>();

		final DataInputStream dataInput = new DataInputStream(inputStream);
			
		int commandNo = 0;

		for (;;) {
			
			final byte logCommandOrdinal;
			
			try {
				logCommandOrdinal = dataInput.readByte();
				
				if (logCommandOrdinal < 0) {
					// EOF
					break;
				}
			}
			catch (EOFException ex) {
				break;
			}
			
			final LogCommand logCommand = LogCommand.values()[logCommandOrdinal];
			
			final int sequenceNo = commandNo ++;
			
			switch (logCommand) {
			
			case ROOT_OBJECT: {
				final int objectConstructorSequenceNo = readSequenceNo(dataInput);
				final String identifier = readIdentifier(dataInput);
				logRoots.add(objectConstructorSequenceNo);

				debugRead(
						sequenceNo,
						LogCommand.ROOT_OBJECT,
						"constructorSequenceNo", String.valueOf(objectConstructorSequenceNo),
						"identifier", identifier);
				break;
			}
			
			case CONSTRUCTOR: {
				
				final int constructorSequenceNo = readSequenceNo(dataInput);
				
				if (sequenceNo != constructorSequenceNo) {
					throw new IllegalStateException();
				}

				final LogObject logObject = readLogObject(sequenceNo, null, dataInput, typeNames);

				if (objectsByConstructorSequenceNo.containsKey(logObject.getConstructorLogSequenceNo())) {
					throw new IllegalStateException("Already contains " + logObject.getConstructorLogSequenceNo()
						+ " of type " + logObject.getType());
				}

				objectsByConstructorSequenceNo.put(logObject.getConstructorLogSequenceNo(), logObject);
				
				debugRead(
						sequenceNo,
						LogCommand.CONSTRUCTOR,
						"type", typeNames.get(logObject.getType()),
						"typeId", String.valueOf(logObject.getType()),
						"identifier", logObject.getLogIdentifier(),
						"localIdentifier", logObject.getLogLocalIdentifier(),
						"description", logObject.getDescription());
				break;
			}
			
			case CONSTRUCTOR_LOGGABLE_FIELD: {
				final int objectSequenceNo = readSequenceNo(dataInput);
				
				final LogObject fieldInstance = objectsByConstructorSequenceNo.get(objectSequenceNo); 
				
				final int fieldIdx = readFieldIndex(dataInput, fieldNames);

				final LogObject logObject = readLogObjectFromReference(dataInput, objectsByConstructorSequenceNo);
				
				debugRead(
						sequenceNo,
						LogCommand.CONSTRUCTOR_LOGGABLE_FIELD,
						"objectSequenceNo", String.valueOf(objectSequenceNo),
						"field", fieldNames.get(fieldIdx),
						"fieldIdx", String.valueOf(fieldIdx),
						"value", logObject != null ? logObject.getLogDebugString() : null);

				
				fieldInstance.addField(fieldNames.get(fieldIdx), new LoggableField(
						sequenceNo,
						objectSequenceNo,
						fieldInstance,
						fieldNames.get(fieldIdx),
						logObject));
				break;
			}

			case CONSTRUCTOR_COLLECTION_FIELD: {
				final int objectSequenceNo = readSequenceNo(dataInput);

				final LogObject fieldInstance = objectsByConstructorSequenceNo.get(objectSequenceNo); 

				final int fieldIdx = readFieldIndex(dataInput, fieldNames);
				
				final int numEntries = dataInput.readInt();

				debugRead(
						sequenceNo,
						LogCommand.CONSTRUCTOR_COLLECTION_FIELD,
						"objectSequenceNo", String.valueOf(objectSequenceNo),
						"field", fieldNames.get(fieldIdx),
						"fieldIdx", String.valueOf(fieldIdx),
						"numEntries", String.valueOf(numEntries));

				final List<LogObject> collection = new ArrayList<>(numEntries);
				
				for (int i = 0; i < numEntries; ++ i) {
					collection.add(readLogObjectFromReference(dataInput, objectsByConstructorSequenceNo));
				}
				
				final LogCollectionField field = new LogCollectionField(sequenceNo, objectSequenceNo, fieldInstance, fieldNames.get(fieldIdx), collection);
				
				fieldInstance.addField(fieldNames.get(fieldIdx), field);
				break;
			}

			case DEBUG:
				logMessages.add(readLogMessage(sequenceNo, Severity.DEBUG, dataInput, objectsByConstructorSequenceNo));
				break;
			
			default:
				throw new UnsupportedOperationException();
			}
		}
		
		final List<LogObject> rootObjects = logRoots.stream()
				.map(objectsByConstructorSequenceNo::get)
				.filter(object -> object != null)
				.collect(Collectors.toList());
		
		return new LogModel(typeNames, fieldNames, objectsByConstructorSequenceNo, logRoots, rootObjects, logMessages);
	}
	
	private LogMessage readLogMessage(int sequenceNo, Severity severity, DataInput dataInput, Map<Integer, LogObject> objectsByConstructorSequenceNo) throws IOException {
		
		final int objectSequenceNo = readSequenceNo(dataInput);

		final String message = dataInput.readUTF();
		
		return new LogMessage(sequenceNo, severity, objectsByConstructorSequenceNo.get(objectSequenceNo), message);
	}

	private LogObject readLogObjectFromReference(DataInput dataInput, Map<Integer, LogObject> objectsByConstructorSequenceNo) throws IOException {
		
		final int sequenceNo = readSequenceNo(dataInput);
		
		return objectsByConstructorSequenceNo.get(sequenceNo);
	}

	private LogObject readLogObject(int sequenceNo, LogField parent, DataInput dataInput, Map<Integer, String> typeNames) throws IOException {

		final int typeId = readTypeId(dataInput, typeNames);
		final int identityHashCode = dataInput.readInt();
		final int hashCode = dataInput.readInt();
		final String itemIdentifier = readIdentifier(dataInput);
		final String itemLocalIdentifier = readIdentifier(dataInput);
		final String description = readDescription(dataInput);
		
		return new LogObject(
				sequenceNo,
				sequenceNo,
				parent,
				identityHashCode,
				hashCode,
				typeNames.get(typeId),
				itemIdentifier,
				itemLocalIdentifier,
				description);
	}
	
	
	private int readTypeId(DataInput dataInput, Map<Integer, String> typeNames) throws IOException {
		return readStringIndex(dataInput, typeNames);
	}

	private int readFieldIndex(DataInput dataInput, Map<Integer, String> fieldNames) throws IOException {
		return readStringIndex(dataInput, fieldNames);
	}

	private int readStringIndex(DataInput dataInput, Map<Integer, String> map) throws IOException {
		final int indexFlag = dataInput.readInt();
		
		final int index;
		
		if ((indexFlag & (1 << 31)) != 0) {
			
			final String name = dataInput.readUTF();
			
			index = indexFlag & ~(1 << 31);
			
			map.put(index, name);
		}
		else {
			index = indexFlag;
		}
		
		return index;
	}
	
	private static String readIdentifier(DataInput dataInput) throws IOException {
		final String identifier = dataInput.readUTF();
		
		return identifier.isEmpty() ? null : identifier;
	}
	
	private static int readSequenceNo(DataInput dataInput) throws IOException {
		return dataInput.readInt();
	}

	private static String readDescription(DataInput dataInput) throws IOException {
		final String description = dataInput.readUTF();
		
		return description.isEmpty() ? null : description;
	}
	
}

