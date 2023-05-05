package raka.tunneling.server.client.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class ThreadPreservedExecutor {
	
	
	static ThreadPreservedExecutor instance;
	
	ExecutorService executor = Executors.newCachedThreadPool();

	ThreadLocal<ChainableTask> lastTaskOfTheThread = new ThreadLocal<>();
	
	private static synchronized ThreadPreservedExecutor createInstance() {
		if(instance == null) {
			instance = new ThreadPreservedExecutor();
		}
		return instance;
	}
	public static ThreadPreservedExecutor getInstance() {
		if(instance == null) {
			return createInstance();
		}
		return instance;
	}
	
	public void Execute(Runnable r) {
		ChainableTask lastTask = lastTaskOfTheThread.get();
		if(lastTask == null) {
			ChainableTask newTask = new ChainableTask(r);
			lastTaskOfTheThread.set(newTask);
			queueForExecution(newTask);
		}
		else {
			synchronized(lastTask) {
				if(lastTask.getThrowable() != null) {
					throw new TaskExecutionException(lastTask.getThrowable());
				}				
				ChainableTask newTask = new ChainableTask(r);
				lastTask.setNext(newTask);
				lastTaskOfTheThread.set(newTask);
				if(lastTask.isNextQueued()) {
					queueForExecution(newTask);
				}
			}
		}
	}
	
	private void queueForExecution(ChainableTask t) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				doExecute(t);
			}
		});
	}
	
	private void doExecute(ChainableTask task) {
		try {
			task.getTask().run();
			synchronized(task) {
				task.setExecuted(true);
				if(task.getNext() != null)
				{
					queueForExecution(task.getNext());
				}
				task.setNextQueued(true);
			}
		}
		catch(Throwable t) {			
			synchronized(task) {
				task.setThrowable(t);
				task.setExecuted(true);
				task.setNextQueued(true);
			}
		}
	}
}
