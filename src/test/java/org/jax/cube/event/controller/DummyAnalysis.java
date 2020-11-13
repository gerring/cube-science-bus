package org.jax.cube.event.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.UUID;
import java.util.function.Consumer;

import org.jax.cube.event.domain.Engine;
import org.jax.cube.event.domain.Instance;
import org.jax.cube.event.mq.Publisher;
import org.jax.cube.event.mq.Subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is both a type of engine, returned by getConfig() 
 * and an instance of that Engine for running dummy Analysis.
 * 
 * @author gerrim
 *
 */
public class DummyAnalysis<S,R> {
	
	private ObjectMapper mapper = new ObjectMapper();

	private Engine config;
	private Instance instance;
	private Subscriber<S> submitter;
	private Publisher<R> responder;
	
	public DummyAnalysis(Class<S> sClass, Class<R> rClass) throws Exception {
		this(sClass.getConstructor().newInstance(), rClass.getConstructor().newInstance());
	}

	public DummyAnalysis(S testSubmitBean, R testResponseBean) throws IOException, URISyntaxException {
		
		config = new Engine();
		config.setId(UUID.randomUUID());
		config.setDescription("Test dummy compute engine");
		config.setRegistered(new Date(System.currentTimeMillis()));
		config.setResponseTemplate(mapper.writeValueAsString(testResponseBean));
		config.setSubmitTemplate(mapper.writeValueAsString(testSubmitBean));
		
		instance = new Instance();
		instance.setEngine(config);
		instance.setStarted(new Date(System.currentTimeMillis()));
		instance.setExpiry(new Date(System.currentTimeMillis()+120000));
		
		this.submitter = new Subscriber<>((Class<S>)testSubmitBean.getClass());
		instance.setSubmit(submitter.getUri());
		
		this.responder = new Publisher<>((Class<R>)testResponseBean.getClass());
		instance.setResponse(responder.getUri());
	}

	/**
	 * @return the config
	 */
	public Engine getEngine() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(Engine config) {
		this.config = config;
	}

	
	public void close() {
		submitter.close();
		responder.close();
	}

	public void send(R r) throws JsonMappingException, JsonProcessingException {
		responder.send(r);
	}
	
	public void setFakeRunner(Consumer<S> consumer) {
		submitter.setConsumer(consumer);
	}

	/**
	 * @return the submitter
	 */
	public Subscriber<S> getSubmitter() {
		return submitter;
	}

	/**
	 * @return the responder
	 */
	public Publisher<R> getResponder() {
		return responder;
	}

	/**
	 * @return the instance
	 */
	public Instance getInstance() {
		return instance;
	}
}
