package edu.school21.classes;

import java.util.StringJoiner;

public class Car {
    private String carBrand;
    private int price;
    private int speed;

    public Car() {
        this.carBrand = "Default first name";
        this.price = 0;
        this.speed = 0;
    }

    public Car(String carBrand, int price, int speed) {
        this.carBrand = carBrand;
        this.price = price;
        this.speed = speed;
    }

    public int speedUp(int value) {
        this.speed += value;
        return speed;
    }

    public void changePrice(int value) {
        this.price = value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Car.class.getSimpleName() + "[", "]")
                .add("carBrand='" + carBrand + "'")
                .add("price=" + price)
                .add("speed=" + speed)
                .toString();
    }

    private int priceIncrease(int value) {
        this.price += value;
        return price;
    }
}
