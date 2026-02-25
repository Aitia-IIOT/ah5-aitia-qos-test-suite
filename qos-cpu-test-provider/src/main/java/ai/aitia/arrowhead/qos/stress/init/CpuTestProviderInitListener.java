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
package ai.aitia.arrowhead.qos.stress.init;

import javax.naming.ConfigurationException;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.cpu.stress.StressConstants;
import ai.aitia.arrowhead.cpu.stress.mqtt.GeneralMqttClient;
import ai.aitia.arrowhead.qos.stress.CpuTestProviderConstants;
import ai.aitia.arrowhead.qos.stress.mqtt.CpuTestProviderMqttCallback;
import eu.arrowhead.common.init.ApplicationInitListener;
import eu.arrowhead.dto.AuthorizationGrantRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyResponseDTO;

@Component
public class CpuTestProviderInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	
	@Autowired
	private GeneralMqttClient mqttClient;
	
	@Autowired
	private CpuTestProviderMqttCallback mqttCallback;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) throws InterruptedException, ConfigurationException {
		connectToMqttBroker();
		defineAuthorizationRules();
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customDestroy() {
		try {
			if (mqttClient != null) {
				mqttClient.unsubscribe(CpuTestProviderConstants.MQTT_SERVICE_OPERATION_BASE_TOPIC);
				mqttClient.destroy();
			}
			
			// TODO stop handler
		} catch (final MqttException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void connectToMqttBroker() throws ConfigurationException {
		try {
			mqttClient.initialize(mqttCallback);
			mqttClient.subscribe(CpuTestProviderConstants.MQTT_SERVICE_OPERATION_BASE_TOPIC);
		} catch (final MqttException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			throw new ConfigurationException("Can't access the MQTT broker: " + ex.getMessage());
		}
		
		// TODO start handler
	}
	
	//-------------------------------------------------------------------------------------------------
	private void defineAuthorizationRules() {
		final AuthorizationGrantRequestDTO payload = new AuthorizationGrantRequestDTO(
				"LOCAL",
				"SERVICE_DEF",
				StressConstants.SERVICE_DEF_STRESS_CPU,
				"can be used by every system in the local cloud",
				new AuthorizationPolicyRequestDTO("ALL", null, null),
				null);

		try {
			arrowheadHttpService.consumeService(
					Constants.SERVICE_DEF_AUTHORIZATION,
					Constants.SERVICE_OP_GRANT,
					AuthorizationPolicyResponseDTO.class,
					payload);
		} catch (final Exception ex) {
			System.out.println("Could not create authorization rules");
			ex.printStackTrace();
		}
	}
}
