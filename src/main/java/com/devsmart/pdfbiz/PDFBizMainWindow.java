package com.devsmart.pdfbiz;

import com.devsmart.swing.BackgroundTask;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.miginfocom.swing.MigLayout;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;


public class PDFBizMainWindow extends JFrame implements WindowListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDFBizMainWindow.class);

    private final TransferHandler mFileDropHandler = new TransferHandler() {
        @Override
        public boolean canImport(TransferSupport support) {
            final boolean containsFiles = Iterables.any(Arrays.asList(support.getDataFlavors()), new Predicate<DataFlavor>() {

                @Override
                public boolean apply(DataFlavor input) {
                    final boolean isFileType = input.isFlavorJavaFileListType();
                    return  isFileType;
                }
            });
            return containsFiles;
        }

        @Override
        public boolean importData(TransferSupport support) {
            try {
                List<File> fileList = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                File f1 = fileList.get(0);
                if(f1.getName().endsWith(".pdf")) {
                    loadPDF(f1);
                    return true;
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
            return false;
        }
    };

    public PDFBizMainWindow() {
        super("PDFBiz");
        setLayout(new MigLayout("fill"));
        addWindowListener(this);

        setTransferHandler(mFileDropHandler);

    }

    public void loadPDF(File pdfFile) {
        BackgroundTask.runBackgroundTask(new BackgroundTask() {

            public PDDocument mDoc;

            @Override
            public void onBackground() {
                try {
                    mDoc = PDDocument.load(pdfFile);

                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }

            @Override
            public void onAfter() {
                super.onAfter();
                if(mDoc != null) {
                    //add(new JLabel("Hello World"), "grow");
                    add(new PDFView(mDoc), "grow");
                    revalidate();
                    repaint();
                }
            }
        });
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(e.getWindow() == this) {
            dispose();
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
