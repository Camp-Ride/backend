package com.richjun.campride.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class CloudWatchLogsService {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    private CloudWatchLogsClient cloudWatchLogsClient;
    private final String LOG_GROUP = "/spring-boot/api-monitoring";
    private final String LOG_STREAM = "api-logs";
    private String sequenceToken;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        // AWS 자격증명 생성
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        // CloudWatch 클라이언트 생성
        cloudWatchLogsClient = CloudWatchLogsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        try {
            createLogGroupAndStream();
        } catch (Exception e) {
            log.error("Error initializing CloudWatch Logs", e);
        }
    }

    private void createLogGroupAndStream() {
        // Create log group if not exists
        try {
            cloudWatchLogsClient.createLogGroup(CreateLogGroupRequest.builder()
                    .logGroupName(LOG_GROUP)
                    .build());
            log.info("Created CloudWatch log group: {}", LOG_GROUP);
        } catch (ResourceAlreadyExistsException e) {
            log.info("CloudWatch log group already exists: {}", LOG_GROUP);
        }

        // Create log stream
        try {
            cloudWatchLogsClient.createLogStream(CreateLogStreamRequest.builder()
                    .logGroupName(LOG_GROUP)
                    .logStreamName(LOG_STREAM)
                    .build());
            log.info("Created CloudWatch log stream: {}", LOG_STREAM);
        } catch (Exception e) {
            log.error("Error creating CloudWatch log stream", e);
        }
    }

    public void sendLog(String message) {
        executor.submit(() -> {
            try {
                PutLogEventsRequest request = PutLogEventsRequest.builder()
                        .logGroupName(LOG_GROUP)
                        .logStreamName(LOG_STREAM)
                        .sequenceToken(sequenceToken)
                        .logEvents(List.of(
                                InputLogEvent.builder()
                                        .timestamp(System.currentTimeMillis())
                                        .message(message)
                                        .build()
                        ))
                        .build();

                PutLogEventsResponse response = cloudWatchLogsClient.putLogEvents(request);
                sequenceToken = response.nextSequenceToken();
                log.debug("Successfully sent log to CloudWatch");

            } catch (Exception e) {
                log.error("Error sending log to CloudWatch", e);
            }
        });
    }
}