package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ActionWithResult;

public final class InfoTarget<TARGET> extends TargetDefinition<TARGET> {

	private final String name;
	private final Function<TARGET, String> qualifierName;

	private static String getLogIdentifier(String name, String qualifierName) {
		return name + (qualifierName != null ? "-" + qualifierName : "");
	}

	private static String getLogLocalIdentifier(String name) {
		return name;
	}

	private static <T> String getQualifierName(Function<T, String> qualifierName, T obj) {
		return qualifierName != null ? qualifierName.apply(obj) : null;
	}
	
	public InfoTarget(
			LogContext logContext,
			Class<TARGET> type,
			String name,
			Function<TARGET, String> qualifierName,
			Function<TARGET, String> description,
			TARGET targetObject,
			List<Prerequisites> prerequisites,
			Action<TARGET> action,
			ActionWithResult<TARGET> actionWithResult) {

		super(
				logContext,
				getLogIdentifier(name, getQualifierName(qualifierName, targetObject)),
				getLogLocalIdentifier(name),
				new TargetReference<>(logContext, type, targetObject, description, true),
				prerequisites,
				action,
				actionWithResult);
		
		Objects.requireNonNull(name);
		
		this.name = name;
		this.qualifierName = qualifierName;
	}
	
	@Override
	public <CONTEXT> TargetDefinition<TARGET> createTarget(
			LogContext logContext,
			CONTEXT context,
			TARGET target,
			List<Prerequisites> prerequisitesList) {

		return new InfoTarget<>(
				logContext,
				getType(),
				name,
				qualifierName,
				getDescriptionFunction(),
				target,
				prerequisitesList,
				getAction(),
				getActionWithResult());
	}

	@Override
	public String getLogIdentifier() {
		return getLogIdentifier(name, getQualifierName(qualifierName, getTargetObject()));
	}

	@Override
	public String getLogLocalIdentifier() {
		return getLogLocalIdentifier(name);
	}

	@Override
	public String getDebugString() {
		return getLogIdentifier();
	}

	String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		InfoTarget<?> other = (InfoTarget<?>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String targetSimpleLogString() {
		return name;
	}

	@Override
	public String targetToLogString() {
		return name;
	}

	@Override
	public String toString() {
		return "InfoTarget [name=" + name + ", description=" + getDescription() + "]";
	}
}
