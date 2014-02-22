package net.technicpack.launcher.ui.controls;

import net.technicpack.launcher.lang.ResourceLoader;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * This file is part of The Technic Launcher Version 3.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * The Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Technic Launcher  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with The Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

public class CountCircle extends JComponent {
    private int count;

    public CountCircle() {
        setVisible(false);
    }

    public int getCount() { return count; }
    public void setCount(int count) {
        if (count < 0)
            count = 0;

        this.count = count;
        setVisible(count > 0);
    }

    public String getText() {
        if (count > 9)
            return "!";
        else
            return Integer.toString(count);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        String text = getText();
        g2d.setFont(getFont());

        Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);

        double radius = (bounds.getWidth() > bounds.getHeight())?bounds.getWidth():bounds.getHeight();
        int size = (int)Math.ceil(radius);

        g2d.setColor(getBackground());
        g2d.fillOval(0, 0, size, size);

        g2d.setColor(getForeground());

        g2d.drawString(text, (size/2) - (int)(0.5 + bounds.getWidth()/2), (size/2) - (int)(0.5 + bounds.getY() + bounds.getHeight()/2));
    }
}
