import compiler.FileReader;

public class TestBaseMain {

	public static void main(String[] args) throws Exception {
		System.out.println("BEGIN");
		test.TestSuiteIntf test = new test.TestSuite(FileReader.fromFileName("TestSuiteAutomatedInput.txt"), new TestCase());
		test.testRun();
		System.out.println("END");
	}

}
