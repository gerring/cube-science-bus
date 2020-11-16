package org.jax.cube.event.domain;

import java.sql.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



/**
 * Rows of the Engine table for analysis engines.
 * 
 * NOTE the @column annotation is not required in this class
 * as the columns match the field names but we have used it anyway.
 * 
 * @see http://lorenzo-dee.blogspot.com/2016/07/reference-by-identity-in-jpa.html
 * 
 * @author gerrim
 *
 */
@Entity
public class Configuration {
	
	public enum ExecutionType {
		/**
		 * Submit to a queues. 0MQ supports queues through an intermediary.
		 * Only engines already registered with the intermediary will be able to take.
		 * items from the queue and do the run. The intermediary can be on the web server machine 
		 * and might need to store its queue state to a table.
		 * 
		 * @see https://learning-0mq-with-pyzmq.readthedocs.io/en/latest/pyzmq/devices/queue.html
		 */
		QUEUE, 
		
		/**
		 * A random engine is selected to run the analysis.
		 * This engine must be started and waiting to receive the submission.
		 */
		RANDOM, 
		
		/**
		 * The analysis represents an engine with only one entry point.
		 * A topic may be used to determine which results come from which 
		 * analysis run.
		 */
		ONE, 
		
		/**
		 * Rather than a queue the lowest loaded analysis engine is found and
		 * the job is executed on that.
		 */
		LOAD_BALANCED;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String description;
	
	/**
	 * An example of what the submit JSON should be.
	 */
	private String submitTemplate;
	/**
	 * The template which we will expect the results to be in.
	 */
	private String responseTemplate;

	/**
	 * The registration of the 
	 */
	private Date registered;
	
	/**
	 * How we run jobs with this engine.
	 */
	private ExecutionType executionType;
	
	/**
	 * No args for reflection/bean
	 */
	public Configuration() {
		
	}

	/**
	 * @param id
	 * @param description
	 * @param submitTemplate
	 * @param responseTemplate
	 * @param registered
	 */
	public Configuration(String description, String submitTemplate, String responseTemplate, Date registered) {
		super();
		this.description = description;
		this.submitTemplate = submitTemplate;
		this.responseTemplate = responseTemplate;
		this.registered = registered;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the submitTemplate
	 */
	public Object getSubmitTemplate() {
		return submitTemplate;
	}

	/**
	 * @param submitTemplate the submitTemplate to set
	 */
	public void setSubmitTemplate(String submitTemplate) {
		this.submitTemplate = submitTemplate;
	}

	/**
	 * @return the responseTemplate
	 */
	public Object getResponseTemplate() {
		return responseTemplate;
	}

	/**
	 * @param responseTemplate the responseTemplate to set
	 */
	public void setResponseTemplate(String responseTemplate) {
		this.responseTemplate = responseTemplate;
	}

	/**
	 * @return the registered
	 */
	public Date getRegistered() {
		return registered;
	}

	/**
	 * @param registered the registered to set
	 */
	public void setRegistered(Date registered) {
		this.registered = registered;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, executionType, id, registered, responseTemplate, submitTemplate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Configuration))
			return false;
		Configuration other = (Configuration) obj;
		return Objects.equals(description, other.description) && executionType == other.executionType
				&& Objects.equals(id, other.id) && Objects.equals(registered, other.registered)
				&& Objects.equals(responseTemplate, other.responseTemplate)
				&& Objects.equals(submitTemplate, other.submitTemplate);
	}

	/**
	 * @return the executionType
	 */
	public ExecutionType getExecutionType() {
		return executionType;
	}

	/**
	 * @param executionType the executionType to set
	 */
	public void setExecutionType(ExecutionType executionType) {
		this.executionType = executionType;
	}
	
}
