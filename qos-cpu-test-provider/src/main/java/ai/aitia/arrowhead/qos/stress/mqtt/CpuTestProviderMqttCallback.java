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
 *  	AITIA
 *
 *******************************************************************************/

package ai.aitia.arrowhead.qos.stress.mqtt;

import java.util.concurrent.BlockingQueue;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.aitia.arrowhead.cpu.stress.StressConstants;
import ai.aitia.arrowhead.qos.stress.CpuTestProviderConstants;
import eu.arrowhead.dto.MqttRequestTemplate;
import jakarta.annotation.Resource;

@Service
public class CpuTestProviderMqttCallback implements MqttCallback {
	
	//=================================================================================================
	// members

	@Resource(name = "cpuStressRequestQueue")
	private BlockingQueue<MqttRequestTemplate> cpuStressRequestQueue;
	
	@Autowired
	private ObjectMapper mapper;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		if (!topic.equals(CpuTestProviderConstants.MQTT_SERVICE_OPERATION_BASE_TOPIC + StressConstants.OPERATION_PERFORM)) {
			System.out.println("Message received on an unhandled topic.: " + topic);
			
		} else {
			try {
				cpuStressRequestQueue.add(mapper.readValue(message.getPayload(), MqttRequestTemplate.class));				
			} catch (final Exception ex) {
				System.out.println("Message received, but it isn't a valid MqttRequestTemplate");
			}
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void connectionLost(Throwable cause) {
		
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	}

}
