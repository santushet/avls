import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.CallableStatement;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

public class Main {

	public static final RethinkDB r = RethinkDB.r;

	Connection con_avls = null;
	private final Object LOCK = true;
	public static String dataip, port, companyname, builddate, versionno,
			driver, url, username, password;
	public static int maxConnections, initialConnections;
	public static boolean e_dispatch = false;
	static Runtime javaRuntime;

	public static void main(String[] args) {

		// TODO code application logic here

		try {
			javaRuntime = Runtime.getRuntime();
			javaRuntime.maxMemory();
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
					NodeList DataIPNmElmntLst = Elmnts
							.getElementsByTagName("DATA_IP");
					Element DataIPElmnt = (Element) DataIPNmElmntLst.item(0);
					NodeList DataIPNm = DataIPElmnt.getChildNodes();
					dataip = ((Node) DataIPNm.item(0)).getNodeValue();

					// DATAPORT
					NodeList DataPORTNmElmntLst = Elmnts
							.getElementsByTagName("DATA_PORT");
					Element DataPORTNmElmnt = (Element) DataPORTNmElmntLst
							.item(0);
					NodeList DataPORTNm = DataPORTNmElmnt.getChildNodes();
					port = ((Node) DataPORTNm.item(0)).getNodeValue();

					// COMPANY NAME
					NodeList CNameNmElmntLst = Elmnts
							.getElementsByTagName("COMPANY_NAME");
					Element CNameNmElmnt = (Element) CNameNmElmntLst.item(0);
					NodeList CNameNm = CNameNmElmnt.getChildNodes();
					companyname = ((Node) CNameNm.item(0)).getNodeValue();

					// BUILD DATE
					NodeList BuildNmElmntLst = Elmnts
							.getElementsByTagName("BUILD_DATE");
					Element BuildNmElmnt = (Element) BuildNmElmntLst.item(0);
					NodeList BuildNm = BuildNmElmnt.getChildNodes();
					builddate = ((Node) BuildNm.item(0)).getNodeValue();

					// VERSIONNO
					NodeList VersionNmElmntLst = Elmnts
							.getElementsByTagName("VERSIONNO");
					Element VersionNmElmnt = (Element) VersionNmElmntLst
							.item(0);
					NodeList VersionNm = VersionNmElmnt.getChildNodes();
					versionno = ((Node) VersionNm.item(0)).getNodeValue();

					// Driver Key
					NodeList DriverNmElmntLst = Elmnts
							.getElementsByTagName("DRIVER");
					Element DriverElmnt = (Element) DriverNmElmntLst.item(0);
					NodeList DriverNm = DriverElmnt.getChildNodes();
					driver = ((Node) DriverNm.item(0)).getNodeValue();

					// URL
					NodeList URLNmElmntLst = Elmnts.getElementsByTagName("URL");
					Element URLNmElmnt = (Element) URLNmElmntLst.item(0);
					NodeList URLNm = URLNmElmnt.getChildNodes();
					url = "" + ((Node) URLNm.item(0)).getNodeValue();

					// IP
					NodeList IPNmElmntLst = Elmnts
							.getElementsByTagName("DB_IP");
					Element IPNmElmnt = (Element) IPNmElmntLst.item(0);
					NodeList IPNm = IPNmElmnt.getChildNodes();
					// url = url + ((Node) IPNm.item(0)).getNodeValue() + "/";
					url = url + ((Node) IPNm.item(0)).getNodeValue() + ";";

					// DB Name
					NodeList DBNmElmntLst = Elmnts
							.getElementsByTagName("DB_NAME");
					Element DBNmElmnt = (Element) DBNmElmntLst.item(0);
					NodeList DBNm = DBNmElmnt.getChildNodes();
					// url = url + ((Node) DBNm.item(0)).getNodeValue();//
					// "?user=";
					url = url + "databaseName="
							+ ((Node) DBNm.item(0)).getNodeValue() + ";user=";
					// USER ID
					NodeList UserNmElmntLst = Elmnts
							.getElementsByTagName("DB_USER");
					Element UserNmElmnt = (Element) UserNmElmntLst.item(0);
					NodeList UserNm = UserNmElmnt.getChildNodes();
					url = url + ((Node) UserNm.item(0)).getNodeValue()
							+ ";password=";
					username = ((Node) UserNm.item(0)).getNodeValue();

					// PASSWORD
					NodeList PwdNmElmntLst = Elmnts
							.getElementsByTagName("DB_PWD");
					Element PwdNmElmnt = (Element) PwdNmElmntLst.item(0);
					NodeList PwdNm = PwdNmElmnt.getChildNodes();
					url = url + ((Node) PwdNm.item(0)).getNodeValue() + ";";
					password = ((Node) PwdNm.item(0)).getNodeValue();

					// MIN CONNECTIONS
					NodeList MINNmElmntLst = Elmnts
							.getElementsByTagName("MIN_CONNECTION");
					Element MINNmElmnt = (Element) MINNmElmntLst.item(0);
					NodeList MINNm = MINNmElmnt.getChildNodes();
					initialConnections = Integer
							.parseInt(((Node) MINNm.item(0)).getNodeValue());

					// MAXCONNECTIONS
					NodeList MAXNmElmntLst = Elmnts
							.getElementsByTagName("MAX_CONNECTION");
					Element MAXNmElmnt = (Element) MAXNmElmntLst.item(0);
					NodeList MAXNm = MAXNmElmnt.getChildNodes();
					maxConnections = Integer.parseInt(((Node) MAXNm.item(0))
							.getNodeValue());

					// EnableDispatch
					NodeList DisNmElmntLst = Elmnts
							.getElementsByTagName("ENABLE_DISPATCH");
					Element DisNmElmnt = (Element) DisNmElmntLst.item(0);
					NodeList DisNm = DisNmElmnt.getChildNodes();
					e_dispatch = Boolean.parseBoolean(((Node) DisNm.item(0))
							.getNodeValue());

					System.out.println("URL " + url);

					String[] sport = port.split(",");
					ExecutorService executor = Executors
							.newFixedThreadPool(sport.length + 6);

					System.out.println(sport[0].toString());

					/*
					 * for(int i=0;i<sport.length;i++) { Runnable dl = new
					 * DataListener(Integer.parseInt(sport[i]));
					 * executor.execute(dl);
					 * 
					 * }
					 */
					System.out.println(companyname
							+ " AVLS Application Verion " + versionno);

					/*
					 * Runnable cs = new ClosingSockets(); executor.execute(cs);
					 */

					/*
					 * if(e_dispatch) {
					 * 
					 * System.out.println("inside the disptch true");
					 * 
					 * Runnable dDis = new DataDispatch();
					 * executor.execute(dDis);
					 * 
					 * }
					 */}
			}

		} catch (Exception e) {
			// LogMessage.errorfilecreation("Error inside the InsertResponseMessage:- "
			// + e.getMessage());
			// gc();
		}

		new Main().startServer();
	}

	public void startServer() {
		final ExecutorService clientProcessingPool = Executors
				.newFixedThreadPool(10);

		Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(1339);
					System.out.println("Waiting for clients to connect...");
					while (true) {
						Socket clientSocket = serverSocket.accept();
						clientProcessingPool
								.submit(new ClientTask(clientSocket));
					}
				} catch (IOException e) {
					System.err.println("Unable to process client request");
					e.printStackTrace();
				}
			}
		};
		Thread serverThread = new Thread(serverTask);
		serverThread.start();
	}

	private class ClientTask implements Runnable {
		private final Socket clientSocket;

		private ClientTask(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			System.out.println("Got a client !");
			try {
				int red = -1;
				byte[] buffer = new byte[5 * 1024]; // a read buffer of 5KiB
				byte[] redData;
				// String clientData = new String();
				String clientData = null;
				while ((red = clientSocket.getInputStream().read(buffer)) > -1) {
					redData = new byte[red];
					System.arraycopy(buffer, 0, redData, 0, red);
					clientData = new String(redData, "UTF-8"); // assumption
																// that client
																// sends data
																// UTF-8 encoded
																// System.out.println("message part recieved:"
																// +
																// clientData);
					// clientData.concat(redDataText);

					/*
					 * System.out.println("Data From Client :" +
					 * clientData.toString());
					 * System.out.println("Data From Client :" + clientData);
					 */

					if (clientData.contains("^TMPER")) {

						System.out.println("String Received " + clientData);
						String[] st = clientData.split("^TMPER");
						for (String dpackt : st) {
							if (!dpackt.equals("")) {
								String datapackt = "^TMPER" + dpackt;
								if (datapackt.contains("^TMPER")) {
									// System.out.println("before calling TMDataPacket");
									TMDataPacket(datapackt);
								} else {

								}
							}
						}

					} else if (clientData.contains("^TMALT")) {/*
																 * 
																 * String[] st =
																 * clientData
																 * .split
																 * ("^TMALT");
																 * for (String
																 * dpackt : st)
																 * { if
																 * (!dpackt.
																 * equals("")) {
																 * String
																 * datapackt =
																 * "^TMALT" +
																 * dpackt; if
																 * (datapackt
																 * .contains
																 * ("^TMALT")) {
																 * System.out
																 * .println(
																 * "before calling TMAlertPacket"
																 * );
																 * TMAlertPacket
																 * (datapackt);
																 * } else {
																 * 
																 * } } }
																 */
					}

				}
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void TMAlertPacket(String datapackt) {
			// TODO Auto-generated method stub

			System.out.println(datapackt);
			String unitNo = "";
			double lait = 0;
			double logit = 0;
			String utcTime = "";
			String utcDate = "";
			String heading = "";
			String speed = "";
			String ignition = "";
			String dinput1 = "";
			String dinput2 = "";
			String analog = "";
			String gOdometer = "";
			String pOdometer = "";
			String mainSupply = "";
			String gValid = "";
			String pStatus = "";
			String[] str = datapackt.split("\\|");
			int len = str.length;
			String dt = "";
			try {
				// ^TMPER|354868055694931|234|12.99829|77.54013|130035|240315|0.0|341.18|1|0|0|0.030|3.7|11.8|0.0|0.0|1|1|0|
				// ^TMPER|868050905399|1313|12.99838|77.54011|164344|070115|0.4||276.64|1|0|0|0.030|4.0|13.0|157.1|3.6|1|1|0|#
				// at : 07-01-2015 22:10:22
				unitNo = str[1];
				lait = Double.parseDouble(str[3].trim());
				logit = Double.parseDouble(str[4].trim());
				utcTime = str[5];
				utcDate = str[6];
				speed = str[7];
				heading = str[8];
				ignition = str[9];
				dinput1 = str[10];
				dinput2 = str[11];
				analog = str[12];
				gOdometer = str[15];
				pOdometer = str[16];
				mainSupply = str[17];
				gValid = str[18];
				pStatus = str[19];

				if (!utcDate.equals("")) {
					utcDate = "20" + utcDate.substring(4, 6) + "-"
							+ utcDate.substring(2, 4) + "-"
							+ utcDate.substring(0, 2);
				}
				if (!utcTime.equals("")) {
					utcTime = utcTime.substring(0, 2) + ":"
							+ utcTime.substring(2, 4) + ":"
							+ utcTime.substring(4, 6);
					dt = utcDate + " " + utcTime;
				}
				// System.out.println(dt);
				DateFormat utcFormat = new SimpleDateFormat(
						"dd-MM-yyyy HH:mm:ss");
				utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date date;
				if (utcTime.equals("") || utcTime.equals(" 00:00:00")) {
					date = new Date();
				} else {
					date = utcFormat.parse(dt);
				}
				DateFormat pstFormat = new SimpleDateFormat(
						"dd-MM-yyyy HH:mm:ss");
				pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
				String utcgmt = "";
				if (gValid.equals("0")) {
					utcgmt = pstFormat.format(date);
					// System.out.println("Date for invalid packet "+utcgmt);
				}

				/*
				 * if (gValid.equals("1")) { InsertTMPacket(unitNo, lait, logit,
				 * dt, speed, heading, Integer.parseInt(ignition),
				 * Integer.parseInt(dinput1), Integer.parseInt(dinput2), analog,
				 * gOdometer, pOdometer, Integer.parseInt(mainSupply),
				 * Integer.parseInt(gValid), Integer.parseInt(pStatus)); } else
				 * { InsertTMPacket(unitNo, lait, logit, dt, speed, heading,
				 * Integer.parseInt(ignition), Integer.parseInt(dinput1),
				 * Integer.parseInt(dinput2), analog, gOdometer, pOdometer,
				 * Integer.parseInt(mainSupply), Integer.parseInt(gValid),
				 * Integer.parseInt(pStatus)); // dp.InsertTMNoGPSPacket(unitNo,
				 * utcgmt, // "Not valid packet"); }
				 */
			} catch (Exception e) {
				System.out.println("4");
				System.out.println("Error message " + e.getMessage());
			}

		}

		public void InsertTMPacket(String unitNo, int pktSeq, double lait,
				double logit, Date dt, String speed, String heading,
				int ignition, int dinput1, int dinput2, String analog,
				String gOdometer, String pOdometer, int mainSupply, int gValid,
				int pStatus) throws IOException, SQLException {
			Connection conn=null;
			try {
				// System.out.println("Inside InsertTMPacket ");
				// con_avls = JDBC.getConnection();

				Date now = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				// utcFormat.setTimeZone(TimeZone.getTimeZone("IST"));

				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				Date currentTime = sdf.parse(sdf.format(now));

				System.out.println(currentTime);

				System.out.println(currentTime.getTime());
				System.out.println(dt.getTime());

				System.out
						.println("(currentTime.getTime()-dt.getTime())/1000) "
								+ ((currentTime.getTime() - dt.getTime()) / 1000));
				System.out.println(((Math.abs(currentTime.getTime()
						- dt.getTime())) / 1000) < 100);

				// rethinkdb connection object
				 conn = r.connection().hostname("localhost")
						.port(28015).timeout(600).connect();

				// mysql connection object
			/*	java.sql.Connection con_avls = JDBC.getConnection();
				System.out.println(con_avls);
*/
				if (!(lait == 0.00000)
						&& !(logit == 0.00000)
						&& ((Math.abs(currentTime.getTime() - dt.getTime())) / 1000) < 100) {

					System.out.println("inside if statement");

					r.table("packet")
							.filter(r.hashMap("unitNo", unitNo))
							.update(r.hashMap("unitNo", unitNo)
									.with("lat", lait).with("lng", logit))
							.run(conn);
					// System.out.println("connection success with 353218071614755");

					/*CallableStatement insertStatement = con_avls
							.prepareCall("{call ListenerTrackMate('" + unitNo
									+ "'," + "" + lait + "," + "" + logit + ","
									+ "'" + dt + "','" + speed + "'," + "'"
									+ heading + "'," + "" + ignition + "," + ""
									+ dinput1 + "," + "" + dinput2 + "," + "'"
									+ analog + "'," + "'" + gOdometer + "',"
									+ "'" + pOdometer + "'," + "" + mainSupply
									+ "," + "" + gValid + "," + "" + pStatus
									+ ") }");
*/
					System.out.println("after procedure execution");
					/*synchronized (LOCK) { // System.out.println(insertStatement);
//						insertStatement.execute();

					}*/
				}

			} catch (Exception se) {
				// LogMessage.errorfilecreation("Error inside the InsertVTSPacket:- "
				// + se.getMessage());
				se.printStackTrace();
			} finally {
				// DBManager.freeConnection(con_avls);
				conn.close();
				System.out.println("connection has been closed- finally");
			}
		}

		public void TMDataPacket(String datapackt) throws IOException {

			// System.out.println(datapackt);
			String unitNo = "";
			double lait = 0;
			double logit = 0;
			String utcTime = "";
			String utcDate = "";
			String heading = "";
			String speed = "";
			String ignition = "";
			String dinput1 = "";
			String dinput2 = "";
			String analog = "";
			String gOdometer = "";
			String pOdometer = "";
			String mainSupply = "";
			String gValid = "";
			String pStatus = "";
			String[] str = datapackt.split("\\|");
			int len = str.length;
			String dt = "";
			int pktSeq = 0;
			try {
				// ^TMPER|354868055694931|234|12.99829|77.54013|130035|240315|0.0|341.18|1|0|0|0.030|3.7|11.8|0.0|0.0|1|1|0|
				// ^TMPER|868050905399|1313|12.99838|77.54011|164344|070115|0.4||276.64|1|0|0|0.030|4.0|13.0|157.1|3.6|1|1|0|#
				// at : 07-01-2015 22:10:22
				unitNo = str[1];
				lait = Double.parseDouble(str[3].trim());
				logit = Double.parseDouble(str[4].trim());
				utcTime = str[5];
				utcDate = str[6];
				speed = str[7];
				heading = str[8];
				ignition = str[9];
				dinput1 = str[10];
				dinput2 = str[11];
				analog = str[12];
				gOdometer = str[15];
				pOdometer = str[16];
				mainSupply = str[17];
				gValid = str[18];
				pStatus = str[19];
				pktSeq = Integer.parseInt(str[2].trim());

				if (!utcDate.equals("")) {
					utcDate = "20" + utcDate.substring(4, 6) + "-"
							+ utcDate.substring(2, 4) + "-"
							+ utcDate.substring(0, 2);
				}
				if (!utcTime.equals("")) {
					utcTime = utcTime.substring(0, 2) + ":"
							+ utcTime.substring(2, 4) + ":"
							+ utcTime.substring(4, 6);
					dt = utcDate + " " + utcTime;
				}
				System.out.println(dt);
				/*
				 * DateFormat utcFormat = new SimpleDateFormat(
				 * "dd-MM-yyyy HH:mm:ss");
				 */

				DateFormat utcFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				// utcFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
				Date date;
				if (utcTime.equals("") || utcTime.equals(" 00:00:00")) {
					date = new Date();
				} else {
					System.out.println("inside else statement");
					date = utcFormat.parse(dt);
					// System.out.println(date);
				}

				// System.out.println("date "+date);
				/*
				 * DateFormat pstFormat = new SimpleDateFormat(
				 * "dd-MM-yyyy HH:mm:ss");
				 * pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+530"));
				 * String utcgmt = ""; if (gValid.equals("0")) { utcgmt =
				 * pstFormat.format(date);
				 * System.out.println("Date for invalid packet " + utcgmt); }
				 */

				/*
				 * if (unitNo.equals("353218071614755")) {
				 * System.out.println(unitNo + "," + pktSeq + "," + lait + "," +
				 * logit); }
				 */
				InsertTMPacket(unitNo, pktSeq, lait, logit, date, speed,
						heading, Integer.parseInt(ignition),
						Integer.parseInt(dinput1), Integer.parseInt(dinput2),
						analog, gOdometer, pOdometer,
						Integer.parseInt(mainSupply), Integer.parseInt(gValid),
						Integer.parseInt(pStatus));

				/*
				 * if (gValid.equals("1")) { InsertTMPacket(unitNo, lait, logit,
				 * dt, speed, heading, Integer.parseInt(ignition),
				 * Integer.parseInt(dinput1), Integer.parseInt(dinput2), analog,
				 * gOdometer, pOdometer, Integer.parseInt(mainSupply),
				 * Integer.parseInt(gValid), Integer.parseInt(pStatus)); } else
				 * { InsertTMPacket(unitNo, lait, logit, dt, speed, heading,
				 * Integer.parseInt(ignition), Integer.parseInt(dinput1),
				 * Integer.parseInt(dinput2), analog, gOdometer, pOdometer,
				 * Integer.parseInt(mainSupply), Integer.parseInt(gValid),
				 * Integer.parseInt(pStatus••••••••)); //
				 * dp.InsertTMNoGPSPacket(unitNo, utcgmt, //
				 * "Not valid packet"); }
				 */

			} catch (Exception e) {
				System.out.println("4");
				System.out.println("Error message " + e.getMessage());
			}

		}

	}

}