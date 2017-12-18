package com.xw.sspku_dormselect.bean;

/**
 * Created by xw on 2017/12/17.
 */

public class Dormitory {

    private int apartment5;

    private int apartment8;

    private int apartment9;

    private int apartment13;

    private int apartment14;

    public int getApartment5() {
        return apartment5;
    }

    public void setApartment5(int apartment5) {
        this.apartment5 = apartment5;
    }

    public int getApartment8() {
        return apartment8;
    }

    public void setApartment8(int apartment8) {
        this.apartment8 = apartment8;
    }

    public int getApartment9() {
        return apartment9;
    }

    public void setApartment9(int apartment9) {
        this.apartment9 = apartment9;
    }

    public int getApartment13() {
        return apartment13;
    }

    public void setApartment13(int apartment13) {
        this.apartment13 = apartment13;
    }

    public int getApartment14() {
        return apartment14;
    }

    public void setApartment14(int apartment14) {
        this.apartment14 = apartment14;
    }

    @Override
    public String toString() {
        return "Dormitory{" +
                "apartment5=" + apartment5 +
                ", apartment8=" + apartment8 +
                ", apartment9=" + apartment9 +
                ", apartment13=" + apartment13 +
                ", apartment14=" + apartment14 +
                '}';
    }
}
