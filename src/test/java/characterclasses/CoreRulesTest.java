package characterclasses;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CoreRulesTest {

    private static byte[] sevenBitAscii = new byte[128];
    private static int[] controlIndices = new int[33];
    private static int[] visibleIndices = new int[94];
    private static int[] alphaIndices = new int[52];
    private static int[] numericIndices = new int[10];

    @BeforeClass
    public static void setup() {
        for (int i = 0; i < 128; i++) {
            sevenBitAscii[i] = (byte) i;
        }
        for (int i = 0; i < 32; i++) {
            controlIndices[i] = i;
        }
        controlIndices[32] = 127;
        for (int i = 0; i < 94; i++) {
            visibleIndices[i] = i + 33;
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 26; j++) {
                alphaIndices[j + (i * 26)] = j + 65 + (i * 32);
            }
        }
        for (int i = 0; i < 10; i++) {
            numericIndices[i] = i + 48;
        }

    }

    @Test
    public void testAlpha() {
        testRule(CoreRules.ALPHA, getAsciiString(alphaIndices), getAsciiComplementString(alphaIndices));
    }

    @Test
    public void testBit() {
        testRule(CoreRules.BIT, "01", getAsciiComplementString(new int[]{48, 49}));
    }

    @Test
    public void testChar() {
        testRule(CoreRules.CHAR, getAsciiComplementString(new int[]{0}), fromChar(0));
    }

    @Test
    public void testCarriageReturn() {
        testRule(CoreRules.CR, fromChar(13), getAsciiComplementString(new int[]{13}));
    }

    @Test
    public void testControl() {
        testRule(CoreRules.CTL, getAsciiString(controlIndices), getAsciiComplementString(controlIndices));
    }

    @Test
    public void testDigits() {
        testRule(CoreRules.DIGIT, getAsciiString(numericIndices), getAsciiComplementString(numericIndices));
    }

    @Test
    public void testDoubleQuote() {
        testRule(CoreRules.DQUOTE, fromChar(34), getAsciiComplementString(new int[]{34}));
    }

    @Test
    public void testHexDigits() {
        int[] indexArray = concatRange(numericIndices, concatRange(buildRange(65, 70), buildRange(97, 102)));
        testRule(CoreRules.HEXDIG, getAsciiString(indexArray), getAsciiComplementString(indexArray));
    }

    @Test
    public void testHorizontalTab() {
        testRule(CoreRules.HTAB, fromChar(9), getAsciiComplementString(new int[]{9}));
    }

    @Test
    public void testLineFeed() {
        testRule(CoreRules.LF, fromChar(10), getAsciiComplementString(new int[]{10}));
    }

    @Test
    public void testCrLf() {
        String shouldFail = getAsciiString(buildRange(0, 127));
        boolean matches = Pattern.matches(CoreRules.CRLF, fromChar(13) + fromChar(10));
        boolean allFail = testAllFail(CoreRules.CRLF, shouldFail);
        assertTrue(matches && allFail);
    }

    @Test
    public void testSpace() {
        testRule(CoreRules.SP, fromChar(32), getAsciiComplementString(new int[]{32}));
    }

    @Test
    public void testWhiteSpace() {
        testRule(CoreRules.WSP, fromChar(32) + fromChar(9), getAsciiComplementString(new int[]{9, 32}));
    }

    @Test
    public void testLinearWhiteSpace() {
        boolean crlfPlusSp = Pattern.matches(CoreRules.LWSP, fromChar(13) + fromChar(10) + fromChar(32));
        boolean crlfPlusHtab = Pattern.matches(CoreRules.LWSP, fromChar(13) + fromChar(10) + fromChar(9));

        // should even match empty string by definition [ie. *(WSP / CRLF WSP)]
        boolean emptyStringMatch = Pattern.matches(CoreRules.LWSP, "");
        assertTrue(emptyStringMatch);
        assertTrue(crlfPlusSp);
        assertTrue(crlfPlusHtab);

        // other permutations should NOT match
        boolean perm1 = Pattern.matches(CoreRules.LWSP, fromChar(13) + fromChar(32) + fromChar(10));
        boolean perm2 = Pattern.matches(CoreRules.LWSP, fromChar(32) + fromChar(13) + fromChar(10));
        boolean perm3 = Pattern.matches(CoreRules.LWSP, fromChar(32) + fromChar(10) + fromChar(13));
        boolean perm4 = Pattern.matches(CoreRules.LWSP, fromChar(10) + fromChar(13) + fromChar(32));
        boolean perm5 = Pattern.matches(CoreRules.LWSP, fromChar(10) + fromChar(32) + fromChar(13));
        assertFalse(perm1);
        assertFalse(perm2);
        assertFalse(perm3);
        assertFalse(perm4);
        assertFalse(perm5);

        // only match 2 ascii singles (ie. SP, HTAB)
        int matches = 0;
        for (byte c : sevenBitAscii) {
            if (Pattern.matches(CoreRules.LWSP, fromChar(c))) {
                matches++;
            }
        }
        assertEquals(2, matches);
    }

    @Test
    public void testOctet() {
        //trivial, match any byte of data
        int numMatches = 0;
        for (int i = 0; i < 256; i++) {
            if (Pattern.matches(CoreRules.OCTET, fromChar(i))) {
                numMatches++;
            }
        }
        assertEquals(256, numMatches);
        //shouldn't match anything higher
        numMatches = 0;
        for (int i = 256; i < 512; i++) {
            if (Pattern.matches(CoreRules.OCTET, fromChar(i))) {
                numMatches++;
            }
        }
        assertEquals(0, numMatches);
    }

    @Test
    public void testVisibleCharacters() {
        testRule(CoreRules.VCHAR, getAsciiString(visibleIndices), getAsciiComplementString(visibleIndices));
    }

    public void testRule(String rule, String charsToMatch, String charsToFail) {
        boolean allMatch = testAllMatch(rule, charsToMatch);
        boolean allFail = testAllFail(rule, charsToFail);
        assertTrue(allMatch && allFail);
    }

    private int[] concatRange(int[] range1, int[] range2) {
        int[] concatted = new int[range1.length + range2.length];
        System.arraycopy(range1, 0, concatted, 0, range1.length);
        System.arraycopy(range2, 0, concatted, range1.length, range2.length);
        return concatted;
    }

    private int[] buildRange(int begin, int end) {
        int[] range = new int[end - begin + 1];
        for (int i = begin; i <= end; i++) {
            range[i - begin] = i;
        }
        return range;
    }

    private static boolean testAllMatch(String regexPattern, String charsToPass) {
        return !charsToPass
                .chars()
                .filter(c -> !Pattern.matches(regexPattern, fromChar(c)))
                .findAny()
                .isPresent();
    }

    private static boolean testAllFail(String regexPattern, String charsToFail) {
        return !charsToFail
                .chars()
                .filter(c -> Pattern.matches(regexPattern, fromChar(c)))
                .findAny()
                .isPresent();
    }

    private static String getAsciiString(int[] indexArray) {
        return getAsciiComplementString(getAsciiIndexComplement(indexArray));
    }

    private static String getAsciiComplementString(int[] indexArray) {
        StringBuilder sb = new StringBuilder(128 - indexArray.length);
        for (int i : getAsciiIndexComplement(indexArray)) {
            sb.append(fromChar(i));
        }
        return sb.toString();
    }

    private static int[] getAsciiIndexComplement(int[] indexArray) {
        int[] complement = new int[128 - indexArray.length];
        int[] bool = new int[128];
        for (int i : indexArray) {
            bool[i] = 1;
        }
        int counter = 0;
        for (int i = 0; i < 128; i++) {
            if (bool[i] == 0) {
                complement[counter] = i;
                counter++;
            }
        }
        return complement;
    }

    private static String fromChar(int c) {
        return Character.toString((char) c);
    }

    @Ignore("Only needed to verify ASCII subsets.")
    public void inspectAscii() {
        // verify by visual inspection
        System.out.println("Printing the non-printable/control (!) ascii characters...");
        for (int i : controlIndices) {
            System.out.printf("'%s' ", fromChar(sevenBitAscii[i]));
        }
        System.out.println("\nPrinting the printable ascii characters...");
        for (int i : visibleIndices) {
            System.out.printf("'%s' ", fromChar(sevenBitAscii[i]));
        }
        System.out.println("\nPrinting the alpha ascii characters...");
        for (int i : alphaIndices) {
            System.out.printf("'%s' ", fromChar(sevenBitAscii[i]));
        }
        System.out.println("\nPrinting numeric ascii characters...");
        for (int i : numericIndices) {
            System.out.printf("'%s' ", fromChar(sevenBitAscii[i]));
        }
    }

}