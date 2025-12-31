package Dao;

import Models.Coupon;
import java.util.List;

public interface CouponDAO {
    List<Coupon> getAllCoupons();
    Coupon getCouponByCode(String code);
    void addCoupon(Coupon coupon);
    void deleteCoupon(int id);
}
