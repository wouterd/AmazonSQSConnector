// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package parallelism.actions;

import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import parallelism.implementation.Parallelism;

public class StopBackgroundExecutions extends CustomJavaAction<java.lang.Boolean>
{
	private java.lang.Long gracefulShutdownDelay;

	public StopBackgroundExecutions(IContext context, java.lang.Long gracefulShutdownDelay)
	{
		super(context);
		this.gracefulShutdownDelay = gracefulShutdownDelay;
	}

	@java.lang.Override
	public java.lang.Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE
		Parallelism.shutdownThreads(gracefulShutdownDelay.intValue());
		return true;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "StopBackgroundExecutions";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
