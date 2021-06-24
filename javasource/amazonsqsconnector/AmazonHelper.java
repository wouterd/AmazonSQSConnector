package amazonsqsconnector;

import amazonsqsconnector.proxies.AwsConfig;
import amazonsqsconnector.proxies.CognitoConfig;
import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.thirdparty.org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.*;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.MessageSystemAttributeName;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;

public class AmazonHelper {
    public final static ILogNode LOGGER = Core.getLogger("SQS");
    private static final Timer timer = new Timer(true);

    private static class SqsClientHolder {
        private final SqsClient client;
        private final Instant expires;

        private SqsClientHolder(SqsClient client, Instant expires) {
            this.client = client;
            this.expires = expires;
        }

        private SqsClientHolder(SqsClient client) {
            this.client = client;
            this.expires = null;
        }

        public SqsClient getClient() {
            return client;
        }

        public Optional<Instant> getExpires() {
            return Optional.ofNullable(expires);
        }
    }

    private static final Map<String, SqsClientHolder> cache = new HashMap<>();

    synchronized public static SqsClient getSqsClient(AwsConfig config) throws CoreException {

        if (cache.containsKey(config.getIdentifier())) {
            SqsClientHolder holder = cache.get(config.getIdentifier());
            final Boolean clientExpired = holder.getExpires()
                    .map(exp -> exp.isBefore(Instant.now()))
                    .orElse(false);
            if (!clientExpired) {
                return holder.getClient();
            }
        }

        SqsClientHolder newHolder = createNewSqsClient(config);
        final SqsClientHolder oldClient = cache.put(config.getIdentifier(), newHolder);
        if (oldClient != null && oldClient.getClient() != null) {
            // Clean up the connections for the old client after 5 minutes
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    oldClient.getClient().close();
                }
            }, Duration.ofMinutes(5).toMillis());
        }

        return newHolder.getClient();
    }

    private static SqsClientHolder createNewSqsClient(AwsConfig config) {
        if (config instanceof CognitoConfig) {
            CognitoConfig cfg = (CognitoConfig) config;

            var region = Region.of(cfg.getRegionName());
            var creds = getCredentialsThroughCognito(cfg.getUsername(), cfg.getPassword(),
                    cfg.getAccessKey(), cfg.getSecretKey(), cfg.getIdentityPoolId(), region);

            var awsCreds = AwsSessionCredentials.create(
                    creds.accessKeyId(), creds.secretKey(), creds.sessionToken());
            var client = createSqsClient(config, awsCreds);
            return new SqsClientHolder(client, creds.expiration().minus(Duration.ofMinutes(5)));
        } else {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey());
            SqsClient client = createSqsClient(config, credentials);
            return new SqsClientHolder(client);
        }
    }

    private static Credentials getCredentialsThroughCognito(String username, String password, String clientId,
                                                            String clientSecret, String identityPoolId, Region region) {
        var provider = CognitoIdentityProviderClient.builder()
                .region(region)
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .build();

        var secretHash = calculateSecretHash(clientId, clientSecret, username);

        var params = new HashMap<String, String>();
        params.put("USERNAME", username);
        params.put("PASSWORD", password);
        params.put("SECRET_HASH", secretHash);
        var req = InitiateAuthRequest.builder()
                .clientId(clientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(params)
                .build();

        final InitiateAuthResponse response = provider.initiateAuth(req);

        LOGGER.info("Successfully logged in using Cognito");

        var token = response.authenticationResult().idToken();
        var payload = token.split("\\.")[1];
        var decoded = Base64.getDecoder().decode(payload);
        var jwtSection = new String(decoded, StandardCharsets.UTF_8);
        var jwt = new JSONObject(jwtSection);

        var providerId = jwt.getString("iss").replace("https://", "");

        var identity = CognitoIdentityClient.builder()
                .region(region)
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .build();

        final Map<String, String> logins = Collections.singletonMap(providerId, token);
        var idReq = GetIdRequest.builder()
                .identityPoolId(identityPoolId)
                .logins(logins)
                .build();

        final GetIdResponse idResponse = identity.getId(idReq);
        final String identityId = idResponse.identityId();

        var credReq = GetCredentialsForIdentityRequest.builder()
                .identityId(identityId)
                .logins(logins)
                .build();

        final GetCredentialsForIdentityResponse credsResp = identity.getCredentialsForIdentity(credReq);

        LOGGER.info("Successfully acquired AWS credentials through Cognito");

        return credsResp.credentials();
    }

    private static SqsClient createSqsClient(AwsConfig config, AwsCredentials credentials) {
        return SqsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .httpClientBuilder(ApacheHttpClient.builder())
                .region(Region.of(config.getRegionName()))
                .build();
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
            } catch (Exception e) {
            }
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

    /**
     * Calculate the secret hash to be sent along with the authentication request.
     *
     * @param userPoolClientId     : The client id of the app.
     * @param userPoolClientSecret : The secret for the userpool client id.
     * @param userName             : The username of the user trying to authenticate.
     * @return Calculated secret hash.
     */
    private static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
}
