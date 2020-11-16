package org.jax.cube.event.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.jax.cube.event.db.ConfigurationRepository;
import org.jax.cube.event.db.InstanceRepository;
import org.jax.cube.event.domain.Configuration;
import org.jax.cube.event.domain.Instance;
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
	private ConfigurationRepository engRepo;
	
	@Autowired
	private InstanceRepository instRepo;

	private WebClient client;

	private static DummyAnalysis<TSubmitBean, TResponseBean> dummyAnalysis;
	private static boolean dataCreated;
	
	@Before
	public void createData() throws Exception {
		
		if (dataCreated) return;
		dataCreated = true;
		
		// Make some engines
		dummyAnalysis = new DummyAnalysis<>(TSubmitBean.class, TResponseBean.class);
		dummyAnalysis.init(s->fakeRun(s, dummyAnalysis.getResponder())); // Does the fake run.
		
		engRepo.save(dummyAnalysis.getConfig());
		Configuration engine = engRepo.getOne(dummyAnalysis.getConfig().getId());
		assertNotNull(engine);
		
		instRepo.save(dummyAnalysis.getInstance());
		Instance instance = instRepo.getOne(dummyAnalysis.getInstance().getId());
		assertNotNull(instance);
		
		assertEquals(1, engRepo.findAll().size());
		assertEquals(1, instRepo.findAll().size());
	}
		
	@Before
	public void createClient() throws Exception {
		this.client = WebClient.create("http://localhost:3230/");
	}

	@Test
	public void getEngines() throws Exception {
		Flux<Configuration> fengines = client.get()
				.uri("/engines")
				.retrieve()
				.bodyToFlux(Configuration.class);

		List<Configuration> engines = fengines.collect(Collectors.toList()).block();
		assertEquals(1, engines.size());
	}

	@Test
	public void getIstances() throws Exception {
		Flux<Instance> finstances = client.get()
				.uri("/instances")
				.retrieve()
				.bodyToFlux(Instance.class);

		List<Instance> instances = finstances.collect(Collectors.toList()).block();
		assertEquals(1, instances.size());
	}

	@Test
	public void getInstancesOfEngine() throws Exception {
		Flux<Instance> einstances = client.get()
				.uri("/instancesOfEngine/{configId}", dummyAnalysis.getConfig().getId())
				.retrieve()
				.bodyToFlux(Instance.class);

		List<Instance> instances = einstances.collect(Collectors.toList()).block();
		assertEquals(1, instances.size());
		assertNotEquals(dummyAnalysis.getConfig().getId(), instances.get(0).getId());
		assertEquals(dummyAnalysis.getInstance().getId(), instances.get(0).getId());
		
	}
	
	@Test
	public void fakeRun() throws Exception {
		
		Flux<TResponseBean> runs = client.get()
				.uri("/subscribe/{configId}", dummyAnalysis.getConfig().getId())
				.retrieve()
				.bodyToFlux(TResponseBean.class);
		
		dummyAnalysis.trigger();
		
		List<TResponseBean> instances = runs.collect(Collectors.toList()).block();
		System.out.println();
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
			res.close();
		}
	}

}
