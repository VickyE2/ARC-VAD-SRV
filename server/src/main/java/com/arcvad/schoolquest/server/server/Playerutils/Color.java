package com.arcvad.schoolquest.server.server.Playerutils;

public class Color {
    private int red;
    private int green;
    private int blue;
    private int alpha;

    // Constructor that valkeyates and sets RGBA values
    public Color(int red, int green, int blue, int alpha) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
        setAlpha(alpha);
    }

    public Color() {
        setRed(0);
        setBlue(0);
        setGreen(0);
        setAlpha(100);
    }

    // Getter and setter for red with valkeyation
    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        if (isValkeyColorValue(red)) {
            this.red = red;
        } else {
            throw new IllegalArgumentException("Red value must be between 0 and 255.");
        }
    }

    // Getter and setter for green with valkeyation
    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        if (isValkeyColorValue(green)) {
            this.green = green;
        } else {
            throw new IllegalArgumentException("Green value must be between 0 and 255.");
        }
    }

    // Getter and setter for blue with valkeyation
    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        if (isValkeyColorValue(blue)) {
            this.blue = blue;
        } else {
            throw new IllegalArgumentException("Blue value must be between 0 and 255.");
        }
    }

    // Getter and setter for alpha with valkeyation
    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        if (alpha >= 0 && alpha <= 100) { // Valkey alpha range is 0 to 100
            this.alpha = alpha;
        } else {
            throw new IllegalArgumentException("Alpha value must be between 0 and 100.");
        }
    }

    // Utility method to valkeyate RGB values
    private boolean isValkeyColorValue(int value) {
        return value >= 0 && value <= 255;
    }

    // Overrkeye toString to display color in RGBA format
    @Override
    public String toString() {
        return "rgba(" + red + ", " + green + ", " + blue + ", " + alpha + "%)";
    }
}


