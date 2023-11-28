package org.eclipse.tracecompass.tmf.core.action;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.tmf.core.dataprovider.IDataProviderDescriptor;

/**
 * @since 9.2
 *
 */
@SuppressWarnings("null")
public class TmfActionDescriptor implements ITmfActionDescriptor {
    private String fId;
    private String fName;
    private String fDescription;
    private String fActionTooltipMessage;
    private String fTargetProviderId;
    private ProviderType fProviderType;
    private ActionType fActionType;
    private Map<String, Object> fInputParameters;

    /**
     * Constructor
     *
     * @param bulider
     *            the builder object to create the descriptor
     */
    private TmfActionDescriptor(Builder builder) {
        this.fId = builder.fId;
        this.fName = builder.fName;
        this.fDescription = builder.fDescription;
        this.fActionTooltipMessage = builder.fActionTooltipMessage;
        this.fTargetProviderId = Objects.requireNonNull(builder.fTargetProviderId);
        this.fProviderType = Objects.requireNonNull(builder.fProviderType);
        this.fInputParameters = builder.fInputParameters;
        this.fActionType = builder.fActionType;
    }

    @Override
    public String getName() {
        return this.fName;
    }

    @Override
    public String getId() {
        return this.fId;
    }

    @Override
    public ProviderType getProviderType() {
        return this.fProviderType;
    }

    @Override
    public String getDescription() {
        return this.fDescription;
    }

    @Override
    public @NonNull String getActionTooltipMessage() {
        // TODO Auto-generated method stub
        return this.fActionTooltipMessage;
    }

    @Override
    public @NonNull Map<@NonNull String, @NonNull Object> getInputParameters() {
        // TODO Auto-generated method stub
        return this.fInputParameters;
    }

    @Override
    public @NonNull String getTargetProviderId() {
        // TODO Auto-generated method stub
        return this.fTargetProviderId;
    }

    @Override
    public @NonNull ActionType getActionType() {
        return this.fActionType;
    }

    @Override
    @SuppressWarnings("nls")
    public String toString() {
        return getClass().getSimpleName()
                + " [fName=" + this.getName()
                + ", fDescription=" + this.getDescription()
                + ", fProviderType=" + this.getProviderType()
                + ", fId=" + getId()
                + ", fInputParameters=" + Arrays.asList(this.getInputParameters()).toString()
                + ", fActionTooltipMessage=" + this.getActionTooltipMessage()
                + ", fTargetProviderId=" + this.getTargetProviderId()
                + ", fActionType=" + this.getActionType()
                + "]";
    }

    @Override
    public boolean equals(@Nullable Object arg0) {
        if (!(arg0 instanceof TmfActionDescriptor)) {
            return false;
        }
        TmfActionDescriptor other = (TmfActionDescriptor) arg0;
        return  Objects.equals(this.fName, other.fName) &&
                Objects.equals(this.fDescription, other.fDescription) &&
                Objects.equals(this.fProviderType, other.fProviderType) &&
                Objects.equals(this.fId, other.fId) &&
                Objects.equals(this.fActionTooltipMessage, other.fActionTooltipMessage) &&
                Objects.equals(this.fTargetProviderId, other.fTargetProviderId) &&
                Objects.equals(this.fActionType, other.fActionType) &&
                Objects.equals(this.fInputParameters, other.fInputParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.fName,
                this.fDescription,
                this.fProviderType,
                this.fId,
                this.fActionTooltipMessage,
                this.fTargetProviderId,
                this.fActionType,
                this.fInputParameters.hashCode()
                );
    }

    /**
     * A builder class to build instances implementing interface {@link ITmfActionDescriptor}
     */
    public static class Builder {
        private String fName = ""; //$NON-NLS-1$
        private String fDescription = ""; //$NON-NLS-1$
        private ProviderType fProviderType = null;
        private String fId = ""; //$NON-NLS-1$
        private String fActionTooltipMessage = null;
        private String fTargetProviderId = ""; //$NON-NLS-1$
        private ActionType fActionType = null;
        private Map<String, Object> fInputParameters = null;

        /**
         * Constructor
         */
        public Builder() {
            // Empty constructor
        }

        /**
         * Sets the data provider ID
         *
         * @param id
         *            the ID of the data provider
         * @return the builder instance.
         */
        public Builder setId(String id) {
            fId = id;
            return this;
        }

        /**
         * Sets the name of the data provider
         *
         * @param name
         *            the name to set
         * @return the builder instance.
         */
        public Builder setName(String name) {
            fName = name;
            return this;
        }

        /**
         * Sets the description of the data provider
         *
         * @param description
         *            the description text to set
         * @return the builder instance.
         */
        public Builder setDescription(String description) {
            this.fDescription = description;
            return this;
        }

        /**
         * Sets the data provider type
         *
         * @param type
         *            the data provider type to set
         * @return the builder instance.
         */
        public Builder setProviderType(ProviderType type) {
            this.fProviderType = type;
            return this;
        }

        public Builder setTargetProviderType(String targetProviderId) {
            this.fTargetProviderId = targetProviderId;
            return this;
        }

        public Builder setActionTooltipMessage(String message) {
            this.fActionTooltipMessage = message;
            return this;
        }

        public Builder setActionType(ActionType actionType) {
            this.fActionType = actionType;
            return this;
        }

        public Builder setInputParameters(Map<String, Object> intputParameters) {
            this.fInputParameters = intputParameters;
            return this;
        }

        /**
         * The method to construct an instance of
         * {@link IDataProviderDescriptor}
         *
         * @return a {@link IDataProviderDescriptor} instance
         */
        public ITmfActionDescriptor build() {
            if (this.fProviderType == null) {
                throw new IllegalStateException("Action provider type not set"); //$NON-NLS-1$
            }

            if (this.fId.isEmpty()) {
                throw new IllegalStateException("Empty action provider ID"); //$NON-NLS-1$
            }

            if (this.fTargetProviderId.isEmpty()) {
                throw new IllegalStateException("Empty target provider ID"); //$NON-NLS-1$
            }

            if (this.fActionType == null) {
                throw new IllegalStateException("Action Type not set"); //$NON-NLS-1$
            }
            return new TmfActionDescriptor(this);
        }

    }



}
