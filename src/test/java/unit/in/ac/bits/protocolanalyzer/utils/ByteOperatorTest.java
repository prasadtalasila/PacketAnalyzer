package unit.in.ac.bits.protocolanalyzer.utils;

import in.ac.bits.protocolanalyzer.utils.ByteOperator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ByteOperatorTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	static final String ERROR = "Byte array length exceeds 4!";
	
	@Test
	public void testParseBytesint() {
		byte[] trueBytes = {1,0,1,0};
		assertThat("Error in converting byte array to int",
				ByteOperator.parseBytesint(trueBytes), equalTo(16777472));
		byte[] falseBytes = {1,0,1,0,1};
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage(ERROR);
		ByteOperator.parseBytesint(falseBytes);
	}
	
	@Test
	public void testParseBytesbyte() {
		byte[] trueBytes = {127};
		assertThat("Error in converting byte array to single byte",
				ByteOperator.parseBytesbyte(trueBytes), equalTo((byte)127));
		byte[] falseBytes = {127,0};
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage(ERROR);
		ByteOperator.parseBytesbyte(falseBytes);		
	}
	
	@Test
	public void testParseBytesshort() {
		byte[] trueBytes = {1,0};
		assertThat("Error in converting byte array to short",
				ByteOperator.parseBytesshort(trueBytes), equalTo((short)256));
		byte[] falseBytes = {1,0,0};	
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		expectedEx.expectMessage(ERROR);
		ByteOperator.parseBytesshort(falseBytes);
	}
	
	@Test
	public void testParseByteslong() {
		byte[] trueBytes = {1,0,0,0,0,0,0,0};
		byte[] falseBytes = {1,0,0,0,0,0,0,0,0};
		assertThat("Error in converting byte array to long",
				ByteOperator.parseByteslong(trueBytes), 
				equalTo(72057594037927936L));
		expectedEx.expect(ArrayIndexOutOfBoundsException.class);
		ByteOperator.parseByteslong(falseBytes);
	}
	
}
