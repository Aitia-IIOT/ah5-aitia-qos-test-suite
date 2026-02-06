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
package ai.aitia.arrowhead.qos.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CpuTestConsumer implements CommandLineRunner {
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
        SpringApplication.run(CpuTestConsumer.class, args);
    }

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run(final String... args) throws Exception {
		System.out.println("Run the tests");
	}
}
