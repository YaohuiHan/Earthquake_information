package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application{
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("application/mainUI.fxml"));

			primaryStage.setTitle("My Application");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
//		long startTime = System.currentTimeMillis();
//		Void.Jsoup();
//		Void.update();

//		long endTime = System.currentTimeMillis();
//		System.out.println("Running time£º" + (endTime - startTime) + "ms");
//		Void.fromCSV();
		
//		System.out.println("-------------------------------------------------");
//		System.out.println("");
//		System.out.println("º«Ò«»Ô 11611223  Jsoup Database GUI and other");
//		System.out.println("");
//		System.out.println("Â¬æºº­ 11611211  Map Chart JavaDoc CSV");
//		System.out.println("");
//		System.out.println("¹«¹úîÚ 11611108  Map Chart JavaDoc CSV ");
//		System.out.println("");
//		System.out.println("-------------------------------------------------");
		
		Void.everydayUpdate();
		launch(args);
		
	}

}
