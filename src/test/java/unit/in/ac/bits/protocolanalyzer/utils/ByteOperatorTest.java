package unit.in.ac.bits.protocolanalyzer.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import in.ac.bits.protocolanalyzer.utils.ByteOperator;

public class ByteOperatorTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void testParseBytesint() {
		byte bytes_T[] = {1,0,1,0};
		byte bytes_F[] = {1,0,1,0,1};
		assertEquals(16777472 , ByteOperator.parseBytesint(bytes_T));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Byte array length exceeds 4!");
		assertEquals(16777472 , ByteOperator.parseBytesint(bytes_F));
	}
	
	@Test
	public void testParseBytesbyte() {
		byte bytes_T[] = {127};
		byte bytes_F[] = {127,0};
		assertEquals(127 , ByteOperator.parseBytesbyte(bytes_T));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Byte array length exceeds 4!");
		assertEquals(127 , ByteOperator.parseBytesbyte(bytes_F));		
	}
	
	@Test
	public void testParseBytesshort() {
		byte bytes_T[] = {1,0};
		byte bytes_F[] = {1,0,0};	
		assertEquals(256 , ByteOperator.parseBytesshort(bytes_T));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Byte array length exceeds 4!");
		assertEquals(256 , ByteOperator.parseBytesshort(bytes_F));
	}
	
	@Test
	public void testParseByteslong() {
		byte bytes_T[] = {1,0,0,0,0,0,0,0};
		byte bytes_F[] = {1,0,0,0,0,0,0,0,0};
		assertEquals(72057594037927936l , ByteOperator.parseByteslong(bytes_T));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		assertEquals(72057594037927936l , ByteOperator.parseByteslong(bytes_F));
	}
	
}
