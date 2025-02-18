// This file was generated by Mendix Studio Pro.
//
// WARNING: Code you write here will be lost the next time you deploy the project.

package amazonsqsconnector.proxies;

public class Queue
{
	private final com.mendix.systemwideinterfaces.core.IMendixObject queueMendixObject;

	private final com.mendix.systemwideinterfaces.core.IContext context;

	/**
	 * Internal name of this entity
	 */
	public static final java.lang.String entityName = "AmazonSQSConnector.Queue";

	/**
	 * Enum describing members of this entity
	 */
	public enum MemberNames
	{
		Url("Url"),
		Queue_AwsConfig("AmazonSQSConnector.Queue_AwsConfig"),
		QueueAttributes_Queue("AmazonSQSConnector.QueueAttributes_Queue");

		private java.lang.String metaName;

		MemberNames(java.lang.String s)
		{
			metaName = s;
		}

		@java.lang.Override
		public java.lang.String toString()
		{
			return metaName;
		}
	}

	public Queue(com.mendix.systemwideinterfaces.core.IContext context)
	{
		this(context, com.mendix.core.Core.instantiate(context, "AmazonSQSConnector.Queue"));
	}

	protected Queue(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject queueMendixObject)
	{
		if (queueMendixObject == null)
			throw new java.lang.IllegalArgumentException("The given object cannot be null.");
		if (!com.mendix.core.Core.isSubClassOf("AmazonSQSConnector.Queue", queueMendixObject.getType()))
			throw new java.lang.IllegalArgumentException("The given object is not a AmazonSQSConnector.Queue");

		this.queueMendixObject = queueMendixObject;
		this.context = context;
	}

