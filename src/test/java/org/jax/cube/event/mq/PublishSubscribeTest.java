package org.jax.cube.event.mq;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.jax.cube.event.domain.TSubmitBean;
import org.junit.Test;

public class PublishSubscribeTest {

	@Test
	public void oneSubmit() throws Exception {
		testSubmit(1);
	}
	
	@Test
	public void tenKSubmit() throws Exception {
		testSubmit(10000, 2000);
	}

	@Test
	public void toneSubmitTopic() throws Exception {
		testSubmit(1, "a.topic.to.subscribe.to");
	}
	
	@Test
	public void tenKSubmitTopic() throws Exception {
		testSubmit(10000, "a.topic.to.subscribe.to", 2000);
	}

	@Test(expected=IllegalTopicException.class)
	public void badTopic() throws IOException, URISyntaxException, InterruptedException {
		testSubmit(1, 0, "#$^#$%^.bad.topic.#$%%", "pub.topic", 100);
	}

	@Test
	public void wrongTopic() throws IOException, URISyntaxException, InterruptedException {
		testSubmit(1, 0, "sub.topic", "pub.topic", 100);
	}
	
	@Test
	public void publishAllListeningSpecific() throws IOException, URISyntaxException, InterruptedException {
		testSubmit(1, 0, "", "sub.topic", 100);
	}
	
	@Test
	public void publishTopicListeningAll() throws IOException, URISyntaxException, InterruptedException {
		testSubmit(1, 1, "pub.topic", "", 100);
	}

	
	
	
	private void testSubmit(int size) throws IOException, URISyntaxException, InterruptedException {
		testSubmit(size, size, "", "", 100);
	}
	
	private void testSubmit(int size, long timeout) throws IOException, URISyntaxException, InterruptedException {
		testSubmit(size, size, "", "", timeout);
	}
	
	private void testSubmit(int size, String topic) throws IOException, URISyntaxException, InterruptedException {
		testSubmit(size, size, topic, topic, 100);
	}
	
	private void testSubmit(int size, String topic, long latchTime) throws IOException, URISyntaxException, InterruptedException {
		testSubmit(size, size, topic, topic, latchTime);
	}

	private void testSubmit(int size, int expected, String pubTopic, String subTopic, long latchTime) throws IOException, URISyntaxException, InterruptedException {

		// Our beans sent will go here.
		List<TSubmitBean> submitted = new ArrayList<>();
		
		// A URI to which we will subscribe and then publish
		URI uri = AbstractContextManager.createRandomFreeUri();
		
		CountDownLatch latch = new CountDownLatch(size);
		Consumer<TSubmitBean> subAction = b->{submitted.add(b); latch.countDown();};
		
		try(Publisher<TSubmitBean> pub = new Publisher<>(TSubmitBean.class, uri, pubTopic);
			Subscriber<TSubmitBean> sub = new Subscriber<>(subAction, TSubmitBean.class, uri, subTopic)) {
			
			TSubmitBean message = new TSubmitBean();
			message.setDataPath(Paths.get("/some/path/to/run/do/you/have/access?"));
			message.setJobName("Test");
			message.setResolution(100); // Not very accurate!
			
			for (int i = 0; i < size; i++) {
				pub.send(message);
			}
			
			// This will return once they are all there or timeout after 1000
			latch.await(latchTime, TimeUnit.MILLISECONDS); // Messages should go fast.
		}
		
		assertEquals(expected, submitted.size());
	}
	
}