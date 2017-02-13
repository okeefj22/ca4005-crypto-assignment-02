# CA4005 Assignment 2: Digital Signature Using RSA
http://www.computing.dcu.ie/%7Ehamilton/teaching/CA4005/Assignments/Assignment2.html

**Mark received: 15/15**

## Spec

The aim of this assignment is to implement a digital signature using RSA. Before the digital signature can be implemented, you will need to set up an appropriate public/private RSA key pair. This should be done as follows:

1. Generate two distinct 512-bit probable primes p and q
2. Calculate the product of these two primes N = pq
3. Calculate the Euler totient function phi(N)
4. You will be using an encryption exponent e = 65537, so you will need to ensure that this is relatively prime to phi(N). If it is not, go back to step 1 and generate new values for p and q
5. Compute the value for the decryption exponent d, which is the multiplicative inverse of e (mod phi(N)). This should use your own implementation of the extended Euclidean GCD algorithm to calculate the inverse rather than using a library method for this purpose.

You should then write code to implement a decryption method which calculates cd (mod N). You should use your own implementation of the Chinese Remainder Theorem to calculate this more efficiently; this can also make use of your multiplicative inverse implementation.

Once your implementation is complete, you should create a zip file containing all your code and digitally sign a digest of this file as follows:

1. Generate a 256-bit digest of the zip file using SHA-256.
2. Apply your decryption method to this digest. Note that for the purpose of this assignment no padding should be added to the digest.

You should send me the following by email:

- Your 1024-bit modulus N in hexadecimal.
- Your zipped code file.
- The digitally signed code digest in hexadecimal.
- A declaration that this is solely your own work (except elements that are explicitly attributed to another source).

The implementation language must be Java. You can make use of the BigInteger class (java.math.BigInteger), the security libraries (java.security.*) and the crypto libraries (javax.crypto.*). You must not make use of the multiplicative inverse or GCD methods provided by the BigInteger class; you will need to implement these yourself. You can however make use of the crypto libraries to perform the SHA-256 hashing.

When I receive your email I will generate a 256-bit digest of your zipped code file using SHA-256 and decrypt your digital signature using the exponent e = 65537 and modulus N; these two values should be the same for a correct submission.

This assignment is due 10am on Monday 5th December. Submissions without the declaration will not be assessed. This assignment carries 15 marks and late submissions will be penalised 1.5 marks for each 24 hours the assignment is overdue.
