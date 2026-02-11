/*******************************************************************************
 *
 * Copyright (c) 2026 AITIA
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 *
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  	AITIA - implementation
 *  	Arrowhead Consortia - conceptualization
 *
 *******************************************************************************/
package ai.aitia.arrowhead.qos.test.experiment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.ConfigurationException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.cpu.stress.StressRequest;
import ai.aitia.arrowhead.qos.test.CpuTestConsumer;
import ai.aitia.arrowhead.qos.test.CpuTestConsumerSystemInfo;
import ai.aitia.arrowhead.qos.test.jpa.service.ExperimentDbService;
import ai.aitia.arrowhead.qos.test.mqtt.GeneralMqttClient;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InternalServerError;
import eu.arrowhead.common.http.ArrowheadHttpService;
import eu.arrowhead.common.mqtt.MqttQoS;
import eu.arrowhead.dto.MqttRequestTemplate;
import eu.arrowhead.dto.OrchestrationRequestDTO;
import eu.arrowhead.dto.OrchestrationResponseDTO;
import eu.arrowhead.dto.OrchestrationResultDTO;
import eu.arrowhead.dto.OrchestrationServiceRequirementDTO;
import eu.arrowhead.dto.QoSRequirementDTO;
import eu.arrowhead.dto.ServiceInstanceInterfaceResponseDTO;
import eu.arrowhead.dto.ServiceInstanceListResponseDTO;
import eu.arrowhead.dto.ServiceInstanceLookupRequestDTO;
import eu.arrowhead.dto.enums.OrchestrationFlag;

@Component
public class Experimenter {

	//=================================================================================================
	// members

	@Value("${test.comment:}")
	private String comment;

	@Value("${test.qos.enabled:false}")
	private boolean qos;

	@Value("${test.qos.metric.names:}")
	private List<String> qosMetricNames;

	@Value("${test.qos.metric.weights:}")
	private List<Double> qosMetricWeights;

	@Value("${test.qos.time.window:1}")
	private int qosTimeWindow;

	@Value("${test.iteration:10}")
	private int iteration;

	@Value("${test.wait:1000}")
	private long wait;

	@Value("${test.cpu.stress.power:20}")
	private int cpuStressPower;

	@Value("${test.cpu.stress.length:5000}")
	private long cpuStressLenth;

	@Value("${test.timeout.threshold:15000}")
	private long timeoutThreshold;

	@Autowired
	private CpuTestConsumerSystemInfo systemInfo;

	@Autowired
	private ArrowheadHttpService arrowheadHttpService;

	@Autowired
	private ExperimentDbService dbService;

	@Autowired
	private GeneralMqttClient mqttClient;

	@Autowired
	private ObjectMapper mapper;

	private boolean initialized = false;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void initialize() throws ConfigurationException {
		if (!initialized) {
			// configuration check
			if (qos && Utilities.isEmpty(qosMetricNames)) {
				throw new ConfigurationException("At least on metric name is needed");
			}

			// get the orchestration service from Service Registry
			final ServiceInstanceLookupRequestDTO payload = new ServiceInstanceLookupRequestDTO.Builder()
					.serviceDefinitionName(Constants.SERVICE_DEF_SERVICE_ORCHESTRATION)
					.providerName(Constants.SYS_NAME_DYNAMIC_SERVICE_ORCHESTRATION)
					.interfaceTemplateName(Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME)
					.build();
			final ServiceInstanceListResponseDTO response = arrowheadHttpService.consumeService(
					Constants.SERVICE_DEF_SERVICE_DISCOVERY,
					Constants.SERVICE_OP_LOOKUP,
					Constants.SYS_NAME_SERVICE_REGISTRY,
					ServiceInstanceListResponseDTO.class,
					payload);

			if (response == null || Utilities.isEmpty(response.entries())) {
				throw new ConfigurationException("DynamicServiceOrchestration system is not found");
			}

			initialized = true;
		}
	}

