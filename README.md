# Mendix Amazon SQS Connector (1.0)

This module provides access to SQS and provides actions for receiving and sending messages as well as configuration, exploration and monitoring.

## Features

- Based on the v2 API of Amazon (matching the Amazon S3 connector)
- Detailed library logging for troubleshooting through *Library Logging* module
- Runtime credential configuration
- Administration interface with explore functionality
- Operations to implement queue monitoring (e.g. queue depth and delay)
- Actions
  - Operation
    - Delete message
    - Receive message (single) 
    - Receive messages (multiple)
    - Send message
  - Administrative
    - Create a queue
    - Delete queue
    - Get queue attributes
    - List queues (lists which queues are visible <u>for your AWS account</u>)
    - Purge queue (empty the queue)

## Use cases

Depending on the situation and criticality of the system, one or more of the use cases below can apply.

### Receive and process messages in near real-time

Processing messages in real-time in an efficient manner in a horizontally scaled environment requires multiple background threads (per instance) to retrieve messages from the SQS queue. A scheduled event is not fit for this pattern. Instead the *Parallelism* module can be applied for this purpose. One can spin e.g. 4 threads per instance. If you scale your application horizontally, for availability, to e.g. 2 instances, this means there are 8 threads polling the SQS (and can process 8 messages at the same time). This makes effective use of the available resources when scaled horizontally.

An example of this pattern is made in the microflow `AmazonSQSConnector.PARALLEL_Poll_Messages`.

High level steps are:

- At your startup sequence, use the `Parallelism.ExecuteInBackground` action to spin up a certain amount of threads used for polling from the queue. Specify a delay between microflow executions (e.g. 5000).
- Within the microflow:
  - Retrieve a message from the SQS queue (preferably long polling using 20000 ms). If none was returned, let the microflow return `false`.
  - Process this message in a different transaction (to avoid possible duplicates apply a structure using a unique validation with a persistent entity based on a property of the message; e.g. `MessageId` or something functionally similar)
  - Delete the message from the SQS queue
  - Let the microflow return `true`.

### Publish to a queue

Wherever in your logic, use the `AmazonSQSConnector.SendMessage` to send a message to the queue.

### Monitoring your queue performance

For (performance) critical systems it can be valuable to monitor the depth of the queue, to make sure that:

- Your system is able to process incoming information in real-time
- Other systems are consuming your messages in time

The information can be obtained by reading the queue attributes. This information can be fed to a monitoring system or you can build your own alerting logic around it. 

#### Queue depth

In order to measure if the depth of your queue isn't growing, use the following approach:

- Use the `Get queue attributes` action to get an `AmazonSQSConnector.QueueAttributes` object.
- Use the `ApproximateNumberOfMessages*` attributes to determine your queue depth
- Define logic to act on it (e.g. send alerts, send to monitoring system)

#### Queue delay

When one retrieves messages from the queue, he will have one or more objects of `AmazonSQSConnector.Message`. This object contains the attribute `SentTimestamp` which can be compared to the current time. This will give you an estimation of delay the queue adds in your messaging architecture. 

## Configuration

Use the following steps to configure the module:

- Connect the `AmazonSQSConnector.AfterStartup` to your *After Startup* sequence after you've called `LibraryLogging.AfterStartup`.
- Connect the `AmazonSQSConnector.Administration` snippet to your application.
- Configure your AWS credentials in the snippet and test.

## Troubleshooting

If, for some reason, regular error handling doesn't provide sufficient information to solve your problem, you can obtain logging from the libraries used by setting the `AWS` lognode to a more detailed level like `Debug` or `Trace`.

## Dependencies

- Library logging module (for detailed troubleshooting)
- Parallelism module (for receiving messages in parallel)



