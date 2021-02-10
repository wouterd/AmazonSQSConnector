package system;

import com.mendix.core.actionmanagement.IActionRegistrator;

public class UserActionsRegistrar
{
  public void registerActions(IActionRegistrator registrator)
  {
    registrator.bundleComponentLoaded();
    registrator.registerUserAction(amazonsqsconnector.actions.CreateQueue.class);
    registrator.registerUserAction(amazonsqsconnector.actions.DeleteMessage.class);
    registrator.registerUserAction(amazonsqsconnector.actions.DeleteQueue.class);
    registrator.registerUserAction(amazonsqsconnector.actions.ExecuteMicroflowInSeparateTransaction.class);
    registrator.registerUserAction(amazonsqsconnector.actions.GetQueueAttributes.class);
    registrator.registerUserAction(amazonsqsconnector.actions.ListQueues.class);
    registrator.registerUserAction(amazonsqsconnector.actions.PurgeQueue.class);
    registrator.registerUserAction(amazonsqsconnector.actions.ReceiveMessage.class);
    registrator.registerUserAction(amazonsqsconnector.actions.ReceiveMessages.class);
    registrator.registerUserAction(amazonsqsconnector.actions.SendMessage.class);
    registrator.registerUserAction(amazonsqsconnector.actions.SyncRegions.class);
    registrator.registerUserAction(librarylogging.actions.AddLibraryLogListener.class);
    registrator.registerUserAction(librarylogging.actions.InitializeLogging.class);
    registrator.registerUserAction(librarylogging.actions.SetRootLevel.class);
    registrator.registerUserAction(parallelism.actions.ExecuteInBackground.class);
    registrator.registerUserAction(parallelism.actions.ExecuteMfAsync.class);
    registrator.registerUserAction(parallelism.actions.GetMfAsyncResult.class);
    registrator.registerUserAction(parallelism.actions.StopBackgroundExecutions.class);
    registrator.registerUserAction(system.actions.VerifyPassword.class);
  }
}