	//-------------------------------------------------------------------------------------------------
	public void performExperiment() throws InterruptedException {
		if (!initialized) {
			throw new InternalServerError("Experimenter is not initialized");
		}

		// initializing the experiment
		final String qosDetails = qos ? createQosDetailsString() : null;
		final UUID experimentId = UUID.randomUUID();
		systemInfo.setExperimentId(experimentId);
		dbService.startExperiment(
				experimentId,
				comment,
				qos,
				qosDetails,
				iteration,
				wait,
				cpuStressPower,
				cpuStressLenth,
				timeoutThreshold);

		for (int i = 0; i < iteration; ++i) {
			performStep();
			if (wait > 0) {
				Thread.sleep(wait);
			}
		}
		
		// waiting for a while (for the last responses)
		Thread.sleep(2 * timeoutThreshold);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private String createQosDetailsString() {
		return "names: " + qosMetricNames + " | weights: " + qosMetricWeights + " | window: " + qosTimeWindow;
	}

	//-------------------------------------------------------------------------------------------------
	private void performStep() {
		final OrchestrationRequestDTO.Builder orchRequestBuilder = new OrchestrationRequestDTO.Builder()
				.serviceRequirement(new OrchestrationServiceRequirementDTO.Builder()
						.serviceDefinition("stressCpu")
						.interfaceTemplateName(Constants.GENERIC_MQTT_INTERFACE_TEMPLATE_NAME)
						.build())
				.orchestrationFlag(OrchestrationFlag.MATCHMAKING.name(), true);

		if (qos) {
			final Map<String, Object> qosReqConfig = new HashMap<>(3);
			qosReqConfig.put("timeWindow", qosTimeWindow);
			qosReqConfig.put("metricNames", qosMetricNames);
			if (!Utilities.isEmpty(qosMetricWeights)) {
				qosReqConfig.put("metricWeights", qosMetricWeights);
			}

			orchRequestBuilder.qualityRequirements(new QoSRequirementDTO(
					"basic-device-kpi",
					"SORT",
					qosReqConfig));
		}

		final OrchestrationRequestDTO orchPayload = orchRequestBuilder.build();
		OrchestrationResponseDTO orchResponse = null;

		try {
			orchResponse = arrowheadHttpService.consumeService(
					Constants.SERVICE_DEF_SERVICE_ORCHESTRATION,
					Constants.SERVICE_OP_ORCHESTRATION_PULL,
					Constants.SYS_NAME_DYNAMIC_SERVICE_ORCHESTRATION,
					OrchestrationResponseDTO.class,
					orchPayload);
		} catch (final Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

		final String providerName = orchResponse == null || Utilities.isEmpty(orchResponse.results()) ? "NA" : orchResponse.results().get(0).providerName();
		final UUID stepId = UUID.randomUUID();
		dbService.addExperimentStep(systemInfo.getExperimentId(), stepId, providerName, Utilities.utcNow());

		if (orchResponse != null && !Utilities.isEmpty(orchResponse.results())) {
			final String topic = getTargetTopic(orchResponse.results().get(0));

			final MqttRequestTemplate template = new MqttRequestTemplate(
					stepId.toString(),
					null,
					CpuTestConsumer.MQTT_RESPONSE_TOPIC,
					MqttQoS.AT_MOST_ONCE.value(),
					Map.of(),
					new StressRequest(stepId.toString(), cpuStressPower, cpuStressLenth));

			try {
				final MqttMessage msg = new MqttMessage(mapper.writeValueAsBytes(template));
				mqttClient.publish(topic, msg);
			} catch (final Exception ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	//-------------------------------------------------------------------------------------------------
	private String getTargetTopic(final OrchestrationResultDTO orchestrationResultDTO) {
		final ServiceInstanceInterfaceResponseDTO intf = orchestrationResultDTO.interfaces().get(0);
		return intf.properties().get("baseTopic") + "perform";
	}
}