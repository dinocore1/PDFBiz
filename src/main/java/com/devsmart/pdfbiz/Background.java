package com.devsmart.pdfbiz;


import java.awt.*;

public interface Background {

    float getWidth();
    float getHeight();

    void draw(Graphics2D g);
}
