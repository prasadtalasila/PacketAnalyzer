package unit.in.ac.bits.protocolanalyzer.utils;

import in.ac.bits.protocolanalyzer.utils.BitOperator;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BitOperatorTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void testGetBit() {
		byte byte1 = 126;
		assertThat("Error in retrieving particular bit from input",
				BitOperator.getBit(byte1, 2),
				equalTo(1));
		assertThat("Error in retrieving particular bit from input",
				BitOperator.getBit(byte1, 0),
				equalTo(0));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Provided bit location is beyond the range 0-7");
		BitOperator.getBit(byte1, -1);
		BitOperator.getBit(byte1, 8);
	}
	
	@Test 
	public void testGetBits() {
		int[] bits2 = {0,1,1,1,1,1,1,0};
		byte byte1 = 126;
		assertThat("Error in retrieving all bits of input",
				Arrays.equals(bits2, BitOperator.getBits(byte1)),
				equalTo(true));
	}
	
	@Test
	public void testGetValue() {
		assertThat("Error in calculating value of sub-bitString from input bits",
				BitOperator.getValue((byte) 25, 2, 5),
				equalTo(100));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Relevant bits are beyond this byte!");
		BitOperator.getValue((byte) 25, 2, 7);
	}
	
	@Test
	public void testGetNibble() {
		byte byte1 = 126;
		assertThat("Error in extracting nibble from byte",
				BitOperator.getNibble(byte1,0),
				equalTo(14));
		assertThat("Error in extracting nibble from byte",
				BitOperator.getNibble(byte1,1),
				equalTo(7));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Nibble index can only be 0 or 1");
		BitOperator.getNibble(byte1,2);
	}
	
	@Test
	public void testParse() {
		byte[] bytes4 = {1,0,1,0};
		final String error = "Error in splitting input into subarray ";
		assertThat(error ,Arrays.equals(bytes4, BitOperator.parse(bytes4, 0, 31)),
				equalTo(true));
		assertThat(error,Arrays.equals(bytes4, BitOperator.parse(bytes4, 0, 30)),
				equalTo(true));
		assertThat(error,Arrays.equals(bytes4, BitOperator.parse(bytes4, 1, 31)),
				equalTo(true));
		assertThat(error,Arrays.equals(bytes4, BitOperator.parse(bytes4, 1, 30)),
				equalTo(true));
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Either startBit or endBit is out"
				+" of range of header bytes provided!!");
		BitOperator.parse(bytes4, -1, 31);
		BitOperator.parse(bytes4, 1, 33);
	}
	
	
}
