package com.devsmart.pdfbiz;


import com.google.common.collect.ImmutableMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class PDFView extends JComponent {

    public static final Logger LOGGER = LoggerFactory.getLogger(PDFView.class);

    private final PDDocument mDoc;
    private final PDFRenderer mRenderer;
    private int mPageIndex;
    private float mScale;
    private AffineTransform mViewTransform = new AffineTransform();
    private Rectangle2D.Float mPageSize = new Rectangle2D.Float();


    public PDFView(PDDocument doc) {
        super();
        mDoc = doc;
        mRenderer = new PDFRenderer(doc);
        mScale = 1.0f;

        setPage(0);
    }


    public void setPage(int pageIndex) {
        PDPage page = mDoc.getPage(pageIndex);
        final int dpi = Toolkit.getDefaultToolkit().getScreenResolution();

        final float factor = dpi / 72f;
        mScale = factor;

        mPageSize.setRect(0, 0, factor * page.getMediaBox().getWidth(), factor * page.getMediaBox().getHeight());
        mPageIndex = pageIndex;

    }

    @Override
    public void revalidate() {
        System.out.println("revalidate()");
        super.revalidate();

    }

    @Override
    public void doLayout() {
        System.out.println("doLayout()");

        mViewTransform.setToIdentity();

        int viewWidth = getWidth();

        if(viewWidth > mPageSize.getWidth()) {
            float dx = (float) ((viewWidth - mPageSize.getWidth()) / 2f);
            mViewTransform.translate(dx, 0);
        }


        super.doLayout();
    }

    @Override
    public void setSize(int width, int height) {
        System.out.println(String.format("setSize(%d, %d)", width, height));
        super.setSize(width, height);
    }

    private static final ImmutableMap RENDER_HINTS = ImmutableMap.builder()
            .put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            .put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            .build();

    @Override
    public void paint(Graphics g) {
        System.out.println("paint()");
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHints(RENDER_HINTS);
        try {
            g2d.setTransform(mViewTransform);
            mRenderer.renderPageToGraphics(mPageIndex, g2d, mScale);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }
}
