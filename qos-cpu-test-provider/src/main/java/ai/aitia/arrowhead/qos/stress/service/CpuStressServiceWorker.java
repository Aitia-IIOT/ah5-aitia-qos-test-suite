package ai.aitia.arrowhead.qos.stress.service;

import eu.arrowhead.dto.MqttRequestTemplate;

public class CpuStressServiceWorker implements Runnable {

	//=================================================================================================
	// members
	
	final MqttRequestTemplate request;

	//=================================================================================================
	// methods
	
	public CpuStressServiceWorker(final MqttRequestTemplate request) {
		this.request = request;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		System.out.println("Executing cpu stress with traceId: " + request.traceId());		
	}
}
