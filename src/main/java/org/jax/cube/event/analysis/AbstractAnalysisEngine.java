package org.jax.cube.event.analysis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import org.jax.cube.event.domain.Configuration;
import org.jax.cube.event.domain.Instance;
import org.jax.cube.event.mq.Publisher;
import org.jax.cube.event.mq.Subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractAnalysisEngine<S,R> {
	
	protected ObjectMapper mapper = new ObjectMapper();

	protected Configuration config;
	protected Instance instance;
	protected Subscriber<S> submitter;
	protected Publisher<R> responder;

	protected Class<S> submitClass;
	protected Class<R> responseClass;


	public AbstractAnalysisEngine(Class<S> sClass, Class<R> rClass) {
		this.submitClass = sClass;
		this.responseClass = rClass;
	}

	/**
	 * Subclasses may override to 
	 * @param conf
	 * @param instance
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void init(Configuration conf, Instance instance) throws IOException, URISyntaxException {
		this.config = conf;
		this.instance = instance;
		this.submitter = new Subscriber<>(createSubmitter(), submitClass, instance.getSubmit());
		this.responder = new Publisher<>(responseClass, instance.getResponse());
	}

	/**
	 * Called when someone wants to submit something to this analysis from
	 * zeromq. Implement to run the code.
	 * @return
	 */
	protected abstract Consumer<S> createSubmitter();
	

	public void send(R r) throws JsonMappingException, JsonProcessingException {
		responder.send(r);
	}

	/**
	 * @return the config
	 */
	public Configuration getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(Configuration config) {
		this.config = config;
	}

	
	public void close() {
		submitter.close();
		responder.close();
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
