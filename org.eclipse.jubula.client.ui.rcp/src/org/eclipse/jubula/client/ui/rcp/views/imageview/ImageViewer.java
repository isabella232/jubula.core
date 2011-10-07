/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.views.imageview;

import java.awt.geom.AffineTransform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * Based on the example provided at:
 * http://www.eclipse.org/articles/Article-Image-Viewer/Image_viewer.html 
 * 
 * Note that instances of this class do not manage the image. That is, the image
 * will not be disposed() by this instance under any conditions. Users of this
 * class must manage the image.
 * 
 * @author BREDEX GmbH
 * @created Apr 14, 2010
 */
public class ImageViewer extends Canvas {
    /**
     * <code>sourceImage</code>
     */
    private Image m_sourceImage;

    /**
     * <code>screenImage</code>
     */
    private Image m_screenImage;
    
    /**
     * <code>transform</code>
     */
    private AffineTransform m_transform = new AffineTransform();
    
    /**
     * @param parent
     *            the parent
     * @param style
     *            the style
     */
    public ImageViewer(Composite parent, int style) {
        super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
        initialize();
    }

    /**
     * init
     */
    private void initialize() {
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                if (m_sourceImage == null) {
                    return;
                }
                drawImage(e.gc);
            }
        });
        addControlListener(new ControlAdapter() { /* resize listener. */
            public void controlResized(ControlEvent event) {
                syncScrollBars();
            }
        });
        initScrollBars();
    }

    /** 
     * Initalize the scrollbar and register listeners. 
     */
    private void initScrollBars() {
        ScrollBar horizontal = getHorizontalBar();
        horizontal.setEnabled(false);
        horizontal.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                scrollHorizontally((ScrollBar) event.widget);
            }
        });
        ScrollBar vertical = getVerticalBar();
        vertical.setEnabled(false);
        vertical.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                scrollVertically((ScrollBar) event.widget);
            }
        });
    }
    
    /**
     * Scroll horizontally
     * 
     * @param scrollBar the scrollbar
     */
    private void scrollHorizontally(ScrollBar scrollBar) {
        if (m_screenImage == null || m_screenImage.isDisposed()) {
            return;
        }

        AffineTransform af = m_transform;
        double tx = af.getTranslateX();
        double select = -scrollBar.getSelection();
        af.preConcatenate(AffineTransform.getTranslateInstance(select - tx, 0));
        m_transform = af;
        syncScrollBars();
    }
    
    /**
     * Scroll vertically
     * 
     * @param scrollBar
     *            the scrollbar
     */
    private void scrollVertically(ScrollBar scrollBar) {
        if (m_screenImage == null || m_screenImage.isDisposed()) {
            return;
        }

        AffineTransform af = m_transform;
        double ty = af.getTranslateY();
        double select = -scrollBar.getSelection();
        af.preConcatenate(AffineTransform.getTranslateInstance(0, select - ty));
        m_transform = af;
        syncScrollBars();
    }
    
    /**
     * Synchronize the scrollbar with the image. If the transform is out
     * of range, it will correct it. This function considers only following
     * factors :<b> transform, image size, client area</b>.
     */
    public void syncScrollBars() {
        if (m_sourceImage == null || m_sourceImage.isDisposed()) {
            redraw();
            return;
        }

        AffineTransform af = m_transform;
        double sx = af.getScaleX(), sy = af.getScaleY();
        double tx = af.getTranslateX(), ty = af.getTranslateY();
        if (tx > 0) {
            tx = 0;
        }
        if (ty > 0) {
            ty = 0;
        }

        ScrollBar horizontal = getHorizontalBar();
        horizontal.setIncrement((getClientArea().width / 100));
        horizontal.setPageIncrement(getClientArea().width);
        Rectangle imageBound = m_sourceImage.getBounds();
        int cw = getClientArea().width, ch = getClientArea().height;
        if (imageBound.width * sx > cw) { /* image is wider than client area */
            horizontal.setMaximum((int) (imageBound.width * sx));
            horizontal.setEnabled(true);
            if (((int)-tx) > horizontal.getMaximum() - cw) {
                tx = -horizontal.getMaximum() + cw;
            }
        } else { /* image is narrower than client area */
            horizontal.setEnabled(false);
            tx = (cw - imageBound.width * sx) / 2; //center if too small.
        }
        horizontal.setSelection((int) (-tx));
        horizontal.setThumb((getClientArea().width));

        ScrollBar vertical = getVerticalBar();
        vertical.setIncrement((getClientArea().height / 100));
        vertical.setPageIncrement((getClientArea().height));
        if (imageBound.height * sy > ch) { // image is higher than client area
            vertical.setMaximum((int) (imageBound.height * sy));
            vertical.setEnabled(true);
            if (((int)-ty) > vertical.getMaximum() - ch) {
                ty = -vertical.getMaximum() + ch;
            }
        } else { /* image is less higher than client area */
            vertical.setEnabled(false);
            ty = (ch - imageBound.height * sy) / 2; //center if too small.
        }
        vertical.setSelection((int) (-ty));
        vertical.setThumb((getClientArea().height));

        /* update transform. */
        af = AffineTransform.getScaleInstance(sx, sy);
        af.preConcatenate(AffineTransform.getTranslateInstance(tx, ty));
        m_transform = af;

        redraw();
    }
    
    /**
     * @param gc
     *            the gc component
     */
    private void drawImage(GC gc) {
        if (m_sourceImage.isDisposed()) {
            getHorizontalBar().setSelection(0);
            getHorizontalBar().setEnabled(false);
            getVerticalBar().setSelection(0);
            getVerticalBar().setEnabled(false);
            return;
        }
        Rectangle clientRect = getClientArea(); /* Canvas' painting area */
        if (m_sourceImage != null) {
            SWT2DUtil.absRect(clientRect);
            Rectangle imageRect = SWT2DUtil.inverseTransformRect(m_transform,
                    clientRect);
            int gap = 2; /* find a better start point to render */
            imageRect.x -= gap;
            imageRect.y -= gap;
            imageRect.width += 2 * gap;
            imageRect.height += 2 * gap;

            Rectangle imageBound = m_sourceImage.getBounds();
            imageRect = imageRect.intersection(imageBound);
            SWT2DUtil.absRect(imageRect);
            Rectangle destRect = SWT2DUtil
                    .transformRect(m_transform, imageRect);

            if (m_screenImage != null) {
                m_screenImage.dispose();
            }
            m_screenImage = new Image(getDisplay(), clientRect.width,
                    clientRect.height);
            GC newGC = new GC(m_screenImage);
            newGC.setClipping(clientRect);
            newGC.drawImage(m_sourceImage, imageRect.x, imageRect.y,
                    imageRect.width, imageRect.height, destRect.x, destRect.y,
                    destRect.width, destRect.height);
            newGC.dispose();

            gc.drawImage(m_screenImage, 0, 0);
        } else {
            gc.setClipping(clientRect);
            gc.fillRectangle(clientRect);
            initScrollBars();
        }
    }

    /**
     * This method sets the image that the receiver is responsible for drawing.
     * This class does not manage the image; it only displays it. Any image the
     * receiver is currently displaying will simply be replaced by the value
     * provided in the parameter. The caller is responsible for disposing the
     * images.
     * 
     * @param image
     *            the Image to display.
     */
    public void setImage(Image image) {
        this.m_sourceImage = image;
        syncScrollBars();
        redraw();
    }
    
    /**
     * Dispose the garbage here
     */
    public void dispose() {
        if (m_sourceImage != null && !m_sourceImage.isDisposed()) {
            m_sourceImage.dispose();
        }
        if (m_screenImage != null && !m_screenImage.isDisposed()) {
            m_screenImage.dispose();
        }
    }
}