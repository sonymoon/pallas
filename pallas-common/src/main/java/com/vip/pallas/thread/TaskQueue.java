/**
 * Copyright 2019 vip.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.vip.pallas.thread;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * As task queue specifically designed to run with a thread pool executor. The task queue is optimised to properly
 * utilize threads within a thread pool executor. If you use a normal queue, the executor will spawn threads when there
 * are idle threads and you wont be able to force items onto the queue itself.
 */
public class TaskQueue extends LinkedBlockingQueue<Runnable> {

	private static final long serialVersionUID = -8492534811742453555L;

	private volatile ExtendableThreadPoolExecutor parent;

	// No need to be volatile. This is written and read in a single thread
	// (when stopping a context and firing the listeners)
	private Integer forcedRemainingCapacity;

	public TaskQueue() {
		super();
	}

	public TaskQueue(int capacity) {
		super(capacity);
	}

	public TaskQueue(Collection<? extends Runnable> c) {
		super(c);
	}

	public void setParent(ExtendableThreadPoolExecutor tp) {
		parent = tp;
	}

	public boolean force(Runnable o) {
		if (parent == null || parent.isShutdown()) {
			throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
		}
		return super.offer(o); // forces the item onto the queue, to be used if the task is rejected
	}

	public boolean force(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
		if (parent == null || parent.isShutdown()) {
			throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
		}
		return super.offer(o, timeout, unit); // forces the item onto the queue, to be used if the task is rejected
	}

	@Override
	public synchronized boolean offer(Runnable o) { // NOSONAR
		// we can't do any checks
		if (parent == null) {
			return super.offer(o);
		}
		// we are maxed out on threads, simply queue the object
		if (parent.getPoolSize() == parent.getMaximumPoolSize()) {
			return super.offer(o);
		}
		// we have idle threads, just add it to the queue
		if (parent.getSubmittedCount() < (parent.getPoolSize())) {
			return super.offer(o);
		}
		// if we have less threads than maximum force creation of a new thread
		if (parent.getPoolSize() < parent.getMaximumPoolSize()) {
			return false;
		}
		// if we reached here, we need to add it to the queue
		return super.offer(o);
	}

	@Override
	public int remainingCapacity() {
		if (forcedRemainingCapacity != null) {
			// ThreadPoolExecutor.setCorePoolSize checks that
			// remainingCapacity==0 to allow to interrupt idle threads
			// I don't see why, but this hack allows to conform to this
			// "requirement"
			return forcedRemainingCapacity.intValue();
		}
		return super.remainingCapacity();
	}

	public void setForcedRemainingCapacity(Integer forcedRemainingCapacity) {
		this.forcedRemainingCapacity = forcedRemainingCapacity;
	}

}