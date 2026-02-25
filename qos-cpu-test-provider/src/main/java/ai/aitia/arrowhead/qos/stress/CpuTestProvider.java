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

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import ai.aitia.arrowhead.Constants;
import eu.arrowhead.common.security.DefaultSecurityConfig;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(
		basePackages = { Constants.BASE_PACKAGE, Constants.COMMON_BASE_PACKAGE }, 
		excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { DefaultSecurityConfig.class }) })
public class CpuTestProvider {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		new SpringApplicationBuilder(CpuTestProvider.class).web(WebApplicationType.NONE).run(args);
	}
	
	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	protected CpuTestProvider() {
	}

}
