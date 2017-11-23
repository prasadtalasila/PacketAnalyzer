package unit.in.ac.bits.protocolanalyzer.utils;

import in.ac.bits.protocolanalyzer.utils.Beautify;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SuppressWarnings({"squid:S1313","PMD.AvoidUsingHardCodedIP"})
public class BeautifyTest {	
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	
	@Test
	public void testBeautify() {
		byte[] bytes1 = {0 , 16 , 2 , 15 , 4};
		String mode = "ip4";
		assertThat( "Validity check bypassed",
				Beautify.beautify(bytes1, mode), equalTo("INVALID-ADDRESS"));
		byte[] bytes2 = {-65 , 66 ,-67 , 68 };
		assertThat( "IPv4 address beautification fails.",
				Beautify.beautify(bytes2, mode), equalTo("191.66.189.68"));
		mode = "hex";
		assertThat("Hex address beautification fails.",
				Beautify.beautify(bytes1, mode), equalTo("0010020f04"));
		mode = "hex2";
		assertThat("Hex2 address beautification fails.",
				Beautify.beautify(bytes1, mode), equalTo("00:10:02:0f:04"));
		mode = "hex4";
		assertThat("Hex4 address beautification fails.",
				Beautify.beautify(bytes2, mode), equalTo("bf42:bd44"));
	}
	
	@Test
	public void testBeautifyException() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("The mode: "
				+ "fake-mode is not supported for beautification!");
		byte[] bytes = {0};
		Beautify.beautify(bytes, "fake-mode");
	}
}
