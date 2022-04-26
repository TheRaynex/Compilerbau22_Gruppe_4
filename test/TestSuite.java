package test;

import compiler.FileReaderIntf;

public class TestSuite extends TestSuiteIntf{

    TestSuite(FileReaderIntf fileReader, TestCaseIntf testCase) {
        super(fileReader, testCase);
    }

    @Override
    void readAndExecuteTestSequence() throws Exception {

    }

    @Override
    void readAndExecuteTestCase() throws Exception {
    readDollarIn();
    readTestContent();
    readDollarOut();
    readTestContent();
    }

    @Override
    String readTestContent() throws Exception {
        return null;
    }

    @Override
    void readDollarIn() throws Exception {
        m_fileReader.expect('$');
        m_fileReader.expect('I');
        m_fileReader.expect('n');
        m_fileReader.expect('\n');
    }

    @Override
    void readDollarOut() throws Exception {
        m_fileReader.expect('$');
        m_fileReader.expect('O');
        m_fileReader.expect('u');
        m_fileReader.expect('t');
        m_fileReader.expect('\n');
    }
}
