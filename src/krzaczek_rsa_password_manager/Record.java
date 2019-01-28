package krzaczek_rsa_password_manager;

import java.io.Serializable;
import java.math.BigInteger;

public final class Record implements Serializable
{
    private String resource;
    private String email;
    private String name;
    private String nickname;
    private String password;

    public Record(String data[])
    {
        for(int i=0; i<data.length; i++)
        {
            if(data[i] == null) data[i] = "none";
            else if(data[i].length() <= 0) data[i] = "none";
        }
        
        resource = data[0].toUpperCase();
        email = data[1];
        name = data[2];
        nickname = data[3];
        password = data[4];
    }
    
    public Record()
    {
        resource = "NONE";
        email = "none";
        name = "none";
        nickname = "none";
        password = "none";
    }

    public String getResource() { return resource; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getNickname() { return nickname; }
    public String getPassword() { return password; } 

    public void setResource(String resource) { this.resource = resource; }
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setPassword(String password) { this.password = password; }
    
    public void encrypt(BigInteger keys[])
    {
        resource = RSA.encrypt(resource, keys);
        email = RSA.encrypt(email, keys);
        name = RSA.encrypt(name, keys);
        nickname = RSA.encrypt(nickname, keys);
        password = RSA.encrypt(password, keys);
    }
    
    public void decrypt(BigInteger keys[])
    {
        resource = RSA.decrypt(resource, keys);
        email = RSA.decrypt(email, keys);
        name = RSA.decrypt(name, keys);
        nickname = RSA.decrypt(nickname, keys);
        password = RSA.decrypt(password, keys);
    }
    
    @Override
    public String toString()
    {
        return resource;
    }
    
    public Boolean isIdentical(String[] data)
    {
        if(data[0].equals("")) data[0] = "NONE";
        for(int i=1; i<data.length; i++)
        {
            if(data[i].equals("")) data[i] = "none";
        }
        
        return resource.equals(data[0])
            && email.equals(data[1])
            && name.equals(data[2])
            && nickname.equals(data[3])
            && password.equals(data[4]);
    }
}
