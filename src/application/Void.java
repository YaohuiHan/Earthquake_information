package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * ClassName: Void<br>
 * Description: <b>all the data processing part.</b>
 * <p>main properties:</p>
 * <ol>
 * <li>earthquakeList:After data processing, the seismic information that user needs</li>
 * <li>lineChart:the data that the line graph needs,count the time of the earthquakes of the current search in recent year on a monthly basis</li>
 * <li>lineChartAll:data that the line graph needs,count the time of all the earthquakes in recent year on a monthly basis</li>
 * </ol>
 * <p>main methods:saerch,fromCSV,Jsoup,update,txt,everydayUpdate</p>
 * @author HanYaohui
 * @version 20171209
 *
 */
class Void {

	static ArrayList<earthquake> earthquakeList = new ArrayList<earthquake>();
	static int []lineChart = new int[12];
	static int []lineChartAll = new int[12];

	
	/**
	* search the conditional earthquake information from the database.<br>
	* get search information mainUI.earthquakeSearch,judge if there is a value,complete the sql sentence.<br>
	* connect the database,put the search results in earthquakeList,called by mainUI.<br>
	* 
	*/
	public static void saerch() {

		lineChart = new int[12];
		lineChartAll = new int[12];
		earthquakeList.clear();

		String sql = "";
		ArrayList<String> list = mainUI.earthquakeSearch;
		
		if (!list.get(0).equals(""))sql = sql + " and UTCdate >= '" + list.get(0) + "'";
		if (!list.get(1).equals(""))sql = sql + " and UTCdate <= '" + list.get(1) + "'";
		if (!list.get(2).equals(""))sql = sql + " and latitude >= " + list.get(2);
		if (!list.get(3).equals(""))sql = sql + " and latitude <= " + list.get(3);
		if (!list.get(4).equals(""))sql = sql + " and longitude >= " + list.get(4);
		if (!list.get(5).equals(""))sql = sql + " and longitude <= " + list.get(5);
		if (!list.get(6).equals(""))sql = sql + " and depth >= " + list.get(6);
		if (!list.get(7).equals(""))sql = sql + " and depth <= " + list.get(7);
		if (!list.get(8).equals(""))sql = sql + " and magnitude >= " + list.get(8);
		if (!list.get(9).equals(""))sql = sql + " and magnitude <= " + list.get(9);
		if (!list.get(10).equals(""))sql = sql + " and region regexp '"+list.get(10)+"' ";

		// System.out.println(sql);

		if (!sql.equals(""))sql = " where " + sql.substring(5);

		// System.out.println(sql);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/Java2_project?characterEncoding=utf8&useSSL=false", "root", "");
			Statement stmt = conn.createStatement();

			// System.out.println(sql);
			sql = "select * from earthquake " + sql;
			ResultSet result = stmt.executeQuery(sql);

			while (result.next()) {
				int id = result.getInt(1);
				Timestamp UTCdate = result.getTimestamp(2);
				double latitude = result.getDouble(3);
				double longitude = result.getDouble(4);
				double depth = result.getDouble(5);
				double magnitude = result.getDouble(6);
				String region = result.getString(7);
				
				if(UTCdate.getYear()==117)lineChart[UTCdate.getMonth()]++;
				
//				System.out.print(UTCdate+","+UTCdate.getYear()+",getMonth(),"+UTCdate.getMonth());
				earthquakeList.add(new earthquake(id + "", UTCdate + "", latitude + "", longitude + "", depth + "",
						magnitude + "", region + ""));
			}
			
			for (int month = 1; month < 13; month++) {
				sql = "select count(*) from earthquake where year(utcdate)='2017' and month(utcdate)='" + month + "'";
				result = stmt.executeQuery(sql);
				while (result.next()) {
					lineChartAll[month-1] = result.getInt(1);
				} 
			}
			
			result.close();
			stmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	* get the earthquakes information from csv file.<br>
	* 
	*/
	public static void fromCSV() {

		String sql = "";
		String line = "";

		StringBuilder temp;
		StringBuilder sb = new StringBuilder();

		sb.append("insert into earthquakeCSV (id,UTCdate, latitude, longitude ,  depth, magnitude , region) values ");

		File csv = new File("D:\\eclipse\\workspace\\Lab4\\src\\application\\earthquakes.csv"); // path of csv file
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csv));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			line = br.readLine();
			while ((line = br.readLine()) != null) {

				if (line.charAt(line.length() - 1) != '\"') {
					temp = new StringBuilder(line);
					temp.insert(temp.lastIndexOf(",") + 1, "'");
					sb.append("( " + temp + "' ),");
				} else {
					sb.append("( " + line + " ),");
				}
			}
			sql = sb.substring(0, sb.length() - 1);
			// System.out.println("all rows in csv chart£º" + allString.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/Java2_project?characterEncoding=utf8&useSSL=false", "root", "");
			Statement stmt = conn.createStatement();

