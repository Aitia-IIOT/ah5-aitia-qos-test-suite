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

package ai.aitia.arrowhead.qos.test;

import java.util.UUID;

public final class CpuTestProviderConstants {
	
	public static final String MQTT_SERVICE_OPERATION_TOPIC = "provider/stress/cpu" + UUID.randomUUID().toString();

	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private CpuTestProviderConstants() {
		throw new UnsupportedOperationException();
	}
}
