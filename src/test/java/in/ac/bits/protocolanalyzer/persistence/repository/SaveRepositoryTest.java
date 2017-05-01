package in.ac.bits.protocolanalyzer.persistence.repository;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.google.common.eventbus.EventBus;

import config.in.ac.bits.protocolanalyzer.persistence.repository.SaveRepositoryTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SaveRepositoryTestConfig.class, loader = AnnotationConfigContextLoader.class)
public class SaveRepositoryTest {
	@Autowired
	public SaveRepository saveRepo;
	
	@Mock
	public EventBus bus;
    
	@Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void wiringTest() {
		assertNotNull(saveRepo);
	}

	@Test
	public void testConfiguration() {
		saveRepo.configure(bus);
	}
}
