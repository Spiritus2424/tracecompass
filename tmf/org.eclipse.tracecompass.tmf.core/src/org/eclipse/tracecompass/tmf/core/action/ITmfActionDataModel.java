package org.eclipse.tracecompass.tmf.core.action;

import java.util.Map;

/**
 * @since 9.1
 */
@SuppressWarnings("javadoc")
public interface ITmfActionDataModel {

    String getDataProviderId();

    Map<String, Object> getInputs();

}
