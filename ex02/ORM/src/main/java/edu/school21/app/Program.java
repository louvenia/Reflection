package edu.school21.app;

import edu.school21.classes.Car;
import edu.school21.classes.User;
import edu.school21.connect.DataAccessObject;
import edu.school21.manager.OrmManager;

public class Program {
    public static void main(String[] args) {
        DataAccessObject da = new DataAccessObject();
        OrmManager om = new OrmManager(da.getDataSource());
        om.createTable();

        System.out.println("----------------Method SAVE----------------");

        User user1 = new User("Jisoo", "Kim", 28);
        User user2 = new User("Jennie", "Kim", 27);
        om.save(user1);
        om.save(user2);

        Car car1 = new Car("BMW", true, 372.0);
        Car car2 = new Car("Mercedes", false, 300.23);
        om.save(car1);
        om.save(car2);

        System.out.println("----------------Method UPDATE----------------");

        User userUp = om.findById(1L, User.class);
        userUp.setLastName("My Love");
        om.update(userUp);

        Car carUp = om.findById(2L, Car.class);
        carUp.setCarBrand("Zhiguly");
        carUp.setRacingCar(null);
        carUp.setMaxSpeed(10.32);
        om.update(carUp);

        System.out.println("----------------Method FIND----------------");

        System.out.println(om.findById(1L, User.class));
        System.out.println(om.findById(2L, Car.class));

        da.closeDataSource();
    }
}