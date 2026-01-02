package Controller;

import Dao.DBReportDAO;
import Dao.ReportDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller class for the Reports dashboard.
 * Visualizes business data such as stock distribution and income analysis using JavaFX charts.
 * * @author Selçuk Aloba
 */
public class ReportsController implements Initializable {

    @FXML
    private PieChart pieChartStock;

    @FXML
    private BarChart<String, Number> barChartIncome;

    private ReportDAO reportDAO = new DBReportDAO();

    /**
     * Called to initialize a controller after its root element has been completely processed.
     * Triggers the loading of stock and income charts.
     * * @param location  The location used to resolve relative paths for the root object.
     * @param resources The resources used to localize the root object.
     * @author Selçuk Aloba
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStockChart();
        loadIncomeChart();
    }

    /**
     * Fetches stock distribution data from the database and populates the PieChart components.
     * * @author Selçuk Aloba
     */
    private void loadStockChart() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        Map<String, Double> data = reportDAO.getStockDistribution();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        pieChartStock.setData(pieData);
        pieChartStock.setTitle("Depo Stok Dağılımı (Kg)");
    }

    /**
     * Fetches daily income data from the database and populates the BarChart for weekly analysis.
     * * @author Selçuk Aloba
     */
    private void loadIncomeChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Günlük Ciro");

        Map<String, Double> data = reportDAO.getDailyIncome();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChartIncome.getData().add(series);
        barChartIncome.setTitle("Haftalık Gelir Analizi");
    }
}