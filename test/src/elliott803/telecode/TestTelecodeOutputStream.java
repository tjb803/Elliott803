/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2012
 */
package elliott803.telecode;

import java.io.StringWriter;

/**
 * JUnit tests for Java->Telecode character conversions
 *
 * @author Baldwin
 */
public class TestTelecodeOutputStream extends BaseTelecodeTest {
    
    /*
     * TelecodeOutputStream should write a sequence of telecode bytes as Java unicode
     * characters. 
     */
    
    static final String LE = System.getProperty("line.separator");
    
    static String[][] testData = {
        { "LS 01",             "A" }, 
        { "01",                "A" },      // Assume letter shift if none given
        { "FS 26",             "#" },      // Pound sign
        { "FS 01 LS 01",       "1A" },
        { "LS 01 CR LF 02",    "A" + LE + "B" },
        { "LS 01 CR CR LF CR", "A" + LE }, // CR ignored, LF->line end
    };
    
    public void testOutputConversion() throws Exception {
        for (int i = 0; i < testData.length; i++) {
            byte[] tc = parseTelecode(testData[i][0]);
            
            String expected = testData[i][1];
            
            // Try with an ASCII-only writer
            StringWriter sout = new StringWriter();
            TelecodeOutputStream tout = new TelecodeOutputStream(sout, true);
            tout.write(tc);
            tout.close();
            assertEquals("ASCII "+i, expected, sout.toString());
            
            // Try with a non-ASCII writer (uses correct £ character)
            expected = expected.replace('#', '£');
            sout = new StringWriter();
            tout = new TelecodeOutputStream(sout, false);
            tout.write(tc);
            tout.close();
            assertEquals("ELLIOTT "+i, expected, sout.toString());
        }
    }
}    