	/**
	 * @deprecated Use 'Queue.load(IContext, IMendixIdentifier)' instead.
	 */
	@java.lang.Deprecated
	public static amazonsqsconnector.proxies.Queue initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		return amazonsqsconnector.proxies.Queue.load(context, mendixIdentifier);
	}

	/**
	 * Initialize a proxy using context (recommended). This context will be used for security checking when the get- and set-methods without context parameters are called.
	 * The get- and set-methods with context parameter should be used when for instance sudo access is necessary (IContext.createSudoClone() can be used to obtain sudo access).
	 */
	public static amazonsqsconnector.proxies.Queue initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject mendixObject)
	{
		return new amazonsqsconnector.proxies.Queue(context, mendixObject);
	}

	public static amazonsqsconnector.proxies.Queue load(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		com.mendix.systemwideinterfaces.core.IMendixObject mendixObject = com.mendix.core.Core.retrieveId(context, mendixIdentifier);
		return amazonsqsconnector.proxies.Queue.initialize(context, mendixObject);
	}

	/**
	 * Commit the changes made on this proxy object.
	 */
	public final void commit() throws com.mendix.core.CoreException
	{
		com.mendix.core.Core.commit(context, getMendixObject());
	}

	/**
	 * Commit the changes made on this proxy object using the specified context.
	 */
	public final void commit(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		com.mendix.core.Core.commit(context, getMendixObject());
	}

	/**
	 * Delete the object.
	 */
	public final void delete()
	{
		com.mendix.core.Core.delete(context, getMendixObject());
	}

	/**
	 * Delete the object using the specified context.
	 */
	public final void delete(com.mendix.systemwideinterfaces.core.IContext context)
	{
		com.mendix.core.Core.delete(context, getMendixObject());
	}
	/**
	 * @return value of Url
	 */
	public final java.lang.String getUrl()
	{
		return getUrl(getContext());
	}

	/**
	 * @param context
	 * @return value of Url
	 */
	public final java.lang.String getUrl(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (java.lang.String) getMendixObject().getValue(context, MemberNames.Url.toString());
	}

	/**
	 * Set value of Url
	 * @param url
	 */
	public final void setUrl(java.lang.String url)
	{
		setUrl(getContext(), url);
	}

	/**
	 * Set value of Url
	 * @param context
	 * @param url
	 */
	public final void setUrl(com.mendix.systemwideinterfaces.core.IContext context, java.lang.String url)
	{
		getMendixObject().setValue(context, MemberNames.Url.toString(), url);
	}

	/**
	 * @return value of Queue_AwsConfig
	 */
	public final amazonsqsconnector.proxies.AwsConfig getQueue_AwsConfig() throws com.mendix.core.CoreException
	{
		return getQueue_AwsConfig(getContext());
	}

	/**
	 * @param context
	 * @return value of Queue_AwsConfig
	 */
	public final amazonsqsconnector.proxies.AwsConfig getQueue_AwsConfig(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		amazonsqsconnector.proxies.AwsConfig result = null;
		com.mendix.systemwideinterfaces.core.IMendixIdentifier identifier = getMendixObject().getValue(context, MemberNames.Queue_AwsConfig.toString());
		if (identifier != null)
			result = amazonsqsconnector.proxies.AwsConfig.load(context, identifier);
		return result;
	}

	/**
	 * Set value of Queue_AwsConfig
	 * @param queue_awsconfig
	 */
	public final void setQueue_AwsConfig(amazonsqsconnector.proxies.AwsConfig queue_awsconfig)
	{
		setQueue_AwsConfig(getContext(), queue_awsconfig);
	}

	/**
	 * Set value of Queue_AwsConfig
	 * @param context
	 * @param queue_awsconfig
	 */
	public final void setQueue_AwsConfig(com.mendix.systemwideinterfaces.core.IContext context, amazonsqsconnector.proxies.AwsConfig queue_awsconfig)
	{
		if (queue_awsconfig == null)
			getMendixObject().setValue(context, MemberNames.Queue_AwsConfig.toString(), null);
		else
			getMendixObject().setValue(context, MemberNames.Queue_AwsConfig.toString(), queue_awsconfig.getMendixObject().getId());
	}

	/**
	 * @return value of QueueAttributes_Queue
	 */
	public final amazonsqsconnector.proxies.QueueAttributes getQueueAttributes_Queue() throws com.mendix.core.CoreException
	{
		return getQueueAttributes_Queue(getContext());
	}

	/**
	 * @param context
	 * @return value of QueueAttributes_Queue
	 */
	public final amazonsqsconnector.proxies.QueueAttributes getQueueAttributes_Queue(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		amazonsqsconnector.proxies.QueueAttributes result = null;
		com.mendix.systemwideinterfaces.core.IMendixIdentifier identifier = getMendixObject().getValue(context, MemberNames.QueueAttributes_Queue.toString());
		if (identifier != null)
			result = amazonsqsconnector.proxies.QueueAttributes.load(context, identifier);
		return result;
	}

	/**
	 * Set value of QueueAttributes_Queue
	 * @param queueattributes_queue
	 */
	public final void setQueueAttributes_Queue(amazonsqsconnector.proxies.QueueAttributes queueattributes_queue)
	{
		setQueueAttributes_Queue(getContext(), queueattributes_queue);
	}

	/**
	 * Set value of QueueAttributes_Queue
	 * @param context
	 * @param queueattributes_queue
	 */
	public final void setQueueAttributes_Queue(com.mendix.systemwideinterfaces.core.IContext context, amazonsqsconnector.proxies.QueueAttributes queueattributes_queue)
	{
		if (queueattributes_queue == null)
			getMendixObject().setValue(context, MemberNames.QueueAttributes_Queue.toString(), null);
		else
			getMendixObject().setValue(context, MemberNames.QueueAttributes_Queue.toString(), queueattributes_queue.getMendixObject().getId());
	}

	/**
	 * @return the IMendixObject instance of this proxy for use in the Core interface.
	 */
	public final com.mendix.systemwideinterfaces.core.IMendixObject getMendixObject()
	{
		return queueMendixObject;
	}

	/**
	 * @return the IContext instance of this proxy, or null if no IContext instance was specified at initialization.
	 */
	public final com.mendix.systemwideinterfaces.core.IContext getContext()
	{
		return context;
	}

	@java.lang.Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (obj != null && getClass().equals(obj.getClass()))
		{
			final amazonsqsconnector.proxies.Queue that = (amazonsqsconnector.proxies.Queue) obj;
			return getMendixObject().equals(that.getMendixObject());
		}
		return false;
	}

	@java.lang.Override
	public int hashCode()
	{
		return getMendixObject().hashCode();
	}

	/**
	 * @return String name of this class
	 */
	public static java.lang.String getType()
	{
		return "AmazonSQSConnector.Queue";
	}

	/**
	 * @return String GUID from this object, format: ID_0000000000
	 * @deprecated Use getMendixObject().getId().toLong() to get a unique identifier for this object.
	 */
	@java.lang.Deprecated
	public java.lang.String getGUID()
	{
		return "ID_" + getMendixObject().getId().toLong();
	}
}
