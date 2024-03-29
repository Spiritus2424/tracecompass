/*******************************************************************************
 * Copyright (c) 2012, 2016 Ericsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/

package org.eclipse.tracecompass.internal.analysis.profiling.ui;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * TMF message bundle
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {
    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$

    public static @Nullable String CallStackView_ConfigureSymbolProvidersText;
    public static @Nullable String CallStackView_ConfigureSymbolProvidersTooltip;
    public static @Nullable String CallStackView_DepthColumn;
    public static @Nullable String CallStackView_DurationColumn;
    public static @Nullable String CallStackView_EntryTimeColumn;
    public static @Nullable String CallStackView_ExitTimeColumn;
    public static @Nullable String CallStackView_FunctionColumn;
    public static @Nullable String CallStackView_StackInfoNotAvailable;
    public static @Nullable String CallStackView_ThreadColumn;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
