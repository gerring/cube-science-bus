package org.jax.cube.event.controller;

import java.sql.Date;
import java.util.function.Consumer;

import org.jax.cube.event.analysis.AbstractAnalysisEngine;
import org.jax.cube.event.domain.Configuration;
import org.jax.cube.event.domain.Configuration.ExecutionType;
import org.jax.cube.event.domain.Instance;
import org.jax.cube.event.mq.AbstractContextManager;

/**
 * This is both a type of engine, returned by getConfig() 
 * and an instance of that Engine for running dummy Analysis.
 * 
 * @author gerrim
 *
 */
public class DummyAnalysis<S,R> extends AbstractAnalysisEngine<S,R>{
	
	
	private Consumer<S> testRunner;

	public DummyAnalysis(Class<S> sClass, Class<R> rClass) throws Exception {
		super(sClass, rClass);
	}

	/**
	 * Creates a dummy config and then calls init(...) in the super class.
	 * @param testRunner
	 * @throws Exception
	 */
	public void init(Consumer<S> testRunner) throws Exception {
		
		this.testRunner = testRunner;
		
		S testSubmitBean = submitClass.getConstructor().newInstance();
		R testResponseBean = responseClass.getConstructor().newInstance();
		config = new Configuration();
		config.setDescription("Test dummy compute engine");
		config.setRegistered(new Date(System.currentTimeMillis()));
		config.setResponseTemplate(mapper.writeValueAsString(testResponseBean));
		config.setSubmitTemplate(mapper.writeValueAsString(testSubmitBean));
		config.setExecutionType(ExecutionType.RANDOM);
		
		instance = new Instance();
		instance.setStarted(new Date(System.currentTimeMillis()));
		instance.setExpiry(new Date(System.currentTimeMillis()+120000));
		
		instance.setSubmit(AbstractContextManager.createRandomFreeUri());
		instance.setResponse(AbstractContextManager.createRandomFreeUri());
		instance.setActive(AbstractContextManager.createRandomFreeUri());
		
		// Wire them
		instance.setConfiguration(config);
		
		super.init(config, instance);
	}

	@Override
	protected Consumer<S> createSubmitter() {
		return testRunner;
	}

	public void trigger() throws Exception {
		testRunner.accept(submitClass.getConstructor().newInstance());
	}
}
