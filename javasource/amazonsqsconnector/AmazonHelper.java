package amazonsqsconnector;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;

import amazonsqsconnector.proxies.AwsConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.MessageSystemAttributeName;

public class AmazonHelper
{
	public final static ILogNode LOGGER = Core.getLogger("SQS");
	
	private static Map<String, SqsClient> cache = new HashMap<>();
	
	public static SqsClient getSqsClient(AwsConfig config) throws CoreException {
		
		if (cache.containsKey(config.getIdentifier())) {
			return cache.get(config.getIdentifier());
		} else {
			AwsBasicCredentials credentials = AwsBasicCredentials.create(
					config.getAccessKey(), config.getSecretKey());
			Region region = Region.of(config.getRegionName());
			SqsClient newClient = SqsClient.builder().credentialsProvider(
					StaticCredentialsProvider.create(credentials))
					.httpClientBuilder(ApacheHttpClient.builder())
					.region(region)
					.build();
			cache.put(config.getIdentifier(), newClient);
			return newClient;
		}
	}
	
	public static amazonsqsconnector.proxies.Message getMendixMessageFromSQSMessage(
			IContext context, Message awsMsg) throws CoreException {
		Map<MessageSystemAttributeName, String> sysAttrs = awsMsg.attributes();
		
		amazonsqsconnector.proxies.Message result = new amazonsqsconnector.proxies.Message(context);
		result.setMessageId(awsMsg.messageId());
		result.setReceiptHandle(awsMsg.receiptHandle());
		result.setMD5OfBody(awsMsg.md5OfBody());
		result.setBody(awsMsg.body());
		result.setSenderId(sysAttrs.get(MessageSystemAttributeName.SENDER_ID));
		
		if (sysAttrs.containsKey(MessageSystemAttributeName.SENT_TIMESTAMP)) {
			try {
				result.setSentTimestamp(new Date(Long.parseLong(
						sysAttrs.get(MessageSystemAttributeName.SENT_TIMESTAMP))));
			} catch (Exception e) {}
		}

		for (Entry<String, MessageAttributeValue> entry : awsMsg.messageAttributes().entrySet()) {
			amazonsqsconnector.proxies.MessageAttribute newAttribute = 
					new amazonsqsconnector.proxies.MessageAttribute(context);
			newAttribute.setKey(entry.getKey());
			newAttribute.setValue(entry.getValue().stringValue());
			newAttribute.setMessageAttribute_Message(result);
		}
		
		return result;
	}
	
	
}
