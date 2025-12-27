package Dao;

import java.util.Map;

public interface ReportDAO {
    // Kategori bazlı stok dağılımı (Örn: Meyve -> 100kg, Sebze -> 50kg)
    Map<String, Double> getStockDistribution();

    // Günlük gelir verisi (Örn: "Pazartesi" -> 5000 TL)
    Map<String, Double> getDailyIncome();
}