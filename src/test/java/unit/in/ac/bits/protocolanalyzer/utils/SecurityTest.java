package unit.in.ac.bits.protocolanalyzer.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import in.ac.bits.protocolanalyzer.utils.Security;

public class SecurityTest {
	@Test
	public void testCreateHash() {
		assertEquals("94383f71bb67e1abe7b5f06eab59a146924dea7bf9362394ef5fc528051d48d1", Security.createHash("BITS"));
		assertEquals("eb9d766276f1337f18e71b6a99704561005fc23a5a0300041813438cbaa2e792", Security.createHash("darshini"));
	}
}
