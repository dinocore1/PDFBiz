package com.devsmart.pdfbiz;

import com.google.common.collect.ImmutableMap;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


public class DocumentView extends JComponent {

    private static final ImmutableMap RENDER_HINTS = ImmutableMap.builder()
            .put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            .put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            .build();

    private Background mBackground;
    private AffineTransform mViewMatrix = new AffineTransform();
    private Rectangle2D.Float mPageSize = new Rectangle2D.Float();

    public void setBackground(Background bg) {
        mBackground = bg;
        if(bg != null) {
            mPageSize.setRect(0, 0, bg.getWidth(), bg.getHeight());
        }

        revalidate();
        repaint();
    }

    @Override
    public void doLayout() {


        float outputImageWidth;
        float outputImageHeight;

        float widthScreen = getWidth();
        float heightScreen = getHeight();

        float widthImage = (float) mPageSize.getWidth();
        float heightImage = (float) mPageSize.getHeight();

        float screenR = widthScreen / heightScreen;
        float imageR = widthImage / heightImage;

        if(screenR > imageR) {
            outputImageWidth = widthImage * heightScreen/heightImage;
            outputImageHeight = heightScreen;
        } else {
            outputImageWidth = widthScreen;
            outputImageHeight = heightImage * widthScreen/widthImage;
        }

        mViewMatrix.setToIdentity();

        mViewMatrix.translate((widthScreen-outputImageWidth)/2f, (heightScreen-outputImageHeight)/2f);
        mViewMatrix.scale(outputImageWidth / mPageSize.getWidth(), outputImageHeight / mPageSize.getHeight());


        super.doLayout();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHints(RENDER_HINTS);

        g2d.transform(mViewMatrix);
        //g2d.setTransform(mViewMatrix);

        if(mBackground != null) {
            mBackground.draw(g2d);
        }
    }
}
