package org.jutils.concurrency.dependencyresolution.executor.logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jutils.concurrency.dependencyresolution.executor.CollectedObject;
import org.jutils.concurrency.dependencyresolution.executor.CollectedProduct;
import org.jutils.concurrency.dependencyresolution.executor.CollectedProducts;
import org.jutils.concurrency.dependencyresolution.executor.CollectedTargetObjects;
import org.jutils.concurrency.dependencyresolution.executor.Status;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionLog;
import org.jutils.structuredlog.xml.model.Log;
import org.jutils.structuredlog.xml.model.LogData;
import org.jutils.structuredlog.xml.model.LogDataEntry;
import org.jutils.structuredlog.xml.model.LogEntry;
import org.jutils.structuredlog.xml.model.LogPath;

public final class StructuredTargetExecutorLogger implements TargetExecutorLogger {

	private final Log log;

	private final Map<LogPath, Integer> paths;
	
	private final Function<TargetDefinition<?>, List<String>> getPath;
	
	public StructuredTargetExecutorLogger(Function<TargetDefinition<?>, List<String>> getPath) {
	    
	    Objects.requireNonNull(getPath);
	    
		this.log = new Log();
		this.paths = new HashMap<>();
	
		this.getPath = getPath;
	}
	
	private LogEntry addLogEntry(TargetDefinition<?> buildEntity, Object entityObj, String state, String message) {

		final List<String> path;
		
		if (buildEntity != null) {
			path = getPath.apply(buildEntity);
		
			path.add(String.format("%08x", System.identityHashCode(buildEntity)));
			
			if (entityObj != null) {
				path.add(String.format("%08x", System.identityHashCode(entityObj)));
			}
			
			if (state != null) {
				path.add(state);
			}
		}
		else {
			path = null;
		}
		
		final LogEntry logEntry = new LogEntry(
				path != null ? makePath(path) : null,
				message);
		
		if (log.getEntries() == null) {
			log.setEntries(new ArrayList<>());
		}
		
		log.getEntries().add(logEntry);
		
		return logEntry;
	}
	
	public Log makeLog() {
		return log;
	}

	private Integer makePath(List<String> list) {
		
		final LogPath logPath = new LogPath(list);
		
		if (log.getPaths() == null) {
			log.setPaths(new ArrayList<>());
		}
		
		Integer pathIndex = paths.get(logPath);
		
		if (pathIndex == null) {
			pathIndex = paths.size();
			
			paths.put(logPath, pathIndex);
			
			log.getPaths().add(logPath);
		}
		
		return pathIndex;
	}

	private void addTargetLogState(LogEntry logEntry, String dataType, Collection<TargetDefinition<?>> targets) {
		
		addCollectionLogState(
				logEntry,
				dataType,
				targets,
				getPath,
				TargetDefinition::getDebugString);
	}

	private <T> void addCollectionLogState(
			LogEntry logEntry,
			String dataType,
			Collection<T> data,
			Function<T, List<String>> toPath,
			Function<T, String> toString) {

		final List<LogDataEntry> dataEntries = data.stream()
				.map(dataEntry -> new LogDataEntry(makePath(toPath.apply(dataEntry)), toString.apply(dataEntry)))
				.collect(Collectors.toList());
		
		final LogData toExecute = new LogData(dataType, dataEntries);
		
		if (logEntry.getData() == null) {
			logEntry.setData(new ArrayList<>());
		}
		
		logEntry.getData().add(toExecute);
	}
	
	private void addTargetLogState(LogEntry logEntry, TargetExecutorLogState logState) {

		addTargetLogState(logEntry, "toExecute", logState.getToExecuteTargets());
		addTargetLogState(logEntry, "scheduled", logState.getScheduledTargets());
		addTargetLogState(logEntry, "collect", logState.getActionPerformedCollectTargets());
		addTargetLogState(logEntry, "completed", logState.getCompletedTargets());
		addTargetLogState(logEntry, "failed", logState.getFailedTargets().keySet());
		
	}
	
	
	@Override
	public void onScheduleTargets(int numScheduledJobs, TargetExecutorLogState logState) {
		
		final LogEntry logEntry = addLogEntry(null, null, null, "Schedule more targets numScheduledJobs=" + numScheduledJobs);

		addTargetLogState(logEntry, logState);
	}
	
