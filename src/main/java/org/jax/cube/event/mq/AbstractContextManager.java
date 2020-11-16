package org.jax.cube.event.mq;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public abstract class AbstractContextManager<T> implements AutoCloseable{

	private static final Pattern TOPIC = Pattern.compile("[a-zA-Z0-9\\.]+");
	private static final Pattern MESSAGE = Pattern.compile(TOPIC.pattern()+" (.+)");
	
	@Autowired
	private ObjectMapper mapper;

	protected ZContext context;
	protected Socket socket;
	
	protected URI uri;
	protected String topic;

	private Class<T> clazz;
	private TypeReference<T> typeRef;
	
	protected AbstractContextManager(Class<T> clazz, String topic) {
		this(clazz, null, topic);
	}
	
	protected AbstractContextManager(TypeReference<T> typeRef, String topic) {
		this(null, typeRef, topic);
	}
	
	private AbstractContextManager(Class<T> clazz, TypeReference<T> typeRef, String topic) {
		this.clazz = clazz;
		this.typeRef = typeRef;
		
		this.context = new ZContext();
		this.topic = topic==null ? "" : topic;
		checkTopic(topic);
	}

	private void checkTopic(String top) {
		if (top.isEmpty()) return;
		if (TOPIC.matcher(top).matches()) return;
		throw new IllegalTopicException("The topic must be alpha-numeric paths separated by dots!");
	}
	
	/**
	 * It is possible in 0MQ to subscribe to all topics by leaving the
	 * topic blank. This requires a strip if you publish on a topic but
	 * receive all topics.
	 * 
	 * @param text
	 * @return
	 */
	protected String stripTopic(String text) {
		if (topic.length()<1) {
			Matcher matcher = MESSAGE.matcher(text);
			return matcher.matches() ? matcher.group(1) : text;
		} else {
			return text.substring(topic.length());
		}
	}

	protected T deserialize(String text) throws JsonMappingException, JsonProcessingException {
		if (mapper == null) mapper = new ObjectMapper();// We make it if not injected e.g. non-spring unit test.
		if (clazz!=null) {
			return mapper.readValue(text, clazz);
		} else {
			return mapper.readValue(text, typeRef);
		}
	}
	
	protected String serialize(T bean) throws JsonMappingException, JsonProcessingException {
		if (mapper == null) mapper = new ObjectMapper();// We make it if not injected e.g. non-spring unit test.
		return mapper.writeValueAsString(bean);
	}

	@Override
	public void close() {
		if (socket!=null) {
			context.destroySocket(socket);
		}
		context.close();
	}

	/**
	 * @return the context
	 */
	public ZContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	protected void setContext(ZContext context) {
		this.context = context;
	}

	/**
	 * @return the uri
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	protected void setUri(URI uri) {
		this.uri = uri;
	}
	
	public static int getFreePort() throws IOException {
		IntStream s = new Random().ints(1000, 0, 65000);
		for (OfInt it = s.iterator(); it.hasNext();) {
			int port = it.nextInt();
			try(ServerSocket sock = new ServerSocket(port)) {
				return port;
			} catch (Exception ne) {
				continue;
			}
		}
		return -1; // We did not find one
	}
	
	protected static String createRandomTopic() {
		return UUID.randomUUID().toString();
	}
	
	public static URI createRandomFreeUri() throws IOException, URISyntaxException {
		return new URI("tcp://localhost:"+getFreePort());
	}

	/**
	 * Simply checks thread is not interrupted and
	 * that the context is not closed.
	 * 
	 * @return true if we are good to go!
	 */
	protected boolean isActive() {
		return !Thread.currentThread().isInterrupted() && !context.isClosed();
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param topic the topic to set
	 */
	protected void setTopic(String topic) {
		this.topic = topic;
	}

}
