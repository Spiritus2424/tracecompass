package org.eclipse.tracecompass.internal.analysis.graph.core.dataprovider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.analysis.graph.core.base.IGraphWorker;
import org.eclipse.tracecompass.analysis.graph.core.graph.ITmfEdgeContextState;
import org.eclipse.tracecompass.analysis.graph.core.graph.ITmfGraph;
import org.eclipse.tracecompass.internal.analysis.graph.core.base.OSCriticalPathPalette;
import org.eclipse.tracecompass.internal.analysis.graph.core.graph.legacy.OSEdgeContextState.OSEdgeContextEnum;
import org.eclipse.tracecompass.tmf.core.model.OutputElementStyle;
import org.eclipse.tracecompass.tmf.core.model.StyleProperties;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

import com.google.common.collect.ImmutableMap;

public class OsCriticalPathVisitor extends CriticalPathVisitor {

    private static final @NonNull String ARROW_SUFFIX = "arrow"; //$NON-NLS-1$
    private static final @NonNull Map<@NonNull String, @NonNull OutputElementStyle> STATE_MAP;
    private static final @NonNull Map<@NonNull String, @NonNull OutputElementStyle> STYLE_MAP = Collections.synchronizedMap(new HashMap<>());

    static {
        ImmutableMap.Builder<@NonNull String, @NonNull OutputElementStyle> builder = new ImmutableMap.Builder<>();
        builder.putAll(OSCriticalPathPalette.getStyles());

        // Add the arrow types
        builder.put(OSEdgeContextEnum.DEFAULT.name() + ARROW_SUFFIX, new OutputElementStyle(OSEdgeContextEnum.UNKNOWN.name(), ImmutableMap.of(StyleProperties.STYLE_NAME, String.valueOf(Messages.CriticalPathDataProvider_UnknownArrow), StyleProperties.STYLE_GROUP, String.valueOf(Messages.CriticalPathDataProvider_GroupArrows))));
        builder.put(OSEdgeContextEnum.NETWORK.name() + ARROW_SUFFIX, new OutputElementStyle(OSEdgeContextEnum.NETWORK.name(), ImmutableMap.of(StyleProperties.STYLE_NAME, String.valueOf(Messages.CriticalPathDataProvider_NetworkArrow), StyleProperties.STYLE_GROUP, String.valueOf(Messages.CriticalPathDataProvider_GroupArrows))));
        STATE_MAP = builder.build();
    }

    public OsCriticalPathVisitor(ITmfTrace tmfTrace, ITmfGraph tmfGraph, IGraphWorker worker) {
        super(tmfTrace, tmfGraph, worker);
    }

//    public OsCriticalPathVisitor(ITmfTrace tmfTrace, ITmfGraph tmfGraph, TmfVertex startVertex) {
//        super(tmfTrace, tmfGraph, startVertex);
//    }

    @Override
    protected @NonNull OutputElementStyle getMatchingState(ITmfEdgeContextState contextState, boolean arrow) {
        String parentStyleName = contextState.getContextEnum().name();
        parentStyleName = STATE_MAP.containsKey(parentStyleName) ? parentStyleName : OSEdgeContextEnum.UNKNOWN.name();
        parentStyleName = arrow ? parentStyleName + ARROW_SUFFIX : parentStyleName;
        return STYLE_MAP.computeIfAbsent(contextState.getContextEnum().name(), style -> new OutputElementStyle(style));
    }


}
