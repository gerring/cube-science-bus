package org.jax.cube.event.controller;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.jax.cube.event.db.EngineRepository;
import org.jax.cube.event.db.InstanceRepository;
import org.jax.cube.event.domain.Engine;
import org.jax.cube.event.domain.TResponseBean;
import org.jax.cube.event.domain.TSubmitBean;
import org.jax.cube.event.mq.Publisher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"server.port=3230"})
@AutoConfigureMockMvc
public class CubeEventControllerTest {

	@Autowired
	private EngineRepository engRepo;
	
	@Autowired
	private InstanceRepository instRepo;

	private WebClient client;

	private DummyAnalysis<TSubmitBean, TResponseBean> dummyAnalysis;

	@Before
	public void before() throws Exception {
		
		// Make some engines
		this.dummyAnalysis = new DummyAnalysis<>(TSubmitBean.class, TResponseBean.class);
		dummyAnalysis.setFakeRunner(s->fakeRun(s, dummyAnalysis.getResponder())); // Does the fake run.
		
		engRepo.save(dummyAnalysis.getEngine());
		instRepo.save(dummyAnalysis.getInstance());
		
		this.client = WebClient.create("http://localhost:3230/");
	}

	@Test
	public void getEngines() throws Exception {
		Flux<Engine> fengines = client.get()
				.uri("/engines")
				.retrieve()
				.bodyToFlux(Engine.class);

		List<Engine> engines = fengines.collect(Collectors.toList()).block();
		assertEquals(1, engines.size());
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
