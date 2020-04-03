public class CharSet {
    public static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String DIGIT = "0123456789";
    public static final String SPECAIL = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private char[] charSet;
    private int index;

    public CharSet(String charSet) {
        this.charSet = charSet.toCharArray();
        index = 0;
    }

    public CharSet(char[] charSet) {
        this.charSet = charSet;
    }

    public char getChar() {
        return charSet[index];
    }

    public boolean hasNext() {
        return charSet[index] != charSet[charSet.length - 1];
    }

    public boolean setNext() {
        boolean flag = hasNext();
        if (flag) {
            index++;
        } else {
            index = 0;
        }
        return flag;
    }
}
