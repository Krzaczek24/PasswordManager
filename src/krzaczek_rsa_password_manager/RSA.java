package krzaczek_rsa_password_manager;

import java.math.BigInteger;
import java.util.Random;

public final class RSA
{
    private final Integer BITS;
    private final BigInteger N;
    private final BigInteger O;
    private final BigInteger E;
    private final BigInteger D;
    
    public RSA(int bits)
    {
        BITS = bits;
        BigInteger P = BigInteger.probablePrime(BITS, new Random());
        BigInteger Q = BigInteger.probablePrime(BITS, new Random());
        N = P.multiply(Q);
        O = (P.add(BigInteger.ONE.negate())).multiply((Q.add(BigInteger.ONE.negate())));
        E = relativelyPrime();
        D = E.modInverse(O);
    }
    
    private BigInteger relativelyPrime()
    {
        BigInteger result;
        
        do { result = BigInteger.probablePrime(BITS, new Random()); }
        while(!isRelativelyPrime(result, O));
        
        return result;
    }
    
    private Boolean isRelativelyPrime(BigInteger a, BigInteger b)
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

    public String[] getPrivateKey()
    {
        String keys[] = {D.toString(), N.toString()};
        return keys;
    }
    
    public String[] getPublicKey()
    {
        String keys[] = {E.toString(), N.toString()};
        return keys;
    }
    
        public static String encrypt(String message, BigInteger keys[])
    {
        String input = "";
        
        for(int i : message.toCharArray())
        {
            int end = 2 - (int)Math.log10(i);
            for(int j=0; j<end; j++) input += "0";
            
            input += i;
        }
        
        return new BigInteger(input).modPow(keys[0], keys[1]).toString();
    }
    
    public static String decrypt(String input, BigInteger keys[])
    {
        String code = (new BigInteger(input)).modPow(keys[0], keys[1]).toString();
        String output = "";

        if(code.length() % 3 == 1) code = "00".concat(code);
        else if(code.length() % 3 == 2) code = "0".concat(code);

        for(int i = 0; i < code.length(); i+=3)
        {
            output += (char)(Integer.parseInt(code.substring(i, i+3)));
        }
        
        return output;
    }
}
