package org.aivinog1.logging;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.ZeebeClientBuilder;
import io.zeebe.client.api.response.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

  private static final Logger LOGGER = LoggerFactory.getLogger("org.aivinog1.logging");

  public static void main(String[] args) {
    final String defaultAddress = "localhost:26500";
    final String envVarAddress = System.getenv("ZEEBE_ADDRESS");

    final ZeebeClientBuilder clientBuilder;
    final String contactPoint;
    if (envVarAddress != null) {
      /* Connect to Camunda Cloud Cluster, assumes that credentials are set in environment variables.
       * See JavaDoc on class level for details
       */
      contactPoint = envVarAddress;
      clientBuilder = ZeebeClient.newClientBuilder().gatewayAddress(envVarAddress);
    } else {
      // connect to local deployment; assumes that authentication is disabled
      contactPoint = defaultAddress;
      clientBuilder = ZeebeClient.newClientBuilder().gatewayAddress(defaultAddress).usePlaintext();
    }

    try (final ZeebeClient client = clientBuilder.build()) {
      LOGGER.info("Requesting topology with initial contact point " + contactPoint);

      final Topology topology = client.newTopologyRequest().send().join();

      LOGGER.info("Topology:");
      topology
          .getBrokers()
          .forEach(
              b -> {
                LOGGER.info("    " + b.getAddress());
                b.getPartitions()
                    .forEach(
                        p ->
                            LOGGER.info(
                                "      " + p.getPartitionId() + " - " + p.getRole()));
              });

      LOGGER.info("Done.");
    }
  }

}
