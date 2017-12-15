package com.devsmart.pdfbiz;


import com.google.common.collect.ImmutableMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class PDFView implements Background {

    public static final Logger LOGGER = LoggerFactory.getLogger(PDFView.class);

    private final PDDocument mDoc;
    private final PDFRenderer mRenderer;
    private int mPageIndex;
    private float mScale;
    private Rectangle2D.Float mPageSize = new Rectangle2D.Float();


    public PDFView(PDDocument doc) {
        super();
        mDoc = doc;
        mRenderer = new PDFRenderer(doc);
        mScale = 1.0f;

        setPage(0);
    }

    @Override
    public float getWidth() {
        return mPageSize.width;
    }

    @Override
    public float getHeight() {
        return mPageSize.height;
    }

    @Override
    public void draw(Graphics2D g) {
        try {
            mRenderer.renderPageToGraphics(mPageIndex, g, mScale);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    public void setPage(int pageIndex) {
        PDPage page = mDoc.getPage(pageIndex);
        final int dpi = Toolkit.getDefaultToolkit().getScreenResolution();

        //final float factor = dpi / 72f;
        //mScale = factor;

        final float factor = 1.0f;

        PDRectangle box = page.getBBox();


        mPageSize.setRect(0, 0, factor * box.getWidth(), factor * box.getHeight());
        mPageIndex = pageIndex;

    }

}
