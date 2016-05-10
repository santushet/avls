/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package avlslistening;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @author sai
 */
public class Main {

    public static String dataip, port, companyname, builddate,versionno,driver, url, username, password;
    public static int maxConnections,initialConnections;
    public static boolean e_dispatch=false;
    static Runtime r;
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here

        try {
            r = Runtime.getRuntime();
            r.maxMemory();
            java.io.File currentDir = new java.io.File("");

            File file = new File(currentDir.getAbsolutePath() + "/Config.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeLst = doc.getElementsByTagName("SETTING");
            
            for (int s = 0; s < nodeLst.getLength(); s++) {
                Node fstNode = nodeLst.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element Elmnts = (Element) fstNode;

                    // DATAIP
                    NodeList DataIPNmElmntLst = Elmnts.getElementsByTagName("DATA_IP");
                    Element DataIPElmnt = (Element) DataIPNmElmntLst.item(0);
                    NodeList DataIPNm = DataIPElmnt.getChildNodes();
                    dataip = ((Node) DataIPNm.item(0)).getNodeValue();

                    //DATAPORT
                    NodeList DataPORTNmElmntLst = Elmnts.getElementsByTagName("DATA_PORT");
                    Element DataPORTNmElmnt = (Element) DataPORTNmElmntLst.item(0);
                    NodeList DataPORTNm = DataPORTNmElmnt.getChildNodes();
                    port= ((Node) DataPORTNm.item(0)).getNodeValue();

                    // COMPANY NAME
                    NodeList CNameNmElmntLst = Elmnts.getElementsByTagName("COMPANY_NAME");
                    Element CNameNmElmnt = (Element) CNameNmElmntLst.item(0);
                    NodeList CNameNm = CNameNmElmnt.getChildNodes();
                    companyname =  ((Node) CNameNm.item(0)).getNodeValue();

                    // BUILD DATE
                    NodeList BuildNmElmntLst = Elmnts.getElementsByTagName("BUILD_DATE");
                    Element BuildNmElmnt = (Element) BuildNmElmntLst.item(0);
                    NodeList BuildNm = BuildNmElmnt.getChildNodes();
                    builddate = ((Node) BuildNm.item(0)).getNodeValue();

                    // VERSIONNO
                    NodeList VersionNmElmntLst = Elmnts.getElementsByTagName("VERSIONNO");
                    Element VersionNmElmnt = (Element) VersionNmElmntLst.item(0);
                    NodeList VersionNm = VersionNmElmnt.getChildNodes();
                    versionno = ((Node) VersionNm.item(0)).getNodeValue();

                    // Driver Key
                    NodeList DriverNmElmntLst = Elmnts.getElementsByTagName("DRIVER");
                    Element DriverElmnt = (Element) DriverNmElmntLst.item(0);
                    NodeList DriverNm = DriverElmnt.getChildNodes();
                    driver = ((Node) DriverNm.item(0)).getNodeValue();

                    //URL
                    NodeList URLNmElmntLst = Elmnts.getElementsByTagName("URL");
                    Element URLNmElmnt = (Element) URLNmElmntLst.item(0);
                    NodeList URLNm = URLNmElmnt.getChildNodes();
                    url = "" + ((Node) URLNm.item(0)).getNodeValue();

                    // IP
                    NodeList IPNmElmntLst = Elmnts.getElementsByTagName("DB_IP");
                    Element IPNmElmnt = (Element) IPNmElmntLst.item(0);
                    NodeList IPNm = IPNmElmnt.getChildNodes();
                    //url = url + ((Node) IPNm.item(0)).getNodeValue() + "/";
                    url = url + ((Node) IPNm.item(0)).getNodeValue() + ";";

                    // DB Name
                    NodeList DBNmElmntLst = Elmnts.getElementsByTagName("DB_NAME");
                    Element DBNmElmnt = (Element) DBNmElmntLst.item(0);
                    NodeList DBNm = DBNmElmnt.getChildNodes();
                   // url = url + ((Node) DBNm.item(0)).getNodeValue();// "?user=";
                     url = url + "databaseName="+ ((Node) DBNm.item(0)).getNodeValue() + ";user=";
                    // USER ID
                    NodeList UserNmElmntLst = Elmnts.getElementsByTagName("DB_USER");
                    Element UserNmElmnt = (Element) UserNmElmntLst.item(0);
                    NodeList UserNm = UserNmElmnt.getChildNodes();
                    url = url + ((Node) UserNm.item(0)).getNodeValue()+ ";password=";
                    username = ((Node) UserNm.item(0)).getNodeValue();

                    // PASSWORD
                    NodeList PwdNmElmntLst = Elmnts.getElementsByTagName("DB_PWD");
                    Element PwdNmElmnt = (Element) PwdNmElmntLst.item(0);
                    NodeList PwdNm = PwdNmElmnt.getChildNodes();
                    url = url + ((Node) PwdNm.item(0)).getNodeValue()+ ";";
                    password = ((Node) PwdNm.item(0)).getNodeValue();

                    //MIN CONNECTIONS
                    NodeList MINNmElmntLst = Elmnts.getElementsByTagName("MIN_CONNECTION");
                    Element MINNmElmnt = (Element) MINNmElmntLst.item(0);
                    NodeList MINNm = MINNmElmnt.getChildNodes();
                    initialConnections = Integer.parseInt(((Node) MINNm.item(0)).getNodeValue());

                    //MAXCONNECTIONS
                    NodeList MAXNmElmntLst = Elmnts.getElementsByTagName("MAX_CONNECTION");
                    Element MAXNmElmnt = (Element) MAXNmElmntLst.item(0);
                    NodeList MAXNm = MAXNmElmnt.getChildNodes();
                    maxConnections = Integer.parseInt(((Node) MAXNm.item(0)).getNodeValue());

                    //EnableDispatch
                    NodeList DisNmElmntLst = Elmnts.getElementsByTagName("ENABLE_DISPATCH");
                    Element DisNmElmnt = (Element) DisNmElmntLst.item(0);
                    NodeList DisNm = DisNmElmnt.getChildNodes();
                    e_dispatch =Boolean.parseBoolean(((Node) DisNm.item(0)).getNodeValue());

                    System.out.println("URL "+url);

                   
                    String[] sport = port.split(",");
                    ExecutorService executor = Executors.newFixedThreadPool(sport.length+6);

                    System.out.println(sport[0].toString());

                    for(int i=0;i<sport.length;i++)
                    {
			Runnable dl = new DataListener(Integer.parseInt(sport[i]));
                        executor.execute(dl);

                    }
                    System.out.println(companyname+ " AVLS Application Verion "+versionno);


                     Runnable cs = new ClosingSockets();
                     executor.execute(cs);


                    if(e_dispatch)
                    {

                        System.out.println("inside the disptch true");

                        Runnable dDis = new DataDispatch();
                        executor.execute(dDis);

                    }
                }
            }

        } catch (Exception e) {
            LogMessage.errorfilecreation("Error inside the InsertResponseMessage:- " + e.getMessage());
            gc();
        }

    }
    public static void gc() {
   Object obj = new Object();
   java.lang.ref.WeakReference ref = new java.lang.ref.WeakReference<>(obj);
   //Runtime r = Runtime.getRuntime();
   r.freeMemory();
   obj = null;
   while(ref.get() != null) {
       r.gc();
        //System.gc();
   }
}



}
