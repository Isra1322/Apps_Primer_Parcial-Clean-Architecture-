package fisei.uta.edu.ec.flagquiz.presentation.util;

public final class FlagNameFormatter {
    private FlagNameFormatter() {
    }

    public static String getCountryName(String name) {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }

    public static String getRegion(String name) {
        return name.substring(0, name.indexOf('-'));
    }
}
