package unit.in.ac.bits.protocolanalyzer.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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
		assertThat(ByteOperator.parseBytesint(bytes_T), equalTo(16777472));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Byte array length exceeds 4!");
		ByteOperator.parseBytesint(bytes_F);
	}
	
	@Test
	public void testParseBytesbyte() {
		byte bytes_T[] = {127};
		byte bytes_F[] = {127,0};
		assertThat(ByteOperator.parseBytesbyte(bytes_T), equalTo((byte)127));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Byte array length exceeds 4!");
		ByteOperator.parseBytesbyte(bytes_F);		
	}
	
	@Test
	public void testParseBytesshort() {
		byte bytes_T[] = {1,0};
		byte bytes_F[] = {1,0,0};	
		assertThat(ByteOperator.parseBytesshort(bytes_T), equalTo((short)256));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage("Byte array length exceeds 4!");
		ByteOperator.parseBytesshort(bytes_F);
	}
	
	@Test
	public void testParseByteslong() {
		byte bytes_T[] = {1,0,0,0,0,0,0,0};
		byte bytes_F[] = {1,0,0,0,0,0,0,0,0};
		assertThat(ByteOperator.parseByteslong(bytes_T), equalTo(72057594037927936l));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		ByteOperator.parseByteslong(bytes_F);
	}
	
}
