import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class Main {
    private static final int MD5_LENGTH = 32;
    private static final int SHA256_LENGTH = 64;
    private static final int SHA512_LENGTH = 128;

    private static final int MIN_SIZE = 6;
    private static final int MAX_SIZE = 9;
    private static final int CUSTOM_LOOPS = 100;
    private static final int INSIGHT_GRANULARITY = 50000;
    private static final String[] charSets = new String[]{CharSet.LOWER, CharSet.UPPER, CharSet.DIGIT, CharSet.SPECAIL};
    private static final String PASS_HASH = "0281041f839d8c4486ff4ae17e5daf0d6c331fb1db8c91927719049d5fad578a7f14a619fba9107d01221cb1e056de83cfac3b86748b7af6e0d19c060c8d0012";

    private static WordGen word;
    private static MessageDigest md5;
    private static MessageDigest sha256;
    private static MessageDigest sha512;
    private static String charSetString;
    private static ArrayList<String> possiblePasswords = new ArrayList<>();

    private static int numberOfTests = 0;
    private static String estimate = "-";
    private static Instant START = Instant.now();
    private static Instant lastItter = Instant.now();
    private static char lastChar;


    public static void main(String[] args) {
        init();
        setCharSet();
        sanityChecks();
        crackHash();
        printPossiblePasswords();
    }

    private static void printPossiblePasswords() {
        if (possiblePasswords.size() > 0) {
            System.out.println("PASSWORD CRACKED!");
        } else {
            System.out.println("Password Not cracked...");
            System.out.println("All combinations tried");
            System.out.println(String.format("CharSet: %s", charSetString));
        }
        for (String password : possiblePasswords) {
            System.out.println(String.format("Possible Password: %s", password));
        }
    }

    private static void crackHash() {
        for (int i = MIN_SIZE; i <= MAX_SIZE; i++) {
            crackHashForLength(i);
        }
    }

    private static void crackHashForLength(int size) {
        setWords(size);
        String hash;
        do {
            hash = customHash(word.get());
            if (PASS_HASH.equals(hash)) {
                System.out.println("\n\nCRACKED!!");
                System.out.println(String.format("PASSWORD: %s\n\n", word.get()));
                possiblePasswords.add(word.get());
            }
            incTests();
        } while (word.setNext());
    }

    private static void incTests() {
        numberOfTests++;
        if (numberOfTests > INSIGHT_GRANULARITY) {
            System.out.println(String.format("Testing Word: %s\tSolutions: %d\tRuntime: %s\tEstimate: %s",
                    word.get(),
                    possiblePasswords.size(),
                    getRunTime(),
                    getDoneEstimate()));
            System.out.println("Pass: " + possiblePasswords);
            numberOfTests = 0;
        }
    }

    private static String getDoneEstimate() {
        String currentWord = word.get();
        char currentLastChar = currentWord.charAt(currentWord.length() - 1);
        if (lastChar != currentLastChar) {
            updateEstimate();
            lastChar = currentLastChar;
        }
        return estimate;
    }

    private static void updateEstimate() {
        DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
        Instant now = Instant.now();
        long millsEstimate = Duration.between(lastItter, now).toMillis();
        millsEstimate *= charSetString.length();
        millsEstimate += System.currentTimeMillis();
        Date date = new Date(millsEstimate);

        estimate = df.format(date);
        lastItter = now;
    }

    private static String getRunTime() {
        Duration currentRunTime = Duration.between(START, Instant.now());
        return String.format("D:%d H:%d M:%d S:%d",
                currentRunTime.toDays(),
                currentRunTime.toHours() % 24,
                currentRunTime.toMinutes() % 60,
                currentRunTime.getSeconds() % 60);
    }

    private static void setCharSet() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String set : charSets) {
            stringBuilder.append(set);
        }
        charSetString = stringBuilder.toString();
        lastChar = charSetString.charAt(0);
    }

    private static void setWords(int size) {
        word = new WordGen(size, charSetString.toCharArray());
    }

    private static void init() {
        try {
            md5 = MessageDigest.getInstance("MD5");
            sha256 = MessageDigest.getInstance("SHA-256");
            sha512 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not init hash functions");
            System.exit(-1);
        }
    }

    private static void sanityChecks() {
        String md5Hash;
        String sha256Hash;
        String sha512Hash;
        String customHash;
        String customHash3;
        try {
            md5Hash = encrypt("test", md5, MD5_LENGTH);
            sha256Hash = encrypt("test", sha256, SHA256_LENGTH);
            sha512Hash = encrypt("test", sha512, SHA512_LENGTH);
            customHash = encrypt(encrypt(encrypt("test", md5, MD5_LENGTH), sha256, SHA256_LENGTH), sha512, SHA512_LENGTH);
            customHash3 = loopHash(loopHash(loopHash("test", md5, 3, MD5_LENGTH), sha256, 3, SHA256_LENGTH), sha512, 3, SHA512_LENGTH);
            if (!md5Hash.equals("098f6bcd4621d373cade4e832627b4f6")) {
                throw new Exception();
            }
            if (!sha256Hash.equals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")) {
                throw new Exception();
            }
            if (!sha512Hash.equals("ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff")) {
                throw new Exception();
            }
            if (!customHash.equals("cd9c41eac82aef04609736760e0f6131efafe49f139f10e7371b1939c2cb372243caeca013f0cdf2bf659bd868b7c0b5459c3631e46feaca20d9c8ff6d57ae7e")) {
                throw new Exception();
            }
            if (!customHash3.equals("fd577e659aca5ab14d251e3687edec002ee723032759b87d67f8d1f9bb74a8b49e3a43ec7c949a90abbe5165a11ca5d9f562f7ce56b499e5c831325a373b6223")) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Hash functions will not initialize!");
            System.exit(-1);
        }
    }

    private static String customHash(String input) {
        String hash;
        hash = loopHashDefault(input, md5, MD5_LENGTH);
        hash = loopHashDefault(hash, sha256, SHA256_LENGTH);
        hash = loopHashDefault(hash, sha512, SHA512_LENGTH);
        return hash;
    }

    private static String loopHashDefault(String input, MessageDigest hashType, int hashLength) {
        return loopHash(input, hashType, CUSTOM_LOOPS, hashLength);
    }

    private static String loopHash(String input, MessageDigest hashType, int loopNum, int hashLength) {
        String hash = input;
        for (int i = 0; i < loopNum; i++) {
            hash = encrypt(hash, hashType, hashLength);
        }
        return hash;
    }

    private static String encrypt(String input, MessageDigest hashType, int length) {
        return toHexString(hashType.digest(input.getBytes()), length);
    }

    private static String toHexString(byte[] hash, int neededLength) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < neededLength) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

}
