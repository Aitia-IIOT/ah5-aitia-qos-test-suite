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

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.aitia.arrowhead.qos.test.dto.ResponseRecord;
import ai.aitia.arrowhead.qos.test.jpa.service.ExperimentDbService;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.mqtt.model.MqttMessageContainer;
import eu.arrowhead.dto.MqttResponseTemplate;
import jakarta.annotation.Resource;

@Component
public class ResponseHandlerThread extends Thread {

	//=================================================================================================
	// members

	@Resource(name = "mqttResponseQueue")
	private BlockingQueue<ResponseRecord> queue;

	@Autowired
	private ExperimentDbService dbService;
	
	@Autowired
	private ObjectMapper mapper;

	@Value("${test.timeout.threshold:15}")
	private long timeoutThreshold;
	
	private volatile boolean doWork = true;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		while (true) {
			try {
				final ResponseRecord response = queue.poll(100, TimeUnit.MILLISECONDS);
				if (response != null) {
					handleResponse(response);
				}
				
				if (!doWork && queue.isEmpty()) {
					return;
				}
			} catch (final Throwable t) {
				System.out.println(t.getMessage());
				t.printStackTrace();
			}
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public void stopGracefully() {
		this.doWork = false;
	}
 
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void handleResponse(final ResponseRecord response) {
		final MqttResponseTemplate responseTemplate = parseMqttMessage(response.container());
		dbService.closeExperimentStep(UUID.fromString(responseTemplate.payload().toString()), response.timestamp(), timeoutThreshold);
	}
	
	//-------------------------------------------------------------------------------------------------
	private MqttResponseTemplate parseMqttMessage(final MqttMessageContainer msgContainer) {
		if (msgContainer.getMessage() == null) {
			throw new InvalidParameterException("Invalid message template: null message");
		}

		try {
			return mapper.readValue(msgContainer.getMessage().getPayload(), MqttResponseTemplate.class);
		} catch (final IOException ex) {
			throw new InvalidParameterException("Invalid message template. Reason: " + ex.getMessage());
		}
	}
}