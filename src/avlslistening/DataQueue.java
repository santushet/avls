/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package avlslistening;
import java.util.*;
/**
 *
 * @author sai
 */
public class DataQueue {

    private static DataQueue dataQuesingleton = new DataQueue();
    public static Queue<String> qe=new LinkedList<>();
    private DataQueue()
    {
        
    }
    public static DataQueue getInstance() {
        return dataQuesingleton;
    }

    public void storeData(String data)
    {
       //System.out.println("Data added in queue   "+data);
        qe.add(data);

    }
    public String getData()
    {
        return(qe.poll());
    }
    


}
