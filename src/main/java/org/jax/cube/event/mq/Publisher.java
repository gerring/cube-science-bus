package org.jax.cube.event.mq;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.zeromq.SocketType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Very light class for sending beans as string messages.
 * @author gerrim
 *
 * @param <T>
 */
public class Publisher<T> extends AbstractContextManager<T> {

	public Publisher(Class<T> clazz) throws IOException, URISyntaxException {
		this(clazz, createRandomFreeUri(), ""); // Empty topic subscribes to everything
	}
	
	public Publisher(Class<T> clazz, URI uri) throws IOException, URISyntaxException {
		this(clazz, uri, ""); // Empty topic subscribes to everything
	}

	public Publisher(Class<T> clazz, URI uri, String topic) throws IOException, URISyntaxException {
		
		super(clazz, topic);
		this.uri = uri;
		
		this.socket = context.createSocket(SocketType.PUB);
		socket.bind(uri.toASCIIString());
	}
	
	public boolean send(T bean) throws JsonMappingException, JsonProcessingException {
		
		String msg = serialize(bean);
        String update = String.format(getTopic()+" %s", msg);
        return socket.send(update);
	}

}
