package krzaczek_rsa_password_manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public final class FileManager
{
    public static ArrayList <Record> loadData(String dat, String pri)
    {
        Keys keys = loadKeys(dat, pri);
        ArrayList <Record> data = new ArrayList<>();
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dat)))
        {
            in.readUTF(); //skip public RSA key
            in.readUTF(); //skip public ElGamal key
            in.readUTF(); //skip private ElGamal key
            
            Record record;
            while((record = (Record)in.readObject()) != null) data.add(record);
            
            in.close();
        }
        catch (FileNotFoundException ex) { System.err.println("Nie znaleziono pliku danych"); }
        catch (IOException ex) { }
        catch (ClassNotFoundException ex) { System.err.println("Zła struktura pliku"); }

        data.forEach((record) -> { record.decrypt(keys.getRSAprivateKey()); });
        
        return data;
    }
    
    public static void saveData(ArrayList <Record> data, String dat, String pri)
    {
        Keys keys = loadKeys(dat, pri);

        data.forEach((record) -> { record.encrypt(keys.getRSApublicKey()); });
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dat)))
        {
            out.writeUTF(keys.RSApublicKeyToString());
            out.writeUTF(keys.ElGamalPublicKeyToString());
            out.writeUTF(keys.ElGamalPrivateKeyToString());
            for(Record record : data) out.writeObject(record);
            out.close();
        }
        catch (FileNotFoundException ex) { System.err.println("Nie znaleziono pliku zapisu danych"); }
        catch (IOException ex) { System.err.println("Podczas zapisu danych wystąpił błąd w pliku"); }
        
        data.forEach((record) -> { record.decrypt(keys.getRSAprivateKey()); });
    }

    public static Keys loadKeys(String pub, String pri)
    {
        Keys keys = new Keys();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(pub)))
        {
            keys.setRSApublicKey(in.readUTF().split(","));
            keys.setElGamalPublicKey(in.readUTF().split(","));
            keys.setElGamalPrivateKey(in.readUTF().split(","));
            in.close();
        }
        catch (FileNotFoundException ex) { System.err.println("Nie znaleziono pliku odczytu klucza publicznego"); }
        catch (IOException ex) { System.err.println("Podczas odczytu klucza publicznego wystąpił błąd w pliku"); }
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(pri)))
        {
            String dec = in.readUTF();
            keys.setRSAprivateKey(ElGamal.decrypt(dec, keys.getElGamalPrivateKey()).split(","));
            in.close();
        }
        catch (FileNotFoundException ex) { System.err.println("Nie znaleziono pliku odczytu klucza prywatnego"); }
        catch (IOException ex) { System.err.println("Podczas odczytu klucza prywatnego wystąpił błąd w pliku"); }

        return keys;
    }
    
    public static void generateKeys(String pub, String pri, int bits)
    {
        Keys keys = new Keys(bits);

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pub)))
        {
            out.writeUTF(keys.RSApublicKeyToString());
            out.writeUTF(keys.ElGamalPublicKeyToString());
            out.writeUTF(keys.ElGamalPrivateKeyToString());
            out.close();
        }
        catch (FileNotFoundException ex) { System.err.println("Nie znaleziono pliku zapisu klucza publicznego"); }
        catch (IOException ex) { System.err.println("Podczas zapisu klucza publicznego wystąpił błąd w pliku"); }
        
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pri)))
        {
            out.writeUTF(ElGamal.encrypt(keys.RSAprivateKeyToString(), keys.getElGamalPublicKey()));
            out.close();
        }
        catch (FileNotFoundException ex) { System.err.println("Nie znaleziono pliku zapisu klucza prywatnego"); }
        catch (IOException ex) { System.err.println("Podczas zapisu klucza prywatnego wystąpił błąd w pliku"); }
    }
}
