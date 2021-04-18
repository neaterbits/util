package org.jutils.concurrency.scheduling.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jutils.concurrency.scheduling.ScheduleFunction;
import org.jutils.concurrency.scheduling.ScheduleListener;
import org.jutils.concurrency.scheduling.ThreadsafeQueue;

final class ThreadIOQueue<T, R> {

	enum State {
		IN_QUEUE,
		SCHEDULED
	};
	
	final class ScheduleState {
		
		private final T parameter;
		private final String debugName;
		private final ScheduleFunction<T, R> ioFunction;
		private final List<ScheduleListener<T, R>> listeners;

		private final long schedulingThread;
		
		private State state;
		
		ScheduleState(T parameter, String debugName, ScheduleFunction<T, R> ioFunction, ScheduleListener<T, R> listener, long schedulingThread) {
			
			Objects.requireNonNull(ioFunction);
			Objects.requireNonNull(listener);
			
			this.parameter = parameter;
			this.debugName = debugName;
			this.ioFunction = ioFunction;
			this.listeners = new ArrayList<>();
			this.schedulingThread = schedulingThread;
		
			listeners.add(listener);
			
			this.state = State.IN_QUEUE;
		}

		T getParameter() {
			return parameter;
		}

		String getDebugName() {
			return debugName;
		}

		ScheduleFunction<T, R> getIOFunction() {
			return ioFunction;
		}

		List<ScheduleListener<T, R>> getListeners() {
			return listeners;
		}

		long getSchedulingThread() {
			return schedulingThread;
		}

		public State getState() {
			return state;
		}

		void setState(State state) {
			this.state = state;
		}

		@Override
		public String toString() {
			return debugName;
		}
	}

	private final ThreadsafeQueue<ScheduleState> queue;
	
	ThreadIOQueue() {
		this.queue = new ThreadsafeQueue<>();
	}

	void add(T value, String debugName, ScheduleFunction<T, R> ioFunction, ScheduleListener<T, R> listener) {

		queue.add(queue -> {
			ScheduleState alreadyAdded = null;

			if (value != null) {
				for (ScheduleState scheduleState : queue) {
					if (scheduleState.parameter.equals(value)) {
						alreadyAdded = scheduleState;
						break;
					}
				}
			}
	
			final ScheduleState toAdd;
			
			if (alreadyAdded != null) {
				alreadyAdded.listeners.add(listener);
				
				toAdd = null;
			}
			else {
				toAdd = new ScheduleState(value, debugName, ioFunction, listener, Thread.currentThread().getId());
			}
			
			return toAdd;
		});
	}
	
	ScheduleState take() {
		
		final ScheduleState state = queue.take();
		
		state.state = State.SCHEDULED;

		return state;
	}

	@Override
	public String toString() {
		return queue.toString();
	}
}
