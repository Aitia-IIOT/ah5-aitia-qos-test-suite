package ai.aitia.arrowhead.qos.stress.service;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.aitia.arrowhead.cpu.stress.StressRequest;
import ai.aitia.arrowhead.cpu.stress.mqtt.GeneralMqttClient;
import eu.arrowhead.dto.MqttRequestTemplate;
import eu.arrowhead.dto.MqttResponseTemplate;

public class CpuStressServiceWorker implements Runnable {

	//=================================================================================================
	// members
	
	@Autowired
	private GeneralMqttClient mqttClient;
	
	@Autowired
	private ObjectMapper mapper;
	
	final MqttRequestTemplate request;

	//=================================================================================================
	// methods
	
	public CpuStressServiceWorker(final MqttRequestTemplate request) {
		this.request = request;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		try {
			final StressRequest stress = mapper.readValue(mapper.writeValueAsBytes(request.payload()), StressRequest.class);
			
			System.out.println("CPU stress start. ID: " + stress.uuid());
			final Process process = new ProcessBuilder("stress-ng", "--cpu", "0", "--cpu-load", String.valueOf(stress.power()), "--timeout", stress.length() + "s").start();
			int exitCode = process.waitFor();
			System.out.println("CPU stress End. ID: " + stress.uuid() + " Exit code: " + exitCode);
			
			mqttClient.publish(request.responseTopic(), new MqttMessage(mapper.writeValueAsBytes(new MqttResponseTemplate(200, request.traceId(), null, stress.uuid()))));
			
		} catch (final Exception ex) {
			System.out.println("Error occured in CpuStressServiceWorker.run()");
			ex.printStackTrace();
		}
	}
}
