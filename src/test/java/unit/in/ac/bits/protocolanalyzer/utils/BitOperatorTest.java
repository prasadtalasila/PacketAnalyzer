package unit.in.ac.bits.protocolanalyzer.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
		assertEquals(1 , BitOperator.getBit(byte1, 2));
		assertEquals(0 , BitOperator.getBit(byte1, 0));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Provided bit location is beyond the range 0-7");
		assertEquals(0 , BitOperator.getBit(byte1, -1));
		assertEquals(0 , BitOperator.getBit(byte1, 8));
	}
	
	@Test 
	public void testGetBits() {
		int bits2[] = {0,1,1,1,1,1,1,0};
		byte byte1 = 126;
		assertTrue(Arrays.equals(bits2, BitOperator.getBits(byte1)));
	}
	
	@Test
	public void testGetValue() {
		assertEquals(100 , BitOperator.getValue((byte) 25, 2, 5));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Relevant bits are beyond this byte!");
		assertEquals(100 , BitOperator.getValue((byte) 25, 2, 7));
	}
	
	@Test
	public void testGetNibble() {
		byte byte1 = 126;
		assertEquals(14 , BitOperator.getNibble(byte1,0));
		assertEquals(7 , BitOperator.getNibble(byte1,1));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Nibble index can only be 0 or 1");
		assertEquals(7 , BitOperator.getNibble(byte1,2));
	}
	
	@Test
	public void testParse() {
		byte bytes4[] = {1,0,1,0};
		assertTrue(Arrays.equals(bytes4, BitOperator.parse(bytes4, 0, 31)));
		assertTrue(Arrays.equals(bytes4, BitOperator.parse(bytes4, 0, 30)));
		assertTrue(Arrays.equals(bytes4, BitOperator.parse(bytes4, 1, 31)));
		assertTrue(Arrays.equals(bytes4, BitOperator.parse(bytes4, 1, 30)));
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Either startBit or endBit is out of range of header bytes provided!!");
		assertTrue(Arrays.equals(bytes4, BitOperator.parse(bytes4, -1, 31)));
		assertTrue(Arrays.equals(bytes4, BitOperator.parse(bytes4, 1, 33)));
	}
	
	
}
