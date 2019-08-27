/**********************************************************************
 * Copyright (c) 2019 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 **********************************************************************/

package org.eclipse.tracecompass.tmf.ui.model;

import java.util.Collections;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.tmf.core.model.OutputElementStyle;
import org.eclipse.tracecompass.tmf.core.model.StyleProperties;
import org.eclipse.tracecompass.tmf.core.presentation.RGBAColor;

/**
 * A manager for a map of output element styles. Styles have a key that is
 * unique to this manager instance. Style inheritance is possible by having a
 * style refer to a parent style key.
 *
 * @author Patrick Tasse
 * @since 5.1
 */
public class StyleManager {

    private final static StyleManager EMPTY = new StyleManager(Collections.emptyMap());

    private final Map<String, OutputElementStyle> fStyleMap;

    /**
     * Constructor
     *
     * @param styleMap a style map
     */
    public StyleManager(Map<String, OutputElementStyle> styleMap) {
        fStyleMap = styleMap;
    }

    /**
     * Get a manager that has an empty style map. It can be used to resolve
     * element styles that have no parent key.
     *
     * @return an empty style manager
     */
    public static StyleManager empty() {
        return EMPTY;
    }

    /**
     * Get the style property value for the specified element style. The style
     * hierarchy is traversed until a value is found.
     *
     * @param elementStyle
     *            the style
     * @param property
     *            the style property
     * @return the style value, or null
     */
    public @Nullable Object getStyle(OutputElementStyle elementStyle, String property) {
        OutputElementStyle style = elementStyle;
        while (style != null) {
            Map<String, Object> styleValues = style.getStyleValues();
            Object value = styleValues.get(property);
            if (value != null) {
                return value;
            }
            style = fStyleMap.get(style.getParentKey());
        }
        return null;
    }

    /**
     * Get the style property factor value for the specified element style. The
     * style hierarchy is traversed until a float value is found, and the
     * returned float value will be multiplied by the first
     * {@link StyleProperties#FACTOR} suffixed modifier style that was found
     * along the way, if any.
     *
     * @param elementStyle
     *            the style
     * @param property
     *            the style property
     * @return the style float value, or null
     */
    public @Nullable Float getFactorStyle(OutputElementStyle elementStyle, String property) {
        Float factor = null;
        OutputElementStyle style = elementStyle;
        while (style != null) {
            Map<String, Object> styleValues = style.getStyleValues();
            if (factor == null) {
                Object value = styleValues.get(property + StyleProperties.FACTOR);
                if (value instanceof Float) {
                    factor = (Float) value;
                }
            }
            Object value = styleValues.get(property);
            if (value instanceof Float) {
                return (factor == null) ? (Float) value : factor * (Float) value;
            }
            style = fStyleMap.get(style.getParentKey());
        }
        return null;
    }

    /**
     * Get the style property color value for the specified element style. The
     * style hierarchy is traversed until a color and opacity value is found,
     * and the returned color value will be blended with the first
     * {@link StyleProperties#BLEND} suffixed modifier style that was found
     * along the way, if any.
     *
     * @param elementStyle
     *            the style
     * @param property
     *            the style property
     * @return the style value, or null
     */
    public @Nullable RGBAColor getColorStyle(OutputElementStyle elementStyle, String property) {
        String color = null;
        Float opacity = null;
        RGBAColor blend = null;
        OutputElementStyle style = elementStyle;
        while (style != null) {
            Map<String, Object> styleValues = style.getStyleValues();
            if (blend == null) {
                Object value = styleValues.get(property + StyleProperties.BLEND);
                if (value instanceof String) {
                    RGBAColor rgba = RGBAColor.fromString((String) value);
                    if (rgba != null) {
                        blend = rgba;
                    }
                }
            }
            if (opacity == null) {
                Object value = styleValues.get(StyleProperties.OPACITY);
                if (value instanceof Float) {
                    opacity = (Float) value;
                    if (color != null) {
                        break;
                    }
                }
            }
            if (color == null) {
                Object value = styleValues.get(property);
                if (value instanceof String) {
                    color = (String) value;
                    if (opacity != null) {
                        break;
                    }
                }
            }
            style = fStyleMap.get(style.getParentKey());
        }
        int alpha = (opacity == null) ? 255 : (int) (opacity * 255);
        RGBAColor rgba = (color == null) ? null : RGBAColor.fromString(color + String.format("%02X", alpha)); //$NON-NLS-1$
        return (rgba == null) ? null : (blend == null) ? rgba : blend(rgba, blend);
    }

    private static RGBAColor blend(RGBAColor rgba1, RGBAColor rgba2) {
        /**
         * If a color component 'c' with alpha 'a' is blended with color
         * component 'd' with alpha 'b', the blended color and alpha are:
         *
         * <pre>
         * color = (a*(1-b)*c + b*d) / (a + b - a*b)
         * alpha = (a + b - a*b)
         * </pre>
         */
        float alpha1 = rgba1.getAlpha() / 255.0f;
        float alpha2 = rgba2.getAlpha() / 255.0f;
        float alpha = alpha1 + alpha2 - alpha1 * alpha2;
        int r = blend(alpha1, rgba1.getRed(), alpha2, rgba2.getRed(), alpha);
        int g = blend(alpha1, rgba1.getGreen(), alpha2, rgba2.getGreen(), alpha);
        int b = blend(alpha1, rgba1.getBlue(), alpha2, rgba2.getBlue(), alpha);
        return new RGBAColor(r, g, b, Math.round(alpha * 255.0f));
    }

    private static int blend(float alpha1, int color1, float alpha2, int color2, float alpha) {
        return (int) ((alpha1 * (1.0f - alpha2) * color1 + alpha2 * color2) / alpha);
    }
}
