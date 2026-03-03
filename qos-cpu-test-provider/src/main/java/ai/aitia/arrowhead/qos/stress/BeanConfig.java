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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import ai.aitia.arrowhead.qos.stress.service.CpuStressServiceWorker;
import eu.arrowhead.dto.MqttRequestTemplate;

@Configuration
public class BeanConfig {
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@Bean("cpuStressRequestQueue")
	BlockingQueue<MqttRequestTemplate> getCpuStressRequestQueue() {
		return new LinkedBlockingQueue<>();
	}
	
	//-------------------------------------------------------------------------------------------------
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	CpuStressServiceWorker createCpuStressServiceWorker(final MqttRequestTemplate request) {
		return new CpuStressServiceWorker(request);
	}
	
	//-------------------------------------------------------------------------------------------------
	@Bean
	Function<MqttRequestTemplate, CpuStressServiceWorker> cpuStressServiceWorkerFactory() {
		return request -> createCpuStressServiceWorker(request);
	}
}