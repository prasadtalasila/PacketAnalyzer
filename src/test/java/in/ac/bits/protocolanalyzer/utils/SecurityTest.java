package in.ac.bits.protocolanalyzer.utils;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SecurityTest
{
	@Autowired
	private Security security;
	
	String hash1;
	String hash2;
	String hash3;
	String hash4;

	@Before
	public void setUp()
	{
		hash1 = security.createHash("Testing hashing function of security class.");
		hash2 = security.createHash("Testing hashing function of security class.");
		hash3 = security.createHash("1 Testing hashing function of security class.");
		hash4 = security.createHash("2 Testing hashing function of security class.");
	}
	
	@Test
	public void autowiringTest()
	{
		assertThat(security, is(not(nullValue())));
	}
	
	@Test
	public void testConsistentHashing1()
	{
		assertThat(hash1, is(hash2));
	}
	
	@Test
	public void testConsistentHashing2()
	{
		assertThat(hash3, is(not(hash4)));
	}
}