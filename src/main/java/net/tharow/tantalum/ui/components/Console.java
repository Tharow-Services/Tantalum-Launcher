/*
 * This file is part of Technic UI Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic UI Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic UI Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic UI Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.ui.components;

import net.tharow.tantalum.launchercore.logging.RotatingFileHandler;
import net.tharow.tantalum.utilslib.Utils;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import net.tharow.tantalum.utilslib.logger.Level;

public class Console {
    private final ConsoleFrame frame = null;
    private RotatingFileHandler handler = null;
    private final ConsoleFrame consoleFrame;
    private final String build;

    public Console(ConsoleFrame consoleFrame, String build) {
        this.consoleFrame = consoleFrame;
        this.build = build;
        Utils.getLogger().info("Console Mode Activated");
    }

    public ConsoleFrame getFrame() {
        return frame;
    }

    public void setRotatingFileHandler(RotatingFileHandler handler) {
        this.handler = handler;
    }

    public RotatingFileHandler getHandler() {
        return handler;
    }

    /**
     * Log a message.
     *
     * @param line line
     */
    public void log(String line) {
        log(line, Level.INFO);
    }

    /**
     * Log a message given the {@link AttributeSet}.
     *
     * @param line       line
     */
    public void log(String line, java.util.logging.Level level) {
        line = "[B#" + build + "] " + line;

        AttributeSet attributes = consoleFrame.getDefaultAttributes();

        if (line.startsWith("(!!)")) {
            attributes = consoleFrame.getHighlightedAttributes();
        } else if (level == Level.SEVERE) {
            attributes = consoleFrame.getErrorAttributes();
        } else if (level == Level.WARNING) {
            attributes = consoleFrame.getWarnAttributes();
        } else if (level.intValue() < Level.INFO.intValue()) {
            attributes = consoleFrame.getDebugAttributes();
        }
        attributes = consoleFrame.getAttributeSet(level);

        final String writeText = line.replace("\n\n", "\n");
        final AttributeSet writeAttributes = (attributes != null) ? attributes : consoleFrame.getDefaultAttributes();
        SwingUtilities.invokeLater(() -> {
            try {
                int offset = consoleFrame.getDocument().getLength();
                consoleFrame.getDocument().insertString(offset, writeText, writeAttributes);
                consoleFrame.setCaretPosition(consoleFrame.getDocument().getLength());
            } catch (BadLocationException | NullPointerException ignored) {}
        });
    }
}