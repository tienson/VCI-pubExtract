package main.LegacyFontConverter;

/*
 * Singleton class for TCVN3-to-Unicode conversion
 *
 * Original author: Quan Nguyen
 * Website:         http://vietunicode.sourceforge.net/
 * Modified:        nghia
 *
 * @author     Quan Nguyen
 * @version    1.2, 23 October 05
 */

public class TCVN3 extends Converter {
    private final String[] TCVN3_char = {"\u00FC", "\u00FB", "\u00FE", "\u00FA", "\u00F9", "\u00F7", "\u00F6",
            "\u00F5", "\u00F8", "\u00F1", "\u00F4", "\u00EE", "\u00EC", "\u00EB", "\u00EA", "\u00ED",
            "\u00E9", "\u00E7", "\u00E6", "\u00E5", "\u00E8", "\u00E1", "\u00E4", "\u00DE", "\u00D8",
            "\u00D6", "\u00D4", "\u00D3", "\u00D2", "\u00D5", "\u00CF", "\u00CE", "\u00D1", "\u00C6",
            "\u00BD", "\u00BC", "\u00AB", "\u00BE", "\u00CB", "\u00C9", "\u00C8", "\u00C7", "\u00CA",
            "\u00B6", "\u00B9", "\u2212", "\u00A6", "\u00AC", "\u00A5", "\u00F2", "\u00DC", "\u00AE",
            "\u00A8", "\u00A1", "\u00F3", "\u00EF", "\u00E2", "\u00BB", "\u00E3", "\u00DF", "\u00DD",
            "\u00D7", "\u00AA", "\u00D0", "\u00CC", "\u00B7", "\u00A9", "\u00B8", "\u00B5", "\u00A4",
            "\u00A7", "\u00A3", "\u00A2", "\u0041\u00E0", "\u0041\u1EA3", "\u0041\u00E3", "\u0041\u00E1",
            "\u0041\u1EA1", "\u0045\u00E8", "\u0045\u1EBB", "\u0045\u1EBD", "\u0045\u00E9",
            "\u0045\u1EB9", "\u0049\u00EC", "\u0049\u1EC9", "\u0049\u0129", "\u0049\u00ED",
            "\u0049\u1ECB", "\u004F\u00F2", "\u004F\u1ECF", "\u004F\u00F5", "\u004F\u00F3",
            "\u004F\u1ECD", "\u0055\u00F9", "\u0055\u1EE7", "\u0055\u0169", "\u0055\u00FA",
            "\u0055\u1EE5", "\u0059\u1EF3", "\u0059\u1EF7", "\u0059\u1EF9", "\u0059\u00FD",
            "\u0059\u1EF5", "\u0102\u1EB1", "\u0102\u1EB3", "\u0102\u1EB5", "\u0102\u1EAF",
            "\u0102\u1EB7", "\u00C2\u1EA7", "\u00C2\u1EA9", "\u00C2\u1EAB", "\u00C2\u1EA5",
            "\u00C2\u1EAD", "\u00CA\u1EC1", "\u00CA\u1EC3", "\u00CA\u1EC5", "\u00CA\u1EBF",
            "\u00CA\u1EC7", "\u00D4\u1ED3", "\u00D4\u1ED5", "\u00D4\u1ED7", "\u00D4\u1ED1",
            "\u00D4\u1ED9", "\u01A0\u1EDD", "\u01A0\u1EDF", "\u01A0\u1EE1", "\u01A0\u1EDB",
            "\u01A0\u1EE3", "\u01AF\u1EEB", "\u01AF\u1EED", "\u01AF\u1EEF", "\u01AF\u1EE9",
            "\u01AF\u1EF1"};

    private final String[] Unicode_char = {"\u1EF9", "\u1EF7", "\u1EF5", "\u1EF3", "\u1EF1", "\u1EEF", "\u1EED",
            "\u1EEB", "\u1EE9", "\u1EE7", "\u1EE5", "\u1EE3", "\u1EE1", "\u1EDF", "\u1EDD", "\u1EDB",
            "\u1ED9", "\u1ED7", "\u1ED5", "\u1ED3", "\u1ED1", "\u1ECF", "\u1ECD", "\u1ECB", "\u1EC9",
            "\u1EC7", "\u1EC5", "\u1EC3", "\u1EC1", "\u1EBF", "\u1EBD", "\u1EBB", "\u1EB9", "\u1EB7",
            "\u1EB5", "\u1EB3", "\u00F4", "\u1EAF", "\u1EAD", "\u1EAB", "\u1EA9", "\u1EA7", "\u1EA5",
            "\u1EA3", "\u1EA1", "\u01B0", "\u01AF", "\u01A1", "\u01A0", "\u0169", "\u0129", "\u0111",
            "\u0103", "\u0102", "\u00FA", "\u00F9", "\u00F5", "\u1EB1", "\u00F3", "\u00F2", "\u00ED",
            "\u00EC", "\u00EA", "\u00E9", "\u00E8", "\u00E3", "\u00E2", "\u00E1", "\u00E0", "\u00D4",
            "\u0110", "\u00CA", "\u00C2", "\u00C0", "\u1EA2", "\u00C3", "\u00C1", "\u1EA0", "\u00C8",
            "\u1EBA", "\u1EBC", "\u00C9", "\u1EB8", "\u00CC", "\u1EC8", "\u0128", "\u00CD", "\u1ECA",
            "\u00D2", "\u1ECE", "\u00D5", "\u00D3", "\u1ECC", "\u00D9", "\u1EE6", "\u0168", "\u00DA",
            "\u1EE4", "\u1EF2", "\u1EF6", "\u1EF8", "\u00DD", "\u1EF4", "\u1EB0", "\u1EB2", "\u1EB4",
            "\u1EAE", "\u1EB6", "\u1EA6", "\u1EA8", "\u1EAA", "\u1EA4", "\u1EAC", "\u1EC0", "\u1EC2",
            "\u1EC4", "\u1EBE", "\u1EC6", "\u1ED2", "\u1ED4", "\u1ED6", "\u1ED0", "\u1ED8", "\u1EDC",
            "\u1EDE", "\u1EE0", "\u1EDA", "\u1EE2", "\u1EEA", "\u1EEC", "\u1EEE", "\u1EE8", "\u1EF0"};

    // Protected constructor
    protected TCVN3() {
    }

    @Override
    public String convert(String legacyText) {
        return convert(legacyText, TCVN3_char, Unicode_char);
    }
}
