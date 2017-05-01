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
public class BeautifyTest
{
	@Autowired
	private Beautify beautify;
	
	byte[] byte_corner_1 = { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
	byte[] byte_corner_0 = { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
	byte[] byte_valid = { (byte)0x84, (byte)0xA0, (byte)0x40, (byte)0x00}; // valid	
	byte[] byte_length_3 = { (byte)0x84, (byte)0xA0, (byte)0x40}; // Length 3
	
	@Test
	public void autowiringTest()
	{
		assertNotNull(beautify);
	}
	
	@Test
	public void properCornerCaseHandling()
	{
		assertThat((beautify.beautify(byte_corner_1, "ip4")), is("255.255.255.255"));
		assertThat((beautify.beautify(byte_corner_0, "ip4")), is("0.0.0.0"));
	}
	
	@Test
	public void properSignedToUnsignedConversion()
	{
		/*
		 * the bytes of byte_valid in binary are 1000 0100, 1010 0000, 0100 0000, 0000 0000
		 * so they should get converted to	 132 : 160 : 64 : 0		(unsigned)
		 * and NOT to 						-124 : -96 : 64 : 0		(signed)
		 */
		
		assertThat((beautify.beautify(byte_valid, "ip4")), is("132.160.64.0"));
	}
	
	@Test
	public void invalidIPv4Length()
	{
		assertThat(beautify.beautify(byte_length_3, "ip4"), is("INVALID-ADDRESS"));
	}
	
	@Test
	public void hexMode()
	{
		assertThat(beautify.beautify(byte_valid, "hex"), is("84a04000")); // ie, 84.A0.40.00
	}
	
	@Test
	public void hex2Mode()
	{
		assertThat(beautify.beautify(byte_valid, "hex2"), is("84:a0:40:00"));
	}
	
	@Test
	public void hex4Mode()
	{
		assertThat(beautify.beautify(byte_valid, "hex4"), is("84a0:4000"));
	}
	
	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testIllegalModeHandling()
	{
		assertThat(beautify.beautify(byte_valid, "dummy"), is("84a0:4000"));
	}
}
