package org.jutils.concurrency.scheduling.task;

import java.util.Objects;

public abstract class Task<T> {

	private final T data;

	public Task(T data) {
		
		Objects.requireNonNull(data);
		
		this.data = data;
	}

	final T getData() {
		return data;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task<?> other = (Task<?>) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
}