	@Override
	public void onStateChange(TargetDefinition<?> target, String oldState, String newState) {
		
	}

	@Override
	public void onAddRecursiveTarget(TargetDefinition<?> target, TargetDefinition<?> subTarget) {
		
	}

	@Override
	public void onCheckRecursiveTargetsComplete(TargetDefinition<?> target, Status status) {
		
	}

	@Override
	public void onAddSubRecursionCollected(TargetDefinition<?> topOfRecursionTarget, TargetDefinition<?> target, CollectedTargetObjects subTargetObjects) {
		
	}

	@Override
	public void onAddTopRecursionCollected(TargetDefinition<?> aboveRecursionTarget, Prerequisites prerequisites, CollectedTargetObjects subTargetObjects) {
		
	}

	@Override
	public void onScheduleTarget(TargetDefinition<?> target, Status hasCompletedPrerequisites, TargetExecutorLogState logState) {

		final LogEntry logEntry = addLogEntry(
				target,
				target.getTargetObject(),
				logState.getTargetStatus(target).name(),
				"Schedule target " + target.getDebugString() + " with prerequisites status " + hasCompletedPrerequisites);

		addTargetLogState(logEntry, logState);
	}

	private String getCollectedLogString(Object collected) {
		
		final String string;
		
		if (collected instanceof CollectedObject) {
			final CollectedObject collectedObject = (CollectedObject)collected;
			
			string = collectedObject.getClass().getSimpleName() + " " + collectedObject.getName() + " " + collectedObject.getCollected();
		}
		else if (collected != null) {
			string = collected.toString();
		}
		else {
			string = "null";
		}
		
		return string;
	}
	
	
	@Override
	public void onCollectProducts(TargetDefinition<?> target, CollectedProducts subProducts, CollectedProduct collected,
			TargetExecutorLogState logState) {

		final LogEntry logEntry = addLogEntry(
				target,
				target.getTargetObject(),
				logState.getTargetStatus(target).name(),
				"Collected " + getCollectedLogString(collected.getProductObject()));

		addTargetLogState(logEntry, logState);
	}

	@Override
	public void onCollectTargetObjects(TargetDefinition<?> target, CollectedTargetObjects targetObjects,
			CollectedProduct collected, TargetExecutorLogState logState) {
		
		final LogEntry logEntry = addLogEntry(
				target,
				target.getTargetObject(),
				logState.getTargetStatus(target).name(),
				"Collected targets " + getCollectedLogString(collected.getProductObject()));

		addTargetLogState(logEntry, logState);
	}

	@Override
	public void onActionCompleted(TargetDefinition<?> target, TargetExecutorLogState logState, ActionLog actionLog) {
		
		final LogEntry logEntry = addLogEntry(
				target,
				target.getTargetObject(),
				logState.getTargetStatus(target).name(),
				"Executed action to build " + target.getDebugString()
			+ (actionLog != null ? ": " + actionLog.toString() : ""));

		addTargetLogState(logEntry, logState);
	}

	@Override
	public void onActionException(TargetDefinition<?> target, TargetExecutorLogState logState, Exception exception) {

		final LogEntry logEntry = addLogEntry(
				target,
				target.getTargetObject(),
				logState.getTargetStatus(target).name(),
				"Failed build action on " + target.getDebugString()
			+ " " + exception.getClass().getName());

		addTargetLogState(logEntry, logState);
	}

	@Override
	public void onTargetDone(TargetDefinition<?> target, Exception exception, TargetExecutorLogState logState) {
		
		final LogEntry logEntry = addLogEntry(
				target,
				target.getTargetObject(),
				logState.getTargetStatus(target).name(),
				(exception == null ? "Completed" : "Failed") 
					+ " target "
							+ (target.getTargetObject() != null ? target.getTargetObject().getClass().getSimpleName() + " " : "" )
							+ target.getDebugString());

		addTargetLogState(logEntry, logState);
	}
}
