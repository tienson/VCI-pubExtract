package main.LegacyFontConverter;

/**
 * Factory class for the converters
 * Converters are Singletons
 */
public class ConverterFactory {
    private TCVN3 TCVN3Converter;
    private VNI VNIConverter;

    public ConverterFactory() {
        TCVN3Converter = new TCVN3();
        VNIConverter = new VNI();
    }

    public Converter getTCVN3Converter() {
        return TCVN3Converter;
    }

    public Converter getVNIConverter() {
        return VNIConverter;
    }
}
