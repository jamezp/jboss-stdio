/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.stdio;

import java.io.IOException;
import java.io.Writer;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

/**
 * Abstract base class for writers which log to a logger.
 */
public class JBossLoggingWriter extends Writer {

    private final StringBuilder buffer = new StringBuilder();
    private final Logger logger;
    private final Level level;

    /**
     * Construct a new instance.
     *
     * @param category the log category to use
     * @param level    the level at which to log messages
     */
    public JBossLoggingWriter(final String category, final Level level) {
        this(Logger.getLogger(category), level);
    }

    /**
     * Construct a new instance.
     *
     * @param logger the logger to use
     * @param level  the level at which to log messages
     */
    public JBossLoggingWriter(final Logger logger, final Level level) {
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void write(final int c) throws IOException {
        if (logger == null) {
            return;
        }
        synchronized (buffer) {
            if (c == '\n') {
                logger.log(level, buffer.toString());
                buffer.setLength(0);
            } else {
                buffer.append((char) c);
            }
        }
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        if (logger == null) {
            return;
        }
        synchronized (buffer) {
            int mark = 0;
            int i;
            for (i = 0; i < len; i++) {
                final char c = cbuf[off + i];
                if (c == '\n') {
                    buffer.append(cbuf, mark + off, i - mark);
                    logger.log(level, buffer.toString());
                    buffer.setLength(0);
                    mark = i + 1;
                }
            }
            buffer.append(cbuf, mark + off, i - mark);
        }
    }

    @Override
    public void flush() throws IOException {
        // ignore
    }

    @Override
    public void close() throws IOException {
        // ignore
    }
}
