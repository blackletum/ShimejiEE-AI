package com.group_finity.mascotnative.macjni;

import com.group_finity.mascot.image.NativeImage;
import com.group_finity.mascot.window.TranslucentWindow;
import com.group_finity.mascot.window.TranslucentWindowEventHandler;

import javax.swing.*;
import java.awt.*;

public class MacJniTranslucentWindow implements TranslucentWindow {
    private final long ptr;
    private final JPanel panel;
    private Rectangle lastSetBounds = new Rectangle();
    private boolean lastSetVisibility = false;
    private MacJniNativeImage currentImage;
    private TranslucentWindowEventHandler eventHandler;

    public MacJniTranslucentWindow() {
        this.ptr = createShimejiWindow();
        this.panel = new JPanel();
    }

    @Override
    public void setEventHandler(TranslucentWindowEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void setImage(NativeImage image) {
        this.currentImage = (MacJniNativeImage) image;
    }

    @Override
    public void updateImage() {
        setImageForShimejiWindow(ptr, currentImage.getNsImagePtr());
    }

    @Override
    public Rectangle getBounds() {
        return lastSetBounds;
    }

    @Override
    public void setBounds(Rectangle r) {
        setJavaBoundsForNSWindow(ptr, r.x, r.y, r.width, r.height);
        lastSetBounds = r;
    }

    @Override
    public boolean isVisible() {
        return lastSetVisibility;
    }

    @Override
    public void setVisible(boolean b) {
        setVisibilityForNSWindow(ptr, b);
        lastSetVisibility = b;
    }

    @Override
    public void dispose() {
        setVisible(false);
        disposeShimejiWindow(ptr);
    }

    @Override
    public Component asComponent() {
        return panel;  // 返回内部的JPanel
    }

    // Native methods
    private native long createShimejiWindow();
    private native void setImageForShimejiWindow(long ptr, long nsImagePtr);
    private native void setJavaBoundsForNSWindow(long ptr, int x, int y, int width, int height);
    private native void setVisibilityForNSWindow(long ptr, boolean visible);
    private native void disposeShimejiWindow(long ptr);
}