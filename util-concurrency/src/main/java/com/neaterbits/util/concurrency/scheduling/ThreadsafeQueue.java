package com.neaterbits.util.concurrency.scheduling;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ThreadsafeQueue<T> {

	private final Object lockObject;
	
	private final LinkedList<T> queue;

	public ThreadsafeQueue() {
		this.queue = new LinkedList<>();
		
		this.lockObject = new Object();
	}
	
	public void add(T item) {
		Objects.requireNonNull(item);
		
		add(queue -> item);
	}
	
	public void add(Function<List<T>, T> add) {
		
		Objects.requireNonNull(add);
	
		synchronized (lockObject) {
			
			final T toAdd = add.apply(queue);
			
			if (toAdd != null) {
				
				queue.add(toAdd);
				
				lockObject.notify();
			}
		}
	}
	
	public boolean isEmpty() {
		
		final boolean isEmpty;
		
		synchronized (lockObject) {
			isEmpty = queue.isEmpty();
		}
		
		return isEmpty;
	}
	
	public T take() {
		
		T state = null;
		
		for (;;) {
			
			synchronized (lockObject) {
				if (queue.isEmpty()) {
					try {
						lockObject.wait();
					} catch (InterruptedException e) {
					}
				}
				else {
					state = queue.removeFirst();
					
					break;
				}
			}
		}
		
		return state;
	}

	@Override
	public String toString() {
		
		final String s;
		
		synchronized (lockObject) {
			s = queue.toString();
		}
		
		return s;
	}
}
