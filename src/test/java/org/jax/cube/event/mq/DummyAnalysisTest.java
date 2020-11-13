package org.jax.cube.event.mq;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.jax.cube.event.controller.DummyAnalysis;
import org.jax.cube.event.domain.TResponseBean;
import org.jax.cube.event.domain.TSubmitBean;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Simply tests that the DummyAnalysis has its events working
 * 
 * @author gerrim
 *
 */
public class DummyAnalysisTest {

	@Test
	public void dummyAnalysis() throws Exception {
		
		DummyAnalysis<TSubmitBean, TResponseBean> dummyAnalysis = new DummyAnalysis<>(new TSubmitBean(), new TResponseBean());
		dummyAnalysis.setFakeRunner(s->fakeRun(s, dummyAnalysis.getResponder())); // Does the fake run.

		// Our beans sent will go here.
		List<TResponseBean> runReplies = new ArrayList<>();
		
		CountDownLatch latch = new CountDownLatch(101); // The fake run has 101 messages
		Consumer<TResponseBean> subAction = b->{runReplies.add(b); latch.countDown();};

		// So now when we send the submit message, we get the fake runner responding on the response port.
		try (Subscriber<TResponseBean> 	sub = new Subscriber<>(subAction, TResponseBean.class, dummyAnalysis.getInstance().getResponse());
			 Publisher<TSubmitBean> 	pub = new Publisher<>(TSubmitBean.class, dummyAnalysis.getInstance().getSubmit())) {
			
			TSubmitBean submission = new TSubmitBean();
			submission.setDataPath(Paths.get("/some/path/to/run/do/you/have/access?"));
			submission.setJobName("Test that dummy analysis");
			pub.send(submission);
			
			// This will return once they are all there or timeout after 1000
			latch.await(1000, TimeUnit.MILLISECONDS); // Messages should go fast.
		}
		
		assertEquals(101, runReplies.size());
		assertEquals("Submitted", runReplies.get(0).getStatus());
		assertEquals("Queued", runReplies.get(1).getStatus());
		assertEquals("Running", runReplies.get(2).getStatus());
		assertEquals("Running", runReplies.get(99).getStatus());
		assertEquals("Complete", runReplies.get(100).getStatus());
	}
	

	private void fakeRun(TSubmitBean s, Publisher<TResponseBean> res) {
		
		try {
			TResponseBean r = new TResponseBean();
			r.setStatus("Submitted"); // Should be a state machine, just free text in this bean
			res.send(r);
			
			r.setStatus("Queued"); // Should be a state machine, just free text in this bean
			res.send(r);

			for (int i = 2; i < 100; i+=1) {
				
				r = new TResponseBean();
				r.setComplete(i);
				r.setStatus("Running"); // Should be a state machine, just free text in this bean
				r.setResults(new URI("http://noresultshere.org/someresultsICouldNotFind"));
				res.send(r);
			}
			
			r = new TResponseBean();
			r.setComplete(100);
			r.setStatus("Complete"); // Should be a state machine, just free text in this bean
			res.send(r);
		} catch (Exception ne) {
			fail(ne.getMessage());
		}
	}

}
