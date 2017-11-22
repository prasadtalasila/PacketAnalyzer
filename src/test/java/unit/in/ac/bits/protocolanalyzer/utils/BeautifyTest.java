package unit.in.ac.bits.protocolanalyzer.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import in.ac.bits.protocolanalyzer.utils.Beautify;

public class BeautifyTest {	
	@Test
	public void testBeautify() {
		byte bytes1[] = {0 , 16 , 2 , 15 , 4};
		String mode = "ip4";
		assertEquals("INVALID-ADDRESS",Beautify.beautify(bytes1, mode));
		byte bytes2[] = {-65 , 66 , -67 , 68 };
		assertEquals("191.66.189.68",Beautify.beautify(bytes2, mode));
		mode = "hex";
		assertEquals("0010020f04",Beautify.beautify(bytes1, mode));
		mode = "hex2";
		assertEquals("00:10:02:0f:04",Beautify.beautify(bytes1, mode));
		mode = "hex4";
		assertEquals("bf42:bd44",Beautify.beautify(bytes2, mode));
	}
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	@Test
	public void testBeautifyException() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("The mode: fake-mode is not supported for beautification!");
		byte bytes[] = {0};
		Beautify.beautify(bytes, "fake-mode");
	}
}
