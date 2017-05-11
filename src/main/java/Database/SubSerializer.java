package Database;

import Substitution.EquationSub;

import java.io.*;

/**
 * Created by jack on 1/9/2017.
 */
public class SubSerializer {
    public static byte[] serialize(EquationSub sub) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(sub);
            out.flush();
            return bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
    public static EquationSub deserialize(byte[] bytes) throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (EquationSub) in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
}
