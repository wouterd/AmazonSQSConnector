package parallelism.implementation;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

public class Parallelism {

	static Map<String, BackgroundMicroflowThread> threads = Collections.synchronizedMap(new HashMap<String, BackgroundMicroflowThread>());
	private final static String FUTURES_KEY = "FUTURES";
	private static RepeatableThreadMonitor monitor;
	private static final Object LOCK = new Object();
	
	public static String addThread(BackgroundMicroflowThread thread) {
		String reference = UUID.randomUUID().toString();
		synchronized(LOCK) {
			threads.put(reference, thread);
			thread.start();
			
			if (monitor == null) {
				monitor = new RepeatableThreadMonitor();
				monitor.start();
			}
		}
		return reference;
	}
	
	public static void removeThread(String reference) {
		threads.remove(reference);
	}
	
	public static void waitForThread(String reference) {
		while (true) {
			if (!threads.containsKey(reference)) {
				return;
			}
			
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				
			}
		}
	}
	
	public static void shutdownThreads(int gracefulShutdownDelay) {
		for (BackgroundMicroflowThread t : threads.values()) {
			if (t.isAlive()) {
				t.setRunning(false);
			}
		}
		long startTime = System.currentTimeMillis();
		
		while (true) {
			if ((startTime + gracefulShutdownDelay) < System.currentTimeMillis() ) {
				break;
			}
			boolean isAnyRunning = false;
			for (BackgroundMicroflowThread t : threads.values()) {
				if (t.isAlive()) {
					isAnyRunning = true;
				}
			}
			
			if (!isAnyRunning) {
				break; // all threads gracefully shutdown
			}
			try { Thread.sleep(100); } catch (Exception e) {}
		}
		
		for (BackgroundMicroflowThread t : threads.values()) {
			if (t.isAlive()) {
				try { t.interrupt(); } catch (Exception e) {}
			}
		}
	}
	
	public static Map<String, Future<Object>> getFutures(IContext context) {
		synchronized (context) {
			if (context.getData().containsKey(FUTURES_KEY)) {
				Object result = context.getData().get(FUTURES_KEY);
				return (Map<String, Future<Object>>) result;
			} else {
				Map<String, Future<Object>> futures = new HashMap<>();
				context.getData().put(FUTURES_KEY, futures);
				return futures;
			}
		}
	}
	
	static class RepeatableThreadMonitor extends Thread {
		public void run() {
			Constants.LOGNODE.debug("Starting monitor of dead repeatable threads.");
			while (true) {
				try {
					sleep(1000);
				} catch (Exception e) {}
				
				List<String> toRestartThreads = new LinkedList<>();
				
				synchronized (LOCK) {
					for (Entry<String, BackgroundMicroflowThread> entry : threads.entrySet()) {
						BackgroundMicroflowThread thread = entry.getValue();
						if (thread.getRepeat() && thread.getIsRunning() && !thread.isAlive()) {
							Constants.LOGNODE.warn("Thread " + thread.getName() + " has died; it will be restarted...");
							toRestartThreads.add(entry.getKey());
						}
					}
				}
				
				for (String key : toRestartThreads) {
					BackgroundMicroflowThread deadThread = threads.get(key);
					BackgroundMicroflowThread newThread =
							new BackgroundMicroflowThread(
									deadThread.getMicroflow(),
									deadThread.getRepeat(),
									deadThread.getSleep(),
									deadThread.getSleepWhenFalse(),
									deadThread.getArgument()
									);
					removeThread(deadThread.getReference());
					addThread(newThread);
				}
				yield();
			}
		}
	}

}
