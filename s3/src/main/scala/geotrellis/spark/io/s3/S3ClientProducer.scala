package geotrellis.spark.io.s3

import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.core.retry.backoff.FullJitterBackoffStrategy
import software.amazon.awssdk.core.retry.conditions.{OrRetryCondition, RetryCondition}
import software.amazon.awssdk.awscore.retry.conditions.RetryOnErrorCodeCondition
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.core.retry.RetryPolicy

import java.time.Duration

/**
  *  Singleton which can be used to customize the default S3 client used throughout geotrellis
  *
  *  Various classes are loaded based on URI patterns and because SPI requires
  *   parameterless constructors, it is necessary to register any customizations
  *   to the S3Client that should be applied by default here.
  *
  */
object S3ClientProducer {
  @transient
  private lazy val client = {
    val retryCondition =
      OrRetryCondition.create(
        RetryCondition.defaultRetryCondition(),
        RetryOnErrorCodeCondition.create("RequestTimeout")
      )
    val backoffStrategy =
      FullJitterBackoffStrategy.builder()
        .baseDelay(Duration.ofMillis(50))
        .maxBackoffTime(Duration.ofMillis(15))
        .build()
    val retryPolicy =
      RetryPolicy.defaultRetryPolicy()
        .toBuilder()
        .retryCondition(retryCondition)
        .backoffStrategy(backoffStrategy)
        .build()
    val overrideConfig =
      ClientOverrideConfiguration.builder()
        .retryPolicy(retryPolicy)
        .build()

    S3Client.builder()
      .overrideConfiguration(overrideConfig)
      .build()
  }

  private var summonClient: () => S3Client = {
    () => client
  }

  /**
    * Set an alternative default function for summoning S3Clients
    */
  def set(getClient: () => S3Client): Unit =
    summonClient = getClient

  /**
    * Get the current function registered as default for summong S3Clients
    */
  def get: () => S3Client =
    summonClient
}
