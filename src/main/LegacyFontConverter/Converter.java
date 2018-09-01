package main.LegacyFontConverter;

import org.apache.commons.lang.StringUtils;

public abstract class Converter {
    /**
     * Main function. The subclasses will define the mapping from its legacy table to the Unicode table
     *
     * @param legacyText the text to be converted
     * @return the Unicode version of the text
     */
    public abstract String convert(String legacyText);

    /**
     * Explicit converter
     *
     * @param input the text to be converted
     * @param legacy the legacy encoding
     * @param unicode the equivalent unicode encoding
     * @return the Unicode version of the text
     */
    protected String convert(String input, String[] legacy, String[] unicode) {

        for (int i = 0; i < legacy.length; i++) {
            input = StringUtils.replace(input, legacy[i], unicode[i]);
        }

        return input;
    }
}
