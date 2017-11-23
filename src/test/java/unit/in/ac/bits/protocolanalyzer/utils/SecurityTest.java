package unit.in.ac.bits.protocolanalyzer.utils;

import in.ac.bits.protocolanalyzer.utils.Security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SecurityTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void testCreateHash() {
		assertThat("Error in hashing function",Security.createHash("BITS"),
				equalTo("94383f71bb67e1abe7b5f06eab5"
						+ "9a146924dea7bf9362394ef5fc528051d48d1"));
		assertThat("Error in hashing function",Security.createHash("darshini"),
				equalTo("eb9d766276f1337f18e71"
						+ "b6a99704561005fc23a5a0300041813438cbaa2e792"));
	}
}
