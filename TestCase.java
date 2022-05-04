import compiler.FileReaderIntf;
import test.TestCaseIntf;

public class TestCase implements TestCaseIntf {

	@Override
	public String executeTest(FileReaderIntf fileReader) throws Exception {
		String result = new String();
		while (fileReader.lookAheadChar() != 0) {
			result += fileReader.lookAheadChar();
			fileReader.advance();
		}
		return result;
	}

}
