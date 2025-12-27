package Dao;

import java.util.HashMap;
import java.util.Map;

public class MockReportDAO implements ReportDAO {

    @Override
    public Map<String, Double> getStockDistribution() {
        Map<String, Double> data = new HashMap<>();
        // Mock veri: Depoda ne kadar mal var?
        data.put("Fruits", 120.5);
        data.put("Vegetables", 85.0);
        data.put("Others", 30.0);
        return data;
    }

    @Override
    public Map<String, Double> getDailyIncome() {
        Map<String, Double> data = new HashMap<>();
        // Mock veri: Son 5 günün kazancı
        data.put("Monday", 1500.0);
        data.put("Tuesday", 2300.0);
        data.put("Wednesday", 1800.0);
        data.put("Thursday", 3200.0);
        data.put("Friday", 4100.0);
        return data;
    }
}