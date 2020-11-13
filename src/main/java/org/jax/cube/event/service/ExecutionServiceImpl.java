package org.jax.cube.event.service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.jax.cube.event.db.EngineRepository;
import org.jax.cube.event.db.InstanceRepository;
import org.jax.cube.event.domain.AnalysisEngineException;
import org.jax.cube.event.domain.Engine;
import org.jax.cube.event.domain.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	


	

}
