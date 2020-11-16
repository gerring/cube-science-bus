package org.jax.cube.event.controller;

import java.util.UUID;
import java.util.function.Consumer;

import org.jax.cube.event.db.ConfigurationRepository;
import org.jax.cube.event.db.InstanceRepository;
import org.jax.cube.event.domain.AnalysisEngineException;
import org.jax.cube.event.domain.Configuration;
import org.jax.cube.event.domain.Instance;
import org.jax.cube.event.service.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * 
 * One thing to note in this simple controller is that we keep the
 * JSON strings as strings. The type of the message is not known
 * by this server so the message must be String or JsonNode type.
 * 
 * @see https://www.baeldung.com/spring-webflux
 * @see https://www.baeldung.com/reactor-core
 * @author gerrim
 *
 */
@RestController()
public class CubeEventController {
	
	@Autowired
	private ConfigurationRepository confs;
	
	@Autowired
	private InstanceRepository instances;
	
	@Autowired
	private ExecutionService service;

	/**
	 * Get all engines registered
	 * @return a map of engineId to description text
	 * @throws Exception
	 */
	@GetMapping(path="/engines")
	public Flux<Configuration> getConfs() throws Exception {
		return Flux.fromStream(confs.findAll().stream());
	}
	
	@GetMapping(path="/instances")
	public Flux<Instance>  getInstances() throws Exception {
		return Flux.fromStream(instances.findAll().stream());
	}

	@GetMapping(path="/instancesOfEngine/{configId}")
	public Flux<Instance>  getInstances(@PathVariable Long configId) throws Exception {
		return Flux.fromStream(instances.findByConfigurationId(configId).stream());
	}

	/**
	 * Subscribe to an engine type. Exactly which instance of the engine
	 * you get is determined by the system. Of course individual engines
	 * themselves might be a network of ZeroMQ communicating devices.
	 * 
	 * It is a good idea to subscribe before running the engine.
	 * 
	 * @param <T>
	 * @param topic
	 * @return
	 */
	@GetMapping(path="/subscribe/{configId}")
	public <T> Flux<T> subscribe(@PathVariable Long engineId) {
		return Flux.create(emitter->subscription(engineId, emitter));
	}

	private <T> void subscription(Long engineId, FluxSink<T> emitter) {
		UUID uuid = UUID.randomUUID(); // Just a unique Id for this subscription
		Consumer<T> event = message->emitter.next(message);
		try {
			service.subscribe(engineId, uuid, event);
		} catch (AnalysisEngineException e) {
			emitter.error(e);
		}
		emitter.onDispose(()->service.unsubscribe(engineId, uuid));
	}

	/**
	 * Subscribe to an engine type. Exactly which instance of the engine
	 * you get is determined by the system. Of course individual engines
	 * themselves might be a nework of ZeroMQ communicating devices.
	 * 
	 * It is a good idea to subscribe before running the engine.
	 * 
	 * @param <T>
	 * @param topic
	 * @return
	 * @throws AnalysisEngineException 
	 */
	@GetMapping(path="/execute")
	public <T> boolean execute(Long engineId, T info) throws AnalysisEngineException {
		return false;
//		return mservice.run(engineId, info);
	}

}
