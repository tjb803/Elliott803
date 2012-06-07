/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2012
 */
package elliott803.telecode;

import java.io.StringReader;
import java.util.Arrays;

/**
 * JUnit tests for Telecode->Java character conversions
 *
 * @author Baldwin
 */
public class TestTelecodeInputStream extends BaseTelecodeTest {
    
    /*
     * TelecodeInputStream should provide a sequence of telecode bytes that correspond
     * to the input sequence of Java unicode characters.
     * 
     * These tests mostly check line ends are converted correctly.  A Java line end is converted
     * to the telecode character pair CR,LF and line end is defined (using the same rules 
     * as the Java BufferedReader.readLine() method) as either a single '\n', a single '\r'
     * or the sequence '\n\r'.
     */
    
    static String[][] testData = {
        /* Java input*/  /* Expected telecode output */
        { "a",           "LS 01" }, 
        { "A",           "LS 01" }, 
        { "1",           "FS 01" }, 
        { "a1",          "LS 01 FS 01" }, 
        { "a\nb",        "LS 01 CR LF 02" }, 
        { "a\r2",        "LS 01 CR LF FS 02" }, 
        { "a\r\nb",      "LS 01 CR LF 02" }, 
        { "a\r\n\rb",    "LS 01 CR LF CR LF 02" }, 
        { "a\n",         "LS 01 CR LF" }, 
        { "a_~\u220eb",  "LS 01 00 02" },   // Note: _=blank, ~ and u220e should be ignored 
    };

    public void testInputConversion() throws Exception {
        for (int i = 0; i < testData.length; i++) {
            StringReader s = new StringReader(testData[i][0]);
            TelecodeInputStream tin = new TelecodeInputStream(s);
            
            byte[] expected = parseTelecode(testData[i][1]);
            
            byte[] big = new byte[100];
            int size = tin.read(big);
            assertEquals("Test "+i, expected.length, size);
            
            byte[] tc = new byte[size];
            System.arraycopy(big, 0, tc, 0, size);
            assertTrue("Result "+i, Arrays.equals(expected, tc));
        }
    }
}
