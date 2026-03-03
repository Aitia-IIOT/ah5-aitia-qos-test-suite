# Test Suite for DeviceQoSEvaluator Support System (Eclipse Arrowhead)

This test suite provides a tool for testing and demonstrating a load balancing strategy via service orchestration in an Eclipse Arrowhead Local Cloud environment.

The project contains a consumer and a provider application system. The consumer performs service orchestration requests with or without QoS requirements for a resource-intensive service and measures the elapsed time between the service request and the service response. As providers receive an increasing number of service requests, their service execution times increase accordingly. When multiple provider system instances are available, it is expected that orchestrations including QoS requirements will result in better service execution times compared to orchestrations without QoS constraints.

Check the docs of [DeviceQoSEvaluator](https://aitia-iiot.github.io/ah5-docs-java-spring/support_systems/device_qos_evaluator/).

## Provider

The provider system provides the **stressCpu** service via mqtt using the [generic_mqtt](https://aitia-iiot.github.io/ah5-docs-java-spring/api/communication-profiles/generic-mqtt-template/) communication profile. The service exposes a single  **perform** operation, which loads the CPU of the hosting device up to a specified percentage for a specified duration. If the current CPU load is greater than or equal to 90%, incoming service operation requests are queued and executed only when the load drops below 90%.

## Consumer

The consumer system runs a configurable session in which it performs multiple orchestrations for the **stressCpu** service, either with or without QoS requirements. The consumer always consumes the service from the provider system returned as the orchestration result. It also measures the service execution times, plus stores the session details and results in an SQLite database.

---

## How to run a test

### Prerequisites

#### Local Cloud

Deploy a local cloud containing the **ServiceRegistry, ConsumerAuthorization, DynamicServiceOrchestration** Core Systems and **DeviceQoSEvaluator** Support Sytem using the default [declared](https://aitia-iiot.github.io/ah5-docs-java-spring/api/authentication_policy/) authentication policy. 
Deploy [docker images](https://aitia-iiot.github.io/ah5-docs-java-spring/home/getting_started/docker/) or [download the executabels](https://aitia-iiot.github.io/ah5-docs-java-spring/home/getting_started/download_executables/)

#### MQTT Broker

The consumer and provider systems must connect to the same MQTT broker in order to being able to reach each other. [Eclipse Mosquitto](https://mosquitto.org/) is recommended.

Note: _MQTT does not need to be enabled in the Arrowhead Core or Support systems. Communication between the framework and the consumer/providers occurs via the default HTTP protocol._

#### Consumer

Download the exactuable from the latest [release](https://github.com/Aitia-IIOT/ah5-aitia-qos-test-suite/releases) to a device that has network access to the local cloud.
- Java 21 Runtime Environment is required.
- Customize the `application.properties` configuration file according to your environment.

#### Providers

Download the exactuable from the latest [release](https://github.com/Aitia-IIOT/ah5-aitia-qos-test-suite/releases) to multipe Linux-based devices that have network access to the local cloud. 
- Java 21 Runtime Environment is required
- [stress-ng](https://manpages.ubuntu.com/manpages/resolute/en/man1/stress-ng.1.html) tool is required.
- One Python3 based [device-agent](https://github.com/eclipse-arrowhead/ah5-device-qos-evaluator-java-spring/releases) is required to run on each device that hosts one or more provider.
- Customize the `application.properties` configuration file according to your environment.
  - Important: Each provider instance must have a unique system name.
- Start the providers using the `java -jar qos-cpu-test-provider-<version>.jar` command.

### Running a session

1) Configure the session parameters in the consumer's `application.properties` configuration file
   - Set the `test.comment` to distinguish the sessions easily. Example: "Expirement 1 with QoS".
   - Set the `test.qos.enabled` to enable or disable QoS requirement in the orchestration request.
   - Set the `test.qos.metric.names` and `test.qos.metric.weights` to specify the [QoS requiremnt metrics](https://aitia-iiot.github.io/ah5-docs-java-spring/api/primitives/#basicdevicekpimetric) (comma separated lists).
     - Mainly the CPU load related ones are targeted, but the others are also possible.
   - Set the `test.qos.time.window` to specify time window for QoS metric evaluation during orchestration.
   - Set the `test.iteration` to specify the number of orchestration and service consumption pairs within the session.
   - Set the `test.wait` to specify the waiting time between two orchestration and service consumption pairs.
   - Set the `test.cpu.stress.power` to specify the CPU load percentage applied during each service executions.
   - Set the `test.cpu.stress.length` to specify the duration of the CPU load during each service executions.
   - Set the `test.timeout.threshold` to specify a threshold that defines the maximum latency of a successful service consumption.
2) Start the consumer
   - Start the consumer using the `java -jar qos-cpu-test-consumer-<version>.jar` command.
3) Check the results
   - The results are strored in the `experiment.db` SQLite file.
     - The file is created automatically on the first run.
   - You will need a database management tool (like [DBeaver](https://dbeaver.io/) for example) to see the content.
