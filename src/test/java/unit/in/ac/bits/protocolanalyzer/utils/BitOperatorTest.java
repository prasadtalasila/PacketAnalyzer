package unit.in.ac.bits.protocolanalyzer.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import in.ac.bits.protocolanalyzer.utils.BitOperator;



public class BitOperatorTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void testGetBit() {
		byte byte1 = 126;
		assertThat(BitOperator.getBit(byte1, 2), equalTo(1));
		assertThat(BitOperator.getBit(byte1, 0), equalTo(0));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Provided bit location is beyond the range 0-7");
		BitOperator.getBit(byte1, -1);
		BitOperator.getBit(byte1, 8);
	}
	
	@Test 
	public void testGetBits() {
		int bits2[] = {0,1,1,1,1,1,1,0};
		byte byte1 = 126;
		assertThat(Arrays.equals(bits2, BitOperator.getBits(byte1)), equalTo(true));
	}
	
	@Test
	public void testGetValue() {
		assertThat(BitOperator.getValue((byte) 25, 2, 5), equalTo(100));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Relevant bits are beyond this byte!");
		BitOperator.getValue((byte) 25, 2, 7);
	}
	
	@Test
	public void testGetNibble() {
		byte byte1 = 126;
		assertThat(BitOperator.getNibble(byte1,0), equalTo(14));
		assertThat(BitOperator.getNibble(byte1,1), equalTo(7));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Nibble index can only be 0 or 1");
		BitOperator.getNibble(byte1,2);
	}
	
	@Test
	public void testParse() {
		byte bytes4[] = {1,0,1,0};
		assertThat(Arrays.equals(bytes4, BitOperator.parse(bytes4, 0, 31)), equalTo(true));
		assertThat(Arrays.equals(bytes4, BitOperator.parse(bytes4, 0, 30)), equalTo(true));
		assertThat(Arrays.equals(bytes4, BitOperator.parse(bytes4, 1, 31)), equalTo(true));
		assertThat(Arrays.equals(bytes4, BitOperator.parse(bytes4, 1, 30)), equalTo(true));
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Either startBit or endBit is out of range of header bytes provided!!");
		BitOperator.parse(bytes4, -1, 31);
		BitOperator.parse(bytes4, 1, 33);
	}
	
	
}
