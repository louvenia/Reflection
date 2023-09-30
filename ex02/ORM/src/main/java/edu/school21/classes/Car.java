package edu.school21.classes;

import edu.school21.annotations.OrmColumn;
import edu.school21.annotations.OrmColumnId;
import edu.school21.annotations.OrmEntity;

import java.util.StringJoiner;

@OrmEntity(table = "simple_car")
public class Car {
    @OrmColumnId
    private Integer id;
    @OrmColumn(name = "car_brand")
    private String carBrand;
    @OrmColumn(name = "racing_car")
    private Boolean racingCar;
    @OrmColumn(name = "max_speed")
    private Double maxSpeed;

    public Car() {
        this.carBrand = "defaultBrand";
        this.racingCar = null;
        this.maxSpeed = 0.0;
    }

    public Car(String carBrand, Boolean racingCar, Double maxSpeed) {
        this.carBrand = carBrand;
        this.racingCar = racingCar;
        this.maxSpeed = maxSpeed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public Boolean getRacingCar() {
        return racingCar;
    }

    public void setRacingCar(Boolean racingCar) {
        this.racingCar = racingCar;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Car.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("carBrand='" + carBrand + "'")
                .add("racingCar=" + racingCar)
                .add("maxSpeed=" + maxSpeed)
                .toString();
    }
}

