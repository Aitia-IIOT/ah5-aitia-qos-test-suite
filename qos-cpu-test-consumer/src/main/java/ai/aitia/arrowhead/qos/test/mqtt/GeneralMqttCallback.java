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
package ai.aitia.arrowhead.qos.test.mqtt;

import java.util.concurrent.BlockingQueue;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import ai.aitia.arrowhead.qos.test.dto.ResponseRecord;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.mqtt.model.MqttMessageContainer;
import jakarta.annotation.Resource;

@Service
public class GeneralMqttCallback implements MqttCallback {

	//=================================================================================================
	// members

	@Resource(name = "mqttResponseQueue")
	private BlockingQueue<ResponseRecord> queue;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void messageArrived(final String topic, final MqttMessage message) throws Exception {
		queue.add(new ResponseRecord(Utilities.utcNow(), new MqttMessageContainer(topic, message)));
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void deliveryComplete(final IMqttDeliveryToken token) {
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void connectionLost(final Throwable cause) {
	}
}