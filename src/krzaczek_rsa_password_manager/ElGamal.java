package krzaczek_rsa_password_manager;

import java.math.BigInteger;
import java.util.Random;

public final class ElGamal
{
    private final Integer BITS;
    private final BigInteger P;
    private final BigInteger A;
    private final BigInteger G;
    private final BigInteger B;
    
    public ElGamal(int bits)
    {
        BITS = bits;
        P = BigInteger.probablePrime(BITS, new Random());
        Long a, g;
        
        Random rand = new Random();
        do { g = rand.nextLong() + 1; }
        while(g <= 1 || BigInteger.valueOf(g).compareTo(P.add(BigInteger.ONE.negate())) >= 0);
        G = BigInteger.valueOf(g);
        
        do { a = rand.nextLong() + 1; }
        while(a <= 1 || BigInteger.valueOf(a).compareTo(P.add(BigInteger.ONE.negate())) >= 0);
        A = BigInteger.valueOf(a);
        
        B = G.modPow(A, P);
    }
    
    public String[] getPrivateKey()
    {
        String keys[] = {A.toString(), G.toString(), P.toString()};
        return keys;
    }
    
    public String[] getPublicKey()
    {
        String keys[] = {B.toString(), G.toString(), P.toString()};
        return keys;
    }
    
        public static String encrypt(String message, BigInteger keys[])
    {
        String input[] = message.split(",");
        
        BigInteger k = getK(keys);
        BigInteger c1 = keys[1].modPow(k, keys[2]);
        BigInteger c2a = new BigInteger(input[0]).multiply(keys[0].modPow(k, keys[2])).remainder(keys[2]);
        BigInteger c2b = new BigInteger(input[1]).multiply(keys[0].modPow(k, keys[2])).remainder(keys[2]);
        
        return c1.toString() + "," + c2a.toString() + "," + c2b.toString();
    }
    
    public static String decrypt(String input, BigInteger keys[])
    {
        String codes[] = input.split(",");
        BigInteger c1 = new BigInteger(codes[0]);
        BigInteger c2a = new BigInteger(codes[1]);
        BigInteger c2b = new BigInteger(codes[2]);

        String output = c2a.multiply(c1.modPow(keys[0], keys[2]).modInverse(keys[2])).remainder(keys[2]).toString()
        + "," + c2b.multiply(c1.modPow(keys[0], keys[2]).modInverse(keys[2])).remainder(keys[2]).toString();

        return output;
    }
    
    private static BigInteger getK(BigInteger keys[])
    {
        BigInteger P = keys[2];
        BigInteger P1 = P.add(BigInteger.ONE.negate());
        BigInteger result;
        
        Random rand = new Random();
        
        int multipler = 3;
        int divider = 4;
        int minBits = (P1.bitLength() * multipler) / divider;
        int rndBits = (P1.bitLength() * (divider - multipler)) / divider;
        
        do
        {
            int bits = rand.nextInt(rndBits) + minBits;
            result = BigInteger.probablePrime(bits, new Random());
        }
        while(!isRelativelyPrime(result, P1) && result.compareTo(P1) >= 0);
        
        return result;
    }
    
    private static Boolean isRelativelyPrime(BigInteger a, BigInteger b)
    {
        while(a.compareTo(BigInteger.ZERO) > 0 
        && b.compareTo(BigInteger.ZERO) > 0)
        {
            if(a.equals(BigInteger.ONE) || b.equals(BigInteger.ONE)) return true;
            else if(a.compareTo(b) > 0) a = a.remainder(b);
            else b = b.remainder(a);
        }
        
        return false;
    }
}
