package org.jax.cube.event.mq;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQException;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Very light class for subscribing to string messages which are
 * JSON serialized objects.
 * 
 * @author gerrim
 *
 * @param <T>
 */
public class Subscriber<T> extends AbstractContextManager<T> {
	
	private static Logger logger = LoggerFactory.getLogger(Subscriber.class);

	private Thread thread;
	private Consumer<T> consumer;
	
	public Subscriber(Class<T> clazz) throws IOException, URISyntaxException {
		this(null, clazz, ""); // Empty topic subscribes to everything
	}

	public Subscriber(Consumer<T> consumer, Class<T> clazz) throws IOException, URISyntaxException {
		this(consumer, clazz, ""); // Empty topic subscribes to everything
	}
	
	public Subscriber(Consumer<T> consumer, Class<T> clazz, URI uri) throws IOException, URISyntaxException {
		this(consumer, clazz, uri, ""); // Empty topic subscribes to everything
	}

	public Subscriber(Consumer<T> consumer, Class<T> clazz, String topic) throws IOException, URISyntaxException {
		this(consumer, clazz, createRandomFreeUri(), topic);
	}
	
	public Subscriber(Consumer<T> consumer, Class<T> clazz, URI uri,  String topic) throws IOException, URISyntaxException {

		super(clazz, topic);
		this.uri = uri;
		this.consumer = consumer;
		this.socket = context.createSocket(SocketType.SUB);
		socket.connect(uri.toString());
		socket.subscribe(getTopic().getBytes(ZMQ.CHARSET));
		start(socket);
	}

	private void start(Socket ssubscriber) {
		this.thread = new Thread(()->listen(ssubscriber), "0MQ Subscriber "+topic);
		thread.setDaemon(true);
		thread.start();
	}

	private void listen(Socket ssubscriber) {

		while (isActive()) {
			String text = null;
			try {
				text = ssubscriber.recvStr(); // Blocking method until message comes
				if (text == null) break;
				text = stripTopic(text);
			} catch (ZMQException error) {
				// this occurs if the context is closed while we are waiting.
				close(); // Just make sure.
				return;
			}
			try {
				T bean = deserialize(text);
				consumer.accept(bean);
			} catch (JsonProcessingException e) {
				logger.error("Cannot listen to events on: "+getUri()+". Disconnecting this 0MQ port now.", e);
				close();
			}
		}
	}

	/**
	 * @param consumer the consumer to set
	 */
	public void setConsumer(Consumer<T> consumer) {
		this.consumer = consumer;
	}

}
