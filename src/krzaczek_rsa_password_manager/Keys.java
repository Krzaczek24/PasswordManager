package krzaczek_rsa_password_manager;

import java.math.BigInteger;
import java.util.HashMap;

public final class Keys
{
    private final HashMap <String,BigInteger> data;
    
    public Keys()
    {
        data = new HashMap<>();
    }
    
    public Keys(int bits)
    {
        RSA rsa_keygen = new RSA(bits / 2);
        ElGamal elGamal_keygen = new ElGamal(bits);
        
        data = new HashMap<>();
        
        setRSApublicKey(rsa_keygen.getPublicKey());
        setRSAprivateKey(rsa_keygen.getPrivateKey());
        setElGamalPublicKey(elGamal_keygen.getPublicKey());
        setElGamalPrivateKey(elGamal_keygen.getPrivateKey());
    }
    
    public void setRSApublicKey(String[] input)
    {
        data.put("e", new BigInteger(input[0]));
        data.put("n", new BigInteger(input[1]));
    }
    
    public void setRSAprivateKey(String[] input)
    {
        data.put("d", new BigInteger(input[0]));
        data.put("n", new BigInteger(input[1]));
    }
    
    public void setElGamalPublicKey(String[] input)
    {
        data.put("b", new BigInteger(input[0]));
        data.put("g", new BigInteger(input[1]));
        data.put("p", new BigInteger(input[2]));
    }
    
    public void setElGamalPrivateKey(String[] input)
    {
        data.put("a", new BigInteger(input[0]));
        data.put("g", new BigInteger(input[1]));
        data.put("p", new BigInteger(input[2]));
    }
    
    public BigInteger[] getRSApublicKey()
    {
        BigInteger key[] = { data.get("e"), data.get("n") };
        return key;
    }
    
    public BigInteger[] getRSAprivateKey()
    {
        BigInteger key[] = { data.get("d"), data.get("n") };
        return key;
    }
    
    public BigInteger[] getElGamalPublicKey()
    {
        BigInteger key[] = { data.get("b"), data.get("g"), data.get("p") };
        return key;
    }
    
    public BigInteger[] getElGamalPrivateKey()
    {
        BigInteger key[] = { data.get("a"), data.get("g"), data.get("p") };
        return key;
    }
    
    public String RSApublicKeyToString()
    {
        return data.get("e").toString() + "," + data.get("n").toString();
    }
    
    public String RSAprivateKeyToString()
    {
        return data.get("d").toString() + "," + data.get("n").toString();
    }
    
    public String ElGamalPublicKeyToString()
    {
        return data.get("b").toString() + "," + data.get("g").toString() + "," + data.get("p").toString();
    }
    
    public String ElGamalPrivateKeyToString()
    {
        return data.get("a").toString() + "," + data.get("g").toString() + "," + data.get("p").toString();
    }
}
