/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.test.selenium.framework;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.test.selenium.browser.Browser;
import org.jboss.test.selenium.framework.internal.Contextual;
import org.jboss.test.selenium.guard.Guard;
import org.jboss.test.selenium.guard.Guarded;

/**
 * <p>Implementation of {@link TypedSelenium} extended by methods in {@link ExtendedTypedSelenium}.</p>
 * 
 * <p>Internally using {@link AjaxAwareCommandProcessor} and {@link GuardedCommandProcessor}.</p>
 * 
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class AjaxSelenium extends ExtendedTypedSelenium implements Guarded {

    /** The reference. */
    private static AtomicReference<AjaxSelenium> reference = new AtomicReference<AjaxSelenium>(null);

    /**
     * Instantiates a new ajax selenium.
     *
     * @param serverHost the server host
     * @param serverPort the server port
     * @param browser the browser
     * @param contextPathURL the context path url
     */
    public AjaxSelenium(String serverHost, int serverPort, Browser browser, URL contextPathURL) {
        selenium = new ExtendedAjaxAwareSelenium(serverHost, serverPort, browser, contextPathURL);
        setCurrentContext(this);
    }

    /**
     * Instantiates a new ajax selenium.
     */
    private AjaxSelenium() {
    }
    
    /**
     * SeleniumAPI wrapper using AjaxAwareCommandProcessor with GuardedCommandProcessor proxy.
     */
    private class ExtendedAjaxAwareSelenium extends ExtendedSelenium {

        /** The ajax aware command processor. */
        AjaxAwareCommandProcessor ajaxAwareCommandProcessor;
        
        /** The guarded command processor. */
        GuardedCommandProcessor guardedCommandProcessor;

        /**
         * Instantiates a new extended ajax aware selenium.
         *
         * @param serverHost the server host
         * @param serverPort the server port
         * @param browser the browser
         * @param contextPathURL the context path url
         */
        public ExtendedAjaxAwareSelenium(String serverHost, int serverPort, Browser browser, URL contextPathURL) {
            super(null);
            ajaxAwareCommandProcessor = new AjaxAwareCommandProcessor(serverHost, serverPort, browser.getAsString(),
                contextPathURL.toString());
            guardedCommandProcessor = new GuardedCommandProcessor(ajaxAwareCommandProcessor);
            this.commandProcessor = guardedCommandProcessor;
        }
    }

    /**
     * <p>Sets the current context.</p>
     * 
     * <p><b>FIXME</b> not safe for multi-instance environment</p>
     * 
     * @param selenium the new current context
     */
    private static void setCurrentContext(AjaxSelenium selenium) {
        reference.set(selenium);
    }

    /**
     * Gets the current context from Contextual objects.
     *
     * @param inContext the in context
     * @return the current context
     */
    public static AjaxSelenium getCurrentContext(Contextual... inContext) {
        return reference.get();
    }

    /* (non-Javadoc)
     * @see org.jboss.test.selenium.guard.Guarded#registerGuard(org.jboss.test.selenium.guard.Guard)
     */
    public void registerGuard(Guard guard) {
        ((ExtendedAjaxAwareSelenium) selenium).guardedCommandProcessor.registerGuard(guard);
    }

    /* (non-Javadoc)
     * @see org.jboss.test.selenium.guard.Guarded#unregisterGuard(org.jboss.test.selenium.guard.Guard)
     */
    public void unregisterGuard(Guard guard) {
        ((ExtendedAjaxAwareSelenium) selenium).guardedCommandProcessor.unregisterGuard(guard);
    }
    
    /* (non-Javadoc)
     * @see org.jboss.test.selenium.guard.Guarded#unregisterGuards(java.lang.Class)
     */
    public void unregisterGuards(Class<? extends Guard> type) {
        ((ExtendedAjaxAwareSelenium) selenium).guardedCommandProcessor.unregisterGuards(type);
    }

    /**
     * Immutable copy for copying this object.
     *
     * @return the AjaxSelenium copy
     */
    public AjaxSelenium immutableCopy() {
        AjaxSelenium copy = new AjaxSelenium();
        copy.selenium = this.selenium;
        ((ExtendedAjaxAwareSelenium) copy.selenium).guardedCommandProcessor =
            ((ExtendedAjaxAwareSelenium) selenium).guardedCommandProcessor.immutableCopy();
        return copy;
    }
    
}
