package Dao;

import Models.User;
import java.util.List;

public interface UserDAO {
    List<User> getAllCarriers(); // Sadece kuryeleri getir
    void addCarrier(User user);
    void deleteUser(String username); // ID veya Username ile silme
}