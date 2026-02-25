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
package ai.aitia.arrowhead.qos.stress;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.cpu.stress.StressConstants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.model.SystemModel;
import eu.arrowhead.common.mqtt.model.MqttInterfaceModel;

@Component
public class CpuTestProviderSystemInfo extends SystemInfo {
	
	//=================================================================================================
	// members

	private SystemModel systemModel;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public SystemModel getSystemModel() {
		if (systemModel == null) {
			SystemModel.Builder builder = new SystemModel.Builder()
					.address(getAddress())
					.version("1.0.0");

			if (AuthenticationPolicy.CERTIFICATE == this.getAuthenticationPolicy()) {
				builder = builder.metadata(Constants.METADATA_KEY_X509_PUBLIC_KEY, getPublicKey());
			}

			systemModel = builder.build();
		}

		return systemModel;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public List<ServiceModel> getServices() {
		final ServiceModel stressCpu = new ServiceModel.Builder()
				.serviceDefinition(StressConstants.SERVICE_DEF_STRESS_CPU)
				.version("1.0.0")
				.serviceInterface(new MqttInterfaceModel.Builder(getSslProperties().isSslEnabled() ? Constants.GENERIC_MQTTS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_MQTT_INTERFACE_TEMPLATE_NAME, getMqttBrokerAddress(), getMqttBrokerPort())
						.baseTopic(CpuTestProviderConstants.MQTT_SERVICE_OPERATION_BASE_TOPIC)
						.operations(Set.of(StressConstants.OPERATION_PERFORM))
						.build())
				.build();
		return List.of(stressCpu);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit() {
		if (Utilities.isEmpty(getMqttBrokerAddress())) {
			throw new InvalidParameterException("MQTT Broker address is not defined");
		}

		if (getMqttBrokerPort() == null) {
			throw new InvalidParameterException("MQTT Broker port is not defined");
		}
	}
}