			stmt.executeUpdate("drop table if exists earthquakeCSV");
			stmt.executeUpdate("create table earthquakeCSV(id int(10), UTCdate datetime(1),latitude double,longitude double, depth double,magnitude double, region varchar(40))");

			stmt.executeUpdate(sql);

			stmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	* get earthquakes information from the Internet, store it in local database.<br>
	* 
	*/
	public static void Jsoup() {
		
		StringBuilder sb = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/Java2_project?characterEncoding=utf8&useSSL=false", "root", "");
			Statement stmt = conn.createStatement();

//			stmt.executeUpdate("drop table if exists earthquakeNET");
//			stmt.executeUpdate("create table earthquakeNET(id int(10), UTCdate datetime(1),latitude double,longitude double,   depth double,magnitude double, region varchar(40))");
			
			long startTime = System.currentTimeMillis();//get current time
			for (int k = 0; k < 20; k++) {
				for (int j = 100*k+1; j <= 100*k+100; j++) {
					sb = new StringBuilder();
					sb.append("insert into earthquakeNET (id,UTCdate, latitude, longitude ,  depth, magnitude , region) values ");

					System.out.println("data in " + j + "th page");
					long endTime = System.currentTimeMillis();
					System.out.println("program running time£º" + (endTime - startTime) + "ms");

					Document doc = Jsoup.connect("https://www.emsc-csem.org/Earthquake/?view=" + j).get();
					Elements table = doc.select("table").select("[border=\"0\"]").select("[cellpadding=\"0\"]")
							.select("[cellspacing=\"0\"]").select("[width=\"100%\"]");
					Elements tbody = table.select("tbody#tbody");
					Elements rows = tbody.select("tr");
					for (Element row : rows) {
						Elements cell = row.select("tr");
						int id;
						try {
							id = Integer.valueOf(cell.get(0).id());
							// System.out.print(id + ",");
						} catch (Exception e) {
							continue;
						}
						sb.append("(" + id);

						String eCell = cell.get(0).text();
						if (eCell.contains("earthquake")) {

//							 System.out.println(eCell);

							eCell = eCell.substring(eCell.indexOf("earthquake"));

							String date = eCell.substring(10, 31);
							sb.append(",\"" + date);

							if (eCell.contains("min ago")) {
								eCell = eCell.substring(eCell.indexOf("min ago") + 8);
							} else {
								eCell = eCell.substring(eCell.indexOf("earthquake") + 32);
							}

							String[] sArray = eCell.split(" ");
							if (sArray[1].equals("S")) {
								sb.append("\",-" + sArray[0]);
							} else {
								sb.append("\"," + sArray[0]);
							}
							if (sArray[3].equals("W")) {
								sb.append(",-" + sArray[2]);
							} else {
								sb.append("," + sArray[2]);
							}
							sb.append("," + sArray[4]);
							sb.append("," + sArray[6]);
							sb.append(",\"" + sArray[7]);
							for (int i = 8; i <= sArray.length - 3; i++) {
								sb.append(" " + sArray[i]);
							}
							sb.append("\"),");
						}
					}
					String sql = sb.toString().substring(0, sb.length() - 1);
//					System.out.println(sql);
					
					stmt.executeUpdate(sql);
				} 
			}

			stmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* update earthquakes information from the Internet.<br>
	* update,delete duplicate data<br>
	* 
	*/
	public static void update() {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into earthquakeNETupdate (id,UTCdate, latitude, longitude ,  depth, magnitude , region) values ");

		String saerch = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/Java2_project?characterEncoding=utf8&useSSL=false", "root", "");
			Statement stmt = conn.createStatement();

			stmt.executeUpdate("drop table if exists earthquakeNETupdate");
			stmt.executeUpdate("create table earthquakeNETupdate (id int(10), UTCdate datetime(1),latitude double,longitude double,   depth double,magnitude double, region varchar(40))");
			
			saerch = "select max(UTCdate) from earthquakeNET";
			ResultSet result = stmt.executeQuery(saerch);
			Timestamp maxdate = null ;
			while (result.next()) {
				maxdate = result.getTimestamp(1);
			}
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//set date format
			String date = null;

			long startTime = System.currentTimeMillis();//get current time
			for (int j = 1; j < 100; j++) {
				
				System.out.println("data in NO." + j + " page");
				long endTime = System.currentTimeMillis();
				System.out.println("program running time£º" + (endTime - startTime) + "ms");

				Document doc = Jsoup.connect("https://www.emsc-csem.org/Earthquake/?view=" + j).get();
				Elements table = doc.select("table").select("[border=\"0\"]").select("[cellpadding=\"0\"]")
						.select("[cellspacing=\"0\"]").select("[width=\"100%\"]");
				Elements tbody = table.select("tbody#tbody");
				Elements rows = tbody.select("tr");
				for (Element row : rows) {
					Elements cell = row.select("tr");
					
//					System.out.print(cell);
					
					int id;
					try {
						id = Integer.valueOf(cell.get(0).id());
						// System.out.print(id + ",");
					} catch (Exception e) {
						continue;
					}
					sb.append("(" + id);

					String eCell = cell.get(0).text();
					if (eCell.contains("earthquake")) {

//						System.out.println(eCell);
						
						eCell = eCell.substring(eCell.indexOf("earthquake"));

						date = eCell.substring(10, 31);
						sb.append(",\"" + date);

						if (eCell.contains("min ago")) {
							eCell = eCell.substring(eCell.indexOf("min ago") + 8);
						} else {
							eCell = eCell.substring(eCell.indexOf("earthquake") + 32);
						}

						String[] sArray = eCell.split(" ");
						if (sArray[1].equals("S")) {
							sb.append("\",-" + sArray[0]);
						} else {
							sb.append("\"," + sArray[0]);
						}
						if (sArray[3].equals("W")) {
							sb.append(",-" + sArray[2]);
						} else {
							sb.append("," + sArray[2]);
						}
						sb.append("," + sArray[4]);
						sb.append("," + sArray[6]);
						sb.append(",\"" + sArray[7]);
						for (int i = 8; i <= sArray.length - 3; i++) {
							sb.append(" " + sArray[i]);
						}
						sb.append("\"),");
					}
				}
				
				long diff = 0,days=0;
				try {
					Date d1 = df.parse(maxdate+"");
					Date d2 = df.parse(date);
					diff = d2.getTime() - d1.getTime();
					days = diff / (1000 * 60 * 60 * 24);
					System.out.println(d1+","+d2+","+diff+","+days);
				} catch (Exception e) {
				} 
				if(days<0){
					break;
				}
				
			}
			String sql = sb.toString().substring(0, sb.length() - 1);
			
//			System.out.println(sql);
			
			stmt.executeUpdate(sql);
			
			stmt.executeUpdate("insert into earthquakenet select * from earthquakenetupdate");
			stmt.executeUpdate("drop table if exists earthquake");
			stmt.executeUpdate("create table earthquake (id int(10), UTCdate datetime(1),latitude double,longitude double,   depth double,magnitude double, region varchar(40))");
			stmt.executeUpdate("insert into earthquake select * from ( select distinct * from earthquakenet)a");
			
			stmt.executeUpdate("drop table if exists earthquakenet");
			stmt.executeUpdate("create table earthquakenet (id int(10), UTCdate datetime(1),latitude double,longitude double,   depth double,magnitude double, region varchar(40))");
			stmt.executeUpdate("insert into earthquakenet select * from earthquake");
			
			System.out.println("Updated completely");
			
			result.close();
			stmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	* Import the earthquake information that the user is searching for into the local file.<br>
	* 
	* @param path path+file&nbsp;name+file type,for example:&nbsp;&nbsp;D:\\earthquake.txt
	*/
	public static void txt(String path) {

		File file = new File(path);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			if (file.exists()) {
				FileWriter fw = new FileWriter(file, true);
				BufferedWriter bw = new BufferedWriter(fw);

				bw.write("id,UTC_date,latitude,longitude,depth,magnitude,region\r\n");

				for (int j = 0; j < earthquakeList.size(); j++) {
					bw.write(earthquakeList.get(j).idProperty().getValue() + ",\""
							+ earthquakeList.get(j).dateProperty().getValue() + "\","
							+ earthquakeList.get(j).latitudeProperty().getValue() + ","
							+ earthquakeList.get(j).longitudeProperty().getValue() + ","
							+ earthquakeList.get(j).depthProperty().getValue() + ","
							+ earthquakeList.get(j).magnitudeProperty().getValue() + ",\""
							+ earthquakeList.get(j).regionProperty().getValue() + "\"\r\n");
				}

				bw.close();
				fw.close();
				JOptionPane.showMessageDialog(null, "Data has been imported into "+path, "complete", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Please input correct format£ºD\\\\earthquake.txt", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	* Timer, update data at 23:59:00. every day.<br>
	* 
	*/
    public static void everydayUpdate() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("update time = " + new Date());
                Void.update();
            }
        };

        //Set execution time
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//every day
        //set to execute at 23:59:00 every day
        calendar.set(year, month, day, 23, 59, 00);
        Date date = calendar.getTime();
        Timer timer = new Timer();
        System.out.println("update time everyday£º"+date);

//        int period = 2 * 1000;
//        execute task at date every day,repeat every two seconds
//        timer.schedule(task, date, period);
//        executed at 23:59:00 every day,execute only once
        timer.schedule(task, date);
    }
	
}

/**
 * ClassName: TimeFormatConversion<br>
 * Description: <b>tool class,String,date format conversion.</b>
 * <p>main methods:dateToString,stringToDate</p>
 * @author HanYaohui
 * @version 201704
 *
 */
class TimeFormatConversion {
	public static String dateToString(Date date) { 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStringParse = sdf.format(date);
		return dateStringParse;
	}

	public static Date stringToDate(String dateString) { 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateParse = null;
		try {
			dateParse = sdf.parse(dateString);
		} catch (ParseException e) {
		}
		return dateParse;
	}
}
