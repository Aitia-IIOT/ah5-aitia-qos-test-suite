package ai.aitia.arrowhead.qos.stress.service;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
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
		System.out.println("Executing cpu stress with traceId: " + request.traceId());
		
		try {
			final StressRequest stress = mapper.readValue(mapper.writeValueAsBytes(request.payload()), StressRequest.class);
			
			Thread.sleep(2000);
			
			mqttClient.publish(request.responseTopic(), new MqttMessage(mapper.writeValueAsBytes(new MqttResponseTemplate(200, request.traceId(), null, stress.uuid()))));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
