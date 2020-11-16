package org.jax.cube.event.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jax.cube.event.db.ConfigurationRepository;
import org.jax.cube.event.db.InstanceRepository;
import org.jax.cube.event.domain.AnalysisEngineException;
import org.jax.cube.event.domain.Configuration;
import org.jax.cube.event.domain.Configuration.ExecutionType;
import org.jax.cube.event.domain.Instance;
import org.jax.cube.event.mq.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * A messaging service which links to AMQ for its publish/subscribe
 * messages.
 * @see https://stackoverflow.com/questions/43126775/how-to-create-a-spring-reactor-flux-from-a-activemq-queue
 * @see https://www.baeldung.com/spring-jms
 * @see https://codenotfound.com/spring-jms-listener-example.html
 * 
 * @author gerrim
 *
 */
@Component
public class ExecutionServiceImpl implements ExecutionService {
	
	@Autowired
	private ConfigurationRepository confs;

	@Autowired
	private InstanceRepository instances;

	private final Map<Configuration.ExecutionType, Function<Configuration, Instance>> instanceFinderMap;
	public ExecutionServiceImpl() {
		Map<Configuration.ExecutionType, Function<Configuration, Instance>> tmp = new HashMap<>();
		
		// Do these with 0MQ queue
		tmp.put(ExecutionType.QUEUE, eng->{throw new IllegalArgumentException(eng.getExecutionType()+" not implemented!");});
		tmp.put(ExecutionType.LOAD_BALANCED, eng->{throw new IllegalArgumentException(eng.getExecutionType()+" not implemented!");});
		
		// These can be done with simple lambda.
		tmp.put(ExecutionType.ONE, eng->instances.findByConfigurationId(eng.getId()).iterator().next());
		tmp.put(ExecutionType.RANDOM, eng->{
			List<Instance> running = instances.findByConfigurationId(eng.getId());
			Random rand = new Random();
			return running.get(rand.nextInt(running.size()));
		});
		
		instanceFinderMap = tmp;
	}

	// THIS BREAKS STATELESS A BROKER WOULD NOT NEED THIS!
	private static final Map<UUID, Subscriber> runs = Collections.synchronizedMap(new HashMap<>());
	
	@Override
	public <T> Long subscribe(Long engineId, UUID uuid, Consumer<T> consumer) throws AnalysisEngineException {
		Configuration conf = confs.getOne(engineId);
		if (conf==null) throw new AnalysisEngineException("No configuration with the id "+engineId+" can be found!");
	
		Function<Configuration, Instance> balancer = instanceFinderMap.get(conf.getExecutionType());
		Instance runner = balancer.apply(conf);
		
		try {
			Subscriber<T> subscriber = new Subscriber<>(consumer, new TypeReference<T>(){}, runner.getResponse());
			
			// If this process stops then the zeromq connection to the running analysis is lost.
			// This might be a reason to go to activemq or rabbit where the connections are held in a broker.
			// Therefore load balancing etc. can be done on the web server because data is not held.
			// WARNING NOT STATELESS!!
			runs.put(uuid, subscriber);
			
		} catch (IOException | URISyntaxException e) {
			throw new AnalysisEngineException(e);
		}
		
		return runner.getId();
	}

	@Override
	public <T> Subscriber<T> unsubscribe(Long instanceId, UUID uuid) {
		Subscriber<T> subscriber = runs.remove(uuid);
		if (subscriber!=null) {
			subscriber.close();
		}
		return subscriber;
	}

}
