package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * ClassName: mainUI<br>
 * Description: <b>All the GUI parts.</b>
 * <p>main properties:</p>
 * <ol>
 * <li>earthquakeSearch:get user's input information(search criteria for earthquakes),then pass it to Void class for the data processing</li>
 * <li>data:receive data processing results,Void.earthquakeList,displays it on tableView</li>
 * <li>12 TextField:gets the user's input search criteria</li>
 * <li>7 TableColumn correspond to seven properties of the earthquake class,the data is displayed after binding</li>
 * </ol>
 * <p>main methods:clickeSearch,clickeClear,showMap,showChart,update,txt</p>
 * @author HanYaohui,LuJinghan,GongGuoyu
 * @version 20171209
 *
 */
public class mainUI implements Initializable {

	static ArrayList<String> earthquakeSearch = new ArrayList<String>();
	static ObservableList<earthquake> data;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private TextField date_start, date_end, latitude_start, latitude_end, longitude_start, longitude_end, depth_start,
			depth_end, magnitude_start, magnitude_end, region,txt_text;
	@FXML
	private TableColumn<earthquake, String> id_tc, date_tc, latitude_tc, longitude_tc, depth_tc, magnitude_tc, region_tc;
	@FXML
	private TableView<earthquake> tableView;
  
	/**
	* Get the search information, pass the search information, and display the result data.<br>
	* Store the search information inputed by the user in the ArrayList earthquakeSearch.
	* pass to class Void,then call Void.search(),receive results, bind TableColumn,shows up on the TableView.<br>
	* 
	*/
	public void clickeSearch() {

		earthquakeSearch.clear();

		String date_startString = date_start.getText();
		String date_endString = date_end.getText();
		String latitude_startString = latitude_start.getText();
		String latitude_endString = latitude_end.getText();
		String longitude_startString = longitude_start.getText();
		String longitude_endString = longitude_end.getText();
		String depth_startString = depth_start.getText();
		String depth_endString = depth_end.getText();
		String magnitude_startString = magnitude_start.getText();
		String magnitude_endString = magnitude_end.getText();
		String regionString = region.getText();

		try {
			if (date_startString.equals(""))earthquakeSearch.add("");
			else earthquakeSearch.add(TimeFormatConversion.dateToString(TimeFormatConversion.stringToDate(date_startString)));
			if (date_endString.equals(""))earthquakeSearch.add("");
			else earthquakeSearch.add(TimeFormatConversion.dateToString(TimeFormatConversion.stringToDate(date_endString)));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Please input the yyyy-MM-dd HH:mm:ss format", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			if (latitude_startString.replace(" ", "").equals(""))earthquakeSearch.add("");
			else earthquakeSearch.add(Double.toString(Double.valueOf(latitude_startString).doubleValue()));
			if (latitude_endString.replace(" ", "").equals(""))earthquakeSearch.add("");
			else earthquakeSearch.add(Double.toString(Double.valueOf(latitude_endString).doubleValue()));
			if (longitude_startString.replace(" ", "").equals(""))earthquakeSearch.add("");
			else earthquakeSearch.add(Double.toString(Double.valueOf(longitude_startString).doubleValue()));
			if (longitude_endString.replace(" ", "").equals(""))earthquakeSearch.add("");
			else earthquakeSearch.add(Double.toString(Double.valueOf(longitude_endString).doubleValue()));
			if (depth_startString.replace(" ", "").equals(""))earthquakeSearch.add("");
			else earthquakeSearch.add(Double.toString(Double.valueOf(depth_startString).doubleValue()));
			if (depth_endString.replace(" ", "").equals(""))earthquakeSearch.add("");
			else earthquakeSearch.add(Double.toString(Double.valueOf(depth_endString).doubleValue()));
			if (magnitude_startString.replace(" ", "").equals(""))earthquakeSearch.add("");
			else earthquakeSearch.add(Double.toString(Double.valueOf(magnitude_startString).doubleValue()));
			if (magnitude_endString.replace(" ", "").equals(""))earthquakeSearch.add("");
			else earthquakeSearch.add(Double.toString(Double.valueOf(magnitude_endString).doubleValue()));
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Error format, please input the number","Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (regionString.replace(" ", "").equals(""))earthquakeSearch.add("");
		else earthquakeSearch.add(regionString);

		Void.saerch();
		
		data = FXCollections.observableArrayList(Void.earthquakeList);

		id_tc.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		date_tc.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		latitude_tc.setCellValueFactory(cellData -> cellData.getValue().latitudeProperty());
		longitude_tc.setCellValueFactory(cellData -> cellData.getValue().longitudeProperty());
		depth_tc.setCellValueFactory(cellData -> cellData.getValue().depthProperty());
		magnitude_tc.setCellValueFactory(cellData -> cellData.getValue().magnitudeProperty());
		region_tc.setCellValueFactory(cellData -> cellData.getValue().regionProperty());

		tableView.setItems(data);
	}

	
	/**
	* Clear the search information entered by the user.<br>
	* 
	*/
	public void clickeClear() {
		date_start.clear();
		date_end.clear();
		latitude_start.clear();
		latitude_end.clear();
		longitude_start.clear();
		longitude_end.clear();
		depth_start.clear();
		depth_end.clear();
		magnitude_start.clear();
		magnitude_end.clear();
		region.clear();
	}
	
	/**
	* show the map of current data.<br>
	* A new window pops up, loading the map, and each earthquake message generates a translucent red dot<br>
	* 
	*/
	public void showMap() {
		Stage secondaryStage = new Stage();
		Group root = new Group();
		Scene secondaryScene = new Scene(root, 900, 600);

		Image image2 = new Image("/Mercator.jpg", true);
		ImageView imageView = new ImageView();
		imageView.setImage(image2);
		root.getChildren().add(imageView);

		ArrayList<earthquake> temp = Void.earthquakeList;
		double X = 0, Y = 0, x = 0, y = 0;

		for (int j = 0; j < temp.size(); j++) {
			double lon = Double.valueOf(temp.get(j).longitudeProperty().getValue());
			double lat = Double.valueOf(temp.get(j).latitudeProperty().getValue());
			X = lon;
			Y = lat;
//			System.out.println(X+","+Y);
			if (X > 0)x = X * 450 / 180;
			else x = 900 - ((-X) * 450 / 180);
			y = Math.log(Math.tan((90 + Y) * Math.PI / 360)) / (Math.PI / 180);
			y = y * 600 / 112.4290555;
			y = 600 - (y + 600) / 2;

			Circle c = new Circle();
			c.setCenterX(x);
			c.setCenterY(y);
			c.setRadius(3.5);
			if (temp.size()<1000) {
				c.setFill(new Color(1, 0, 0, 0.6));
			}else{
				c.setFill(new Color(1, 0, 0, 0.35));
			}
			root.getChildren().add(c);
		}
		secondaryStage.setScene(secondaryScene);
		secondaryStage.show();
	}
	
	/**
	* show the line graph of current data.<br>
	* A new window pops up, and all seismic information in recent 
	* year generates a line that contrasts with the current information<br>
	* 
	*/
	public void showChart() {
		Stage stage = new Stage();

		stage.setTitle("Line Chart");
		// defining the axes
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		// creating the chart
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		lineChart.setTitle("The time of earthquakes in recent year");

		int []chart = Void.lineChart;
		int []chartAll = Void.lineChartAll;
		
		XYChart.Series series1 = new XYChart.Series();
		series1.setName("worldwide");
		for (int month = 1; month < 13; month++) {
			series1.getData().add(new XYChart.Data(month, chartAll[month-1]));
		}

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("current data");
		for (int month = 1; month < 13; month++) {
			series2.getData().add(new XYChart.Data(month, chart[month-1]));
		}
		
		Scene scene = new Scene(lineChart, 900, 600);
		lineChart.getData().addAll(series1, series2);
		
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	* update data.<br>
	* 
	*/
	public void update() {
		Void.update();
	}
	
	/**
	* Export the data to the local file.<br>
	* 
	*/
	public void txt(){
		String path = txt_text.getText();
		Void.txt(path);
	}
	
}


/**
 * ClassName: earthquake<br>
 * Description: include seven properties:id,UTC_date,latitude,longitude,depth,magnitude,region,
 *              the all earthquake's information,data type:SimpleStringProperty.
 *              method:construction method,return the SimpleStringProperty value<br>
 * @author HanYaohui
 * @version 20171210
 *
 */
class earthquake {

	private SimpleStringProperty id;
	private SimpleStringProperty UTC_date;
	private SimpleStringProperty latitude;
	private SimpleStringProperty longitude;
	private SimpleStringProperty depth;
	private SimpleStringProperty magnitude;
	private SimpleStringProperty region;

	public earthquake(String id, String UTC_date, String latitude, String longitude, String depth, String magnitude,
			String region) {
		this.id = new SimpleStringProperty(id);
		this.UTC_date = new SimpleStringProperty(UTC_date);
		this.latitude = new SimpleStringProperty(latitude);
		this.longitude = new SimpleStringProperty(longitude);
		this.depth = new SimpleStringProperty(depth);
		this.magnitude = new SimpleStringProperty(magnitude);
		this.region = new SimpleStringProperty(region);

	}

	public SimpleStringProperty idProperty() {
		return id;
	}

	public SimpleStringProperty dateProperty() {
		return UTC_date;
	}

	public SimpleStringProperty latitudeProperty() {
		return latitude;
	}

	public SimpleStringProperty longitudeProperty() {
		return longitude;
	}

	public SimpleStringProperty depthProperty() {
		return depth;
	}

	public SimpleStringProperty magnitudeProperty() {
		return magnitude;
	}

	public SimpleStringProperty regionProperty() {
		return region;
	}
}
