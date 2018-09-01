package main.LegacyFontConverter;

import org.apache.commons.lang.StringUtils;

/*
 * Singleton class for VNI-to-Unicode conversion
 * The process consists of 3 parts:
 *
 * 1) converts single-byte (ASCII) characters except O+ and U+ because they are also
 *    used as base in some VNI double-byte characters
 * 2) converts VNI double-byte characters
 * 3) converts O+ and U+
 *
 * Original author: Quan Nguyen
 * Website:         http://vietunicode.sourceforge.net/
 * Modified:        nghia
 *
 * @author     Quan Nguyen
 * @version    1.2, 23 October 05
 */

public class VNI extends Converter {
    private final String[] VNI = {"O\u00C2", "o\u00E2", "y\u00F5", "Y\u00D5", "y\u00FB", "Y\u00DB",
            "y\u00F8", "Y\u00D8", "\u00F6\u00EF", "\u00D6\u00CF", "\u00F6\u00F5", "\u00D6\u00D5",
            "\u00F6\u00FB", "\u00D6\u00DB", "\u00F6\u00F8", "\u00D6\u00D8", "\u00F6\u00F9",
            "\u00D6\u00D9", "u\u00FB", "U\u00DB", "u\u00EF", "U\u00CF", "\u00F4\u00EF", "\u00D4\u00CF",
            "\u00F4\u00F5", "\u00D4\u00D5", "\u00F4\u00FB", "\u00D4\u00DB", "\u00F4\u00F8",
            "\u00D4\u00D8", "\u00F4\u00F9", "\u00D4\u00D9", "o\u00E4", "O\u00C4", "o\u00E3", "O\u00C3",
            "o\u00E5", "O\u00C5", "o\u00E0", "O\u00C0", "o\u00E1", "O\u00C1", "o\u00FB", "O\u00DB",
            "o\u00EF", "O\u00CF", "e\u00E4", "E\u00C4", "e\u00E3", "E\u00C3", "e\u00E5", "E\u00C5",
            "e\u00E0", "E\u00C0", "e\u00E1", "E\u00C1", "e\u00F5", "E\u00D5", "e\u00FB", "E\u00DB",
            "e\u00EF", "E\u00CF", "a\u00EB", "A\u00CB", "a\u00FC", "A\u00DC", "a\u00FA", "A\u00DA",
            "a\u00E8", "A\u00C8", "a\u00E9", "A\u00C9", "a\u00E4", "A\u00C4", "a\u00E3", "A\u00C3",
            "a\u00E5", "A\u00C5", "a\u00E0", "A\u00C0", "a\u00E1", "A\u00C1", "a\u00FB", "A\u00DB",
            "a\u00EF", "A\u00CF", "u\u00F5", "U\u00D5", "a\u00EA", "A\u00CA", "y\u00F9", "u\u00F9",
            "u\u00F8", "o\u00F5", "o\u00F9", "o\u00F8", "e\u00E2", "e\u00F9", "e\u00F8", "a\u00F5",
            "a\u00E2", "a\u00F9", "a\u00F8", "Y\u00D9", "U\u00D9", "U\u00D8", "O\u00D5", "O\u00D9",
            "O\u00D8", "E\u00C2", "E\u00D9", "E\u00D8", "A\u00D5", "A\u00C2", "A\u00D9", "A\u00D8"};

    private final String[] Unicode = {"\u00C6", "\u00E6", "\u1EF9", "\u1EF8", "\u1EF7", "\u1EF6",
            "\u1EF3", "\u1EF2", "\u1EF1", "\u1EF0", "\u1EEF", "\u1EEE", "\u1EED", "\u1EEC", "\u1EEB",
            "\u1EEA", "\u1EE9", "\u1EE8", "\u1EE7", "\u1EE6", "\u1EE5", "\u1EE4", "\u1EE3", "\u1EE2",
            "\u1EE1", "\u1EE0", "\u1EDF", "\u1EDE", "\u1EDD", "\u1EDC", "\u1EDB", "\u1EDA", "\u1ED9",
            "\u1ED8", "\u1ED7", "\u1ED6", "\u1ED5", "\u1ED4", "\u1ED3", "\u1ED2", "\u1ED1", "\u1ED0",
            "\u1ECF", "\u1ECE", "\u1ECD", "\u1ECC", "\u1EC7", "\u1EC6", "\u1EC5", "\u1EC4", "\u1EC3",
            "\u1EC2", "\u1EC1", "\u1EC0", "\u1EBF", "\u1EBE", "\u1EBD", "\u1EBC", "\u1EBB", "\u1EBA",
            "\u1EB9", "\u1EB8", "\u1EB7", "\u1EB6", "\u1EB5", "\u1EB4", "\u1EB3", "\u1EB2", "\u1EB1",
            "\u1EB0", "\u1EAF", "\u1EAE", "\u1EAD", "\u1EAC", "\u1EAB", "\u1EAA", "\u1EA9", "\u1EA8",
            "\u1EA7", "\u1EA6", "\u1EA5", "\u1EA4", "\u1EA3", "\u1EA2", "\u1EA1", "\u1EA0", "\u0169",
            "\u0168", "\u0103", "\u0102", "\u00FD", "\u00FA", "\u00F9", "\u00F5", "\u00F3", "\u00F2",
            "\u00EA", "\u00E9", "\u00E8", "\u00E3", "\u00E2", "\u00E1", "\u00E0", "\u00DD", "\u00DA",
            "\u00D9", "\u00D5", "\u00D3", "\u00D2", "\u00CA", "\u00C9", "\u00C8", "\u00C3", "\u00C2",
            "\u00C1", "\u00C0"};

    // Protected constructor
    protected VNI() {
    }

    @Override
    public String convert(String legacyText) {
        // Part 1
        String convertedText = legacyText.replace('\u00D1', '\u0110')    // DD
                                         .replace('\u00F1', '\u0111')    // dd
                                         .replace('\u00D3', '\u0128')    // I~
                                         .replace('\u00F3', '\u0129')    // i~
                                         .replace('\u00D2', '\u1ECA')    // I.
                                         .replace('\u00F2', '\u1ECB')    // i.
                                         .replace('\u00C6', '\u1EC8')    // I?
                                         .replace('\u00E6', '\u1EC9')    // i?
                                         .replace('\u00CE', '\u1EF4')    // Y.
                                         .replace('\u00EE', '\u1EF5');   // y.

        // Part 2
        // Transform "O\u00C2" -> "\u00C6" to later transform back to "\u00D4" in Part 3
        for (int i = 0; i < VNI.length; i++) {
            convertedText = StringUtils.replace(convertedText, VNI[i], Unicode[i]);
        }

        // Part 3
        convertedText = convertedText.replace('\u00D4', '\u01A0')    // O+
                                     .replace('\u00F4', '\u01A1')    // o+
                                     .replace('\u00D6', '\u01AF')    // U+
                                     .replace('\u00F6', '\u01B0')    // u+
                                     .replace('\u00C6', '\u00D4')    // O^
                                     .replace('\u00E6', '\u00F4');   // o^

        return convertedText;
    }
}
