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

public class ReportsController implements Initializable {

    @FXML
    private PieChart pieChartStock; // Pasta grafik (Stok durumu)

    @FXML
    private BarChart<String, Number> barChartIncome; // Çubuk grafik (Gelir)

    private ReportDAO reportDAO = new DBReportDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStockChart();
        loadIncomeChart();
    }

    private void loadStockChart() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        Map<String, Double> data = reportDAO.getStockDistribution();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        pieChartStock.setData(pieData);
        pieChartStock.setTitle("Depo Stok Dağılımı (Kg)");
    }

    private void loadIncomeChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Günlük Ciro");

        Map<String, Double> data = reportDAO.getDailyIncome();

        // Map sırasız olduğu için günleri sırayla eklemek istersek farklı logic gerekir
        // Ama şimdilik mock olduğu için direkt döngüyle ekliyoruz
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChartIncome.getData().add(series);
        barChartIncome.setTitle("Haftalık Gelir Analizi");
    }
}