package it.unipi.lab3.abalderi1;

public class ColoredStringConverter {

    // Codici ANSI per colori
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static String convert(String word, String pattern) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            if (i < word.length()) {
                char c = word.charAt(i);
                switch (pattern.charAt(i)) {
                    case '?' -> result.append(ANSI_YELLOW).append(c).append(ANSI_RESET);
                    case 'X' -> result.append(ANSI_RED).append(c).append(ANSI_RESET);
                    case '+' -> result.append(ANSI_GREEN).append(c).append(ANSI_RESET);
                    default -> result.append(c);
                }
            } else {
                switch (pattern.charAt(i)) {
                    case 'X' -> result.append(ANSI_RED).append("X").append(ANSI_RESET);
                    case '+' -> result.append(ANSI_GREEN).append("+").append(ANSI_RESET);
                    default -> result.append(pattern.charAt(i));
                }
            }
        }
        return result.toString();
    }
}
