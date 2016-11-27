import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import static java.util.Collections.singletonList;


public class CA4005Assignment02 {

    // left to right variant of the square and multiply algorithm
    private static BigInteger modExp(BigInteger a, BigInteger exp, BigInteger mod) {
        int n = exp.bitLength();
        BigInteger y = BigInteger.ONE;
        for (int i = n-1; i >= 0; i--) {
            y = y.multiply(y).mod(mod);
            if (exp.testBit(i)) {
                y = y.multiply(a).mod(mod);
            }
        }
        return y;
    }

    // given gcd(a,b) = ax + by, the following function returns [x, y, gcd(a,b)]
    private static BigInteger[] xgcd(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) return new BigInteger[] {BigInteger.ONE, BigInteger.ZERO, a};

        BigInteger[] xyd = xgcd(b, a.mod(b));
        return new BigInteger[] {xyd[1], xyd[0].subtract((a.divide(b)).multiply(xyd[1])), xyd[2]};
    }

    // calculate the multiplicative inverse of some a mod b using xgcd
    private static BigInteger multInv(BigInteger a, BigInteger b) {
        BigInteger[] tmp = xgcd(a, b);
        return tmp[0];
    }

    private static BigInteger decrypt(BigInteger c, BigInteger d, BigInteger p, BigInteger q) {
        // calculate Cp and Cq
        BigInteger cp = modExp(c, d.mod(p.subtract(BigInteger.ONE)), p);
        BigInteger cq = modExp(c, d.mod(q.subtract(BigInteger.ONE)), q);

        // calculate the multiplicative inverse of q (mod p)
        BigInteger qInv = multInv(q, p);

        // use Chinese remainder theorem
        return cq.add(q.multiply((qInv.multiply(cp.subtract(cq))).mod(p)));
    }

    // output a single string to a file using the preferred method as of Java 8
    private static void writeStringToFile(String filepath, String output) throws IOException {
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> out = singletonList(output);
        Files.write(Paths.get(filepath), out, utf8);
    }

    public static void main(String[] args) {
        BigInteger e = new BigInteger("65537"); // encryption exponent

        BigInteger p;
        BigInteger q;
        BigInteger N; // N = pq
        BigInteger phiN;
        BigInteger[] xEuclid;

        // initialise secure random generator for the generation of p and q
        SecureRandom sRnd = new SecureRandom();

        // generate new values for p and q until phiN and e are coprime
        do {
            // generate probable primes p and q
            p = BigInteger.probablePrime(512, sRnd);
            q = BigInteger.probablePrime(512, sRnd);

            // Euler totient phi(N) = (p-1)(q-1) if p and q are both prime
            N = p.multiply(q);
            phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

            // e and phiN are relatively prime if their gcd is 1
            xEuclid = xgcd(e, phiN);
        } while (!(xEuclid[2].equals(BigInteger.ONE)));

        BigInteger d = xEuclid[0]; // decryption exponent

        try {
            // get digest of input file as a positive BigInteger
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            BigInteger c = new BigInteger(1, md.digest(Files.readAllBytes(Paths.get(args[0]))));

            // sign the digest using my private key
            BigInteger signedDigest = decrypt(c, d, p, q);

            // write out N and digitally signed code digest to files in hex format
            writeStringToFile("modulo-N", N.toString(16));
            writeStringToFile("signed-digest", signedDigest.toString(16));

        } catch ( IOException
                | NoSuchAlgorithmException err) {

            err.printStackTrace();
        }
    }
}
