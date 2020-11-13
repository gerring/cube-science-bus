package org.jax.cube.event.controller;

import java.util.Map;
import java.util.UUID;

import org.jax.cube.event.db.EngineRepository;
import org.jax.cube.event.db.InstanceRepository;
import org.jax.cube.event.domain.AnalysisEngineException;
import org.jax.cube.event.domain.Engine;
import org.jax.cube.event.domain.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

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
	private EngineRepository engines;
	
	@Autowired
	private InstanceRepository instances;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Get all engines registered
	 * @return a map of engineId to description text
	 * @throws Exception
	 */
	@GetMapping(path="/engines")
	public Flux<Engine> getEngines() throws Exception {
		return Flux.fromStream(engines.findAll().stream());
	}
	
	@GetMapping(path="/instances")
	public Flux<Instance>  getInstances() throws Exception {
		return Flux.fromStream(instances.findAll().stream());
	}

	@GetMapping(path="/instancesOfEngine")
	public Flux<Instance>  getInstances(UUID engineId) throws Exception {
		return Flux.fromStream(instances.findByEngineId(engineId).stream());
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
	@GetMapping(path="/subscribe")
	public <T> Flux<T> subscribe(UUID engineId) {
		return Flux.create(emitter->subscription(engineId, emitter));
	}

	private <T> void subscription(UUID engineId, FluxSink<T> emitter) {
//		UUID uuid = UUID.randomUUID();
//		Consumer<T> event = message->emitter.next(message);
//		try {
//			mservice.subscribe(engineId, uuid, event);
//		} catch (AnalysisEngineException e) {
//			emitter.error(e);
//		}
//		emitter.onDispose(()->mservice.unsubscribe(engineId, uuid));
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
	public <T> boolean execute(UUID engineId, T info) throws AnalysisEngineException {
		return false;
//		return mservice.run(engineId, info);
	}

}
