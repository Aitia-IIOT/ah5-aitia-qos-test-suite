package ai.aitia.arrowhead.qos.stress.service;

import java.lang.management.ManagementFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.management.OperatingSystemMXBean;

import eu.arrowhead.dto.MqttRequestTemplate;
import jakarta.annotation.Resource;

@Service
public class CpuStressServiceManager extends Thread {

	//=================================================================================================
	// members
	
	@Resource(name = "cpuStressRequestQueue")
	private BlockingQueue<MqttRequestTemplate> queue;
	
	@Autowired
	private Function<MqttRequestTemplate, CpuStressServiceWorker> workerFactory;
	
	private final ExecutorService executor = Executors.newCachedThreadPool();
	
	private final OperatingSystemMXBean opSys = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	
	private volatile boolean doWork = true;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		while (doWork) {
			try {
				final MqttRequestTemplate request = queue.take();
				
				while (doWork && opSys.getCpuLoad() >= 0.9) {
					Thread.sleep(1000);
				}
				executor.execute(workerFactory.apply(request));
				
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
}
