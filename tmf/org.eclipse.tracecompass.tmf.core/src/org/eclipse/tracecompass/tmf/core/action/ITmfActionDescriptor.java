package org.eclipse.tracecompass.tmf.core.action;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * @author Ahmad Faour
 * @since 9.2
 */
@SuppressWarnings("javadoc")
@NonNullByDefault
public interface ITmfActionDescriptor {
    public enum ProviderType {
        TABLE,
        TREE_TIME_XY,
        TIME_GRAPH,
        DATA_TREE
    }

    public enum ActionType {
        INPUT_PROVIDER,
        OUTPUT_LOCATION
    }

    /**
     * Gets the name of the data provide
     *
     * @return the name
     */
    String getName();

    /**
     * Getter for this data provider's ID.
     *
     * @return the ID for this data provider.
     */
    String getId();

    /**
     * Getter for this data provider's type
     *
     * @return this data provider's type
     */
    ProviderType getProviderType();

    /**
     * Getter for the description of this data provider.
     *
     * @return a short description of this data provider.
     */
    String getDescription();

    String getActionTooltipMessage();

    Map<String, Object> getInputParameters();

    String getTargetProviderId();


    ActionType getActionType();

}
