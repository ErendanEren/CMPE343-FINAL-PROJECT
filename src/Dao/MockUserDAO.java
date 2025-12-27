package Dao;

import Models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockUserDAO implements UserDAO {
    private static List<User> usersDB = new ArrayList<>();

    static {
        // Sahte kuryeler ekleyelim
        // User sırası: username, pass, fullname, phone, email, address, role
        usersDB.add(new User("kurye_ahmet", "1234", "Ahmet Yılmaz", "05551112233", "ahmet@kurye.com", "Istanbul", "CARRIER"));
        usersDB.add(new User("kurye_mehmet", "1234", "Mehmet Demir", "05329998877", "mehmet@kurye.com", "Ankara", "CARRIER"));
    }

    @Override
    public List<User> getAllCarriers() {
        // Sadece ROLE = CARRIER olanları filtrele
        return usersDB.stream()
                .filter(u -> "CARRIER".equals(u.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public void addCarrier(User user) {
        user.setRole("CARRIER"); // Rölü garantiye alalım
        usersDB.add(user);
        System.out.println("Mock DB: Kurye eklendi -> " + user.getUsername());
    }

    @Override
    public void deleteUser(String username) {
        usersDB.removeIf(u -> u.getUsername().equals(username));
        System.out.println("Mock DB: Kullanıcı silindi -> " + username);
    }
}