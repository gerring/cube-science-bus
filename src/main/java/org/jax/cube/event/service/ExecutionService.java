package org.jax.cube.event.service;

import java.util.UUID;
import java.util.function.Consumer;

import org.jax.cube.event.domain.AnalysisEngineException;
import org.jax.cube.event.mq.Subscriber;
import org.springframework.stereotype.Service;

/**
 * A service used to wrap the messaging service which is 
 * likely to be Rabbit or ActiveMQ.
 * 
 * @author gerrim
 *
 */
@Service
public interface ExecutionService {

	/**
	 * Subscribe to an engine being run. The actual instance running the 
	 * run will be determined by the system checking load. A queueing or
	 * token system will be used depending on what is correct for the 
	 * given analysis requirements.
	 * 
	 * @param <T>
	 * @param engineId - Engine which we will run.
	 * @param uuid - Id of the listener we want to add.
	 * @param event - Consumer of the events we want to receive after running the run.
	 * @return id of the task running this analysis.
	 */
	 <T> Long subscribe(Long engineId, UUID uuid, Consumer<T> event) throws AnalysisEngineException;

	 /**
	  * Delete the listener of the run.
	  * @param instanceId - Id of the task running this analysis as returned by subscribe(...)
	  * @param uuid - Id of the listener which has subscribed for events.
	  */
	 <T> Subscriber<T> unsubscribe(Long instanceId, UUID uuid);
	

}
