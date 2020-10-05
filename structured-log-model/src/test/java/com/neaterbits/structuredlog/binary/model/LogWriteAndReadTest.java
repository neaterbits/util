package com.neaterbits.structuredlog.binary.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.structuredlog.binary.logging.Loggable;

public class LogWriteAndReadTest {

	private static class BaseLoggable implements Loggable {
		
		private final String logIdentifier;
		private final String localLogIdentifier;
		private final String description;

		private int sequenceNo;
		
		public BaseLoggable(LogContext logContext, String logIdentifier, String localLogIdentifier, String description) {
			this.logIdentifier = logIdentifier;
			this.localLogIdentifier = localLogIdentifier;
			this.description = description;

			this.sequenceNo = logConstructor(logContext, this, getClass(), logIdentifier, localLogIdentifier, description);
		}

		@Override
		public final int getConstructorLogSequenceNo() {
			return sequenceNo;
		}

		@Override
		public final String getLogIdentifier() {
			return logIdentifier;
		}

		@Override
		public final String getLogLocalIdentifier() {
			return localLogIdentifier;
		}

		@Override
		public final String getDescription() {
			return description;
		}
	}
	
	public static class TestLoggable extends BaseLoggable implements Loggable {

		private final List<SubTestLoggable> sub;
		
		public TestLoggable(LogContext logContext, String logIdentifier, String localLogIdentifier,
				String description) {
			
			super(logContext, logIdentifier, localLogIdentifier, description);
			
			final List<SubTestLoggable> subLoggables = Arrays.asList(
					new SubTestLoggable(logContext, "sublog", "localsublog", "Sub log"),
					new SubTestLoggable(logContext, "othersublog", "otherlocalsublog", "Other sub log")
			);
			
			this.sub = logConstructorListField(logContext, "sub", subLoggables);
		}

		public List<SubTestLoggable> getSub() {
			return sub;
		}
	}

	public static class SubTestLoggable extends BaseLoggable implements Loggable {

		public SubTestLoggable(LogContext logContext, String logIdentifier, String localLogIdentifier,
				String description) {
			super(logContext, logIdentifier, localLogIdentifier, description);
		}
	}
	
	@Test
	public void testWriteAndRead() throws IOException {

		final LogContext logContext = new LogContext();
		
		final TestLoggable testLoggable = new TestLoggable(logContext, "loggable", "localloggable", "Loggable");
		
		testLoggable.logRootObject(logContext);
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		logContext.writeLogBufferToOutput(baos);

		final BinaryLogReader reader = new BinaryLogReader();
		
		final LogModel logModel = reader.readLog(new ByteArrayInputStream(baos.toByteArray()));
		assertThat(logModel).isNotNull();

		assertThat(logModel.getLogRootObjects().size()).isEqualTo(1);
	
		final LogObject rootObject = logModel.getLogRootObjects().get(0);
		
		assertThat(rootObject.getLogIdentifier()).isEqualTo("loggable");
		assertThat(rootObject.getLogLocalIdentifier()).isEqualTo("localloggable");
		assertThat(rootObject.getDescription()).isEqualTo("Loggable");
		
		assertThat(rootObject.getFields().size()).isEqualTo(1);
		
		final LogField field = rootObject.getFields().iterator().next();
		
		assertThat(field.getConstructorSequenceNo()).isEqualTo(rootObject.getConstructorLogSequenceNo());
		
		assertThat(field.getFieldName()).isEqualTo("sub");
		
		assertThat(field instanceof LogCollectionField).isTrue();

		final LogCollectionField collectionField = (LogCollectionField)field;

		final Collection<LogObject> subs = collectionField.getCollection();
		
		assertThat(subs.size()).isEqualTo(2);
		
		final Iterator<LogObject> iterator = subs.iterator();
		
		final LogObject sub = iterator.next();
		
		assertThat(sub.getLogIdentifier()).isEqualTo("sublog");
		assertThat(sub.getLogLocalIdentifier()).isEqualTo("localsublog");
		assertThat(sub.getDescription()).isEqualTo("Sub log");

		final LogObject othersub = iterator.next();
		
		assertThat(othersub.getLogIdentifier()).isEqualTo("othersublog");
		assertThat(othersub.getLogLocalIdentifier()).isEqualTo("otherlocalsublog");
		assertThat(othersub.getDescription()).isEqualTo("Other sub log");
	}
	
}
