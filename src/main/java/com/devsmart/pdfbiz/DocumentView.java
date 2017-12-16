package com.devsmart.pdfbiz;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;


public class DocumentView extends JComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentView.class);

    private static final ImmutableMap RENDER_HINTS = ImmutableMap.builder()
            .put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            .put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            .build();

    private Background mBackground;
    private AffineTransform mDefaultMatrix = new AffineTransform();
    private AffineTransform mViewMatrix = new AffineTransform();
    private Rectangle2D.Float mPageSize = new Rectangle2D.Float();
    private Deque<Mode> mModeStack = new ArrayDeque<Mode>();
    private float mMinImageWidth;
    private float mMinImageHeight;
    private double mMinScale;


    private class Mode extends MouseAdapter {

    }

    private AffineTransform interpolate(AffineTransform src, AffineTransform dest, double factor) {
        double[] a = new double[6];
        double[] b = new double[6];
        src.getMatrix(a);
        dest.getMatrix(b);

        for(int i=0;i<6;i++) {
            b[i] = (1.0-factor) * a[i] + factor * b[i];
        }

        return new AffineTransform(b);
    }

    private class ZoomMode extends Mode {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

            try {

                int x = e.getX();
                int y = e.getY();

                double scaleDelta = 0.01 * e.getPreciseWheelRotation();
                double absScale = Math.max(mMinScale, mViewMatrix.getScaleX() + scaleDelta);

                LOGGER.info("mouseWheelMoved() scaleDelta {} absScale {}", scaleDelta, absScale);

                if(scaleDelta > 0.0) {

                    Point2D.Float mousePoint = new Point2D.Float();
                    mViewMatrix.inverseTransform(new Point2D.Float(x, y), mousePoint);

                    AffineTransform transform = new AffineTransform();

                    transform.translate(mousePoint.getX(), mousePoint.getY());

                    transform.scale(1.0 + scaleDelta, 1.0 + scaleDelta);

                    transform.translate(-mousePoint.getX(), -mousePoint.getY());

                    mViewMatrix.concatenate(transform);

                } else {

                    mViewMatrix = interpolate(mViewMatrix, mDefaultMatrix, mMinScale / absScale);

                }


            } catch (NoninvertibleTransformException e1) {
                LOGGER.error("", e);
            }


            e.consume();

            repaint();
        }
    }

    public DocumentView() {
    }

    private void pushMode(Mode mode) {
        addMouseListener(mode);
        addMouseMotionListener(mode);
        addMouseWheelListener(mode);
        mModeStack.push(mode);
    }

    private void popMode() {
        Mode m = mModeStack.pollFirst();
        if(m != null) {
            removeMouseListener(m);
            removeMouseMotionListener(m);
            removeMouseWheelListener(m);
        }
    }

    public void setBackground(Background bg) {
        mBackground = bg;
        if(bg != null) {
            mPageSize.setRect(0, 0, bg.getWidth(), bg.getHeight());
        }
        center();
        pushMode(new ZoomMode());

        revalidate();
        repaint();
    }

    public void addText() {

    }

    public void center() {
        calculateMinDim();
        mViewMatrix.setTransform(mDefaultMatrix);
    }

    private void calculateMinDim() {
        float widthScreen = getWidth();
        float heightScreen = getHeight();

        float widthImage = (float) mPageSize.getWidth();
        float heightImage = (float) mPageSize.getHeight();

        float screenR = widthScreen / heightScreen;
        float imageR = widthImage / heightImage;

        if(screenR > imageR) {
            mMinImageWidth = widthImage * heightScreen/heightImage;
            mMinImageHeight = heightScreen;
        } else {
            mMinImageWidth = widthScreen;
            mMinImageHeight = heightImage * widthScreen/widthImage;
        }

        mMinScale = mMinImageWidth / mPageSize.getWidth();

        mDefaultMatrix.setToIdentity();
        mDefaultMatrix.translate((widthScreen-mMinImageWidth)/2.0, (heightScreen-mMinImageHeight)/2.0);
        mDefaultMatrix.scale(mMinScale, mMinScale);
    }

    @Override
    public void doLayout() {
        LOGGER.info("doLayout()");
        super.doLayout();

        if(mBackground != null) {
            calculateMinDim();
        }

    }

    @Override
    public void paint(Graphics g) {
        LOGGER.info("paint()");
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
