package parallelism.implementation;

import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

public class BackgroundMicroflowThread extends Thread {
	private String microflow, reference;
	private Boolean repeat, sleepWhenFalse, isRunning;
	private Long sleep;
	private IMendixObject argument;
	
	public BackgroundMicroflowThread(String microflow, Boolean repeat, Long sleep, Boolean sleepWhenFalse, IMendixObject argument) {
		this.microflow = microflow;
		this.repeat = repeat;
		this.sleep = sleep;
		this.sleepWhenFalse = sleepWhenFalse;
		this.argument = argument;
	}
	
	public void run() {
		isRunning = true;
		this.setName(this.getClass().getSimpleName() + " - " + microflow);
		do {
			IContext context = Core.createSystemContext();
			try {
				context.startTransaction();
				
				Object result = Core.execute(context, microflow, new Object[0]);
				endTransaction(context);
				
				if (result instanceof Boolean && !(Boolean)result && sleepWhenFalse) {
					sleep(sleep);
				}
				
			} catch (Throwable t) {
				Constants.LOGNODE.error("An uncatched throwable has occurred when executing " + microflow + ": " + t.getMessage(), t);
				try {sleep(30000); } catch (Exception e) {};
			} finally {
				endTransaction(context);
			}
			yield();
		} while (repeat && isRunning);
		isRunning = false;
		Parallelism.removeThread(reference);
	}
	
	private void endTransaction(IContext context) {
		try {
			while (context.isInTransaction()) {
				context.endTransaction();
			}
		} catch (Throwable t) {
			Constants.LOGNODE.error("Error while ending transaction: " + t.getMessage(), t);
		}
	}
	
	public IMendixObject getArgument() {
		return argument;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	public void setReference(String reference) {
		this.reference = reference;
	}

	public Boolean getRepeat() {
		return repeat;
	}

	public Boolean getIsRunning() {
		return isRunning;
	}

	public String getMicroflow() {
		return microflow;
	}

	public String getReference() {
		return reference;
	}

	public Boolean getSleepWhenFalse() {
		return sleepWhenFalse;
	}

	public Long getSleep() {
		return sleep;
	}
	
	
}
