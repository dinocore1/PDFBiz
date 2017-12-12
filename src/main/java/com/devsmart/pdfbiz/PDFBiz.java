package com.devsmart.pdfbiz;

import javax.swing.*;


public class PDFBiz {

    private static PDFBizMainWindow mMainAppWindow;

    public static void main(String[] args) {

        mMainAppWindow = new PDFBizMainWindow();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mMainAppWindow.setSize(800, 600);
                mMainAppWindow.setLocationRelativeTo(null);
                mMainAppWindow.setVisible(true);
            }
        });


    }
}
