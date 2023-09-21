package org.eclipse.tracecompass.internal.analysis.graph.core.dataprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.analysis.graph.core.base.IGraphWorker;
import org.eclipse.tracecompass.analysis.graph.core.graph.ITmfEdge;
import org.eclipse.tracecompass.analysis.graph.core.graph.ITmfEdgeContextState;
import org.eclipse.tracecompass.analysis.graph.core.graph.ITmfGraph;
import org.eclipse.tracecompass.analysis.graph.core.graph.ITmfGraphVisitor;
import org.eclipse.tracecompass.analysis.graph.core.graph.ITmfVertex;
import org.eclipse.tracecompass.internal.analysis.graph.core.graph.TmfGraphStatistics;
import org.eclipse.tracecompass.tmf.core.model.OutputElementStyle;
import org.eclipse.tracecompass.tmf.core.model.timegraph.ITimeGraphArrow;
import org.eclipse.tracecompass.tmf.core.model.timegraph.ITimeGraphState;
import org.eclipse.tracecompass.tmf.core.model.timegraph.TimeGraphArrow;
import org.eclipse.tracecompass.tmf.core.model.timegraph.TimeGraphState;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.TreeMultimap;

public class CriticalPathVisitor implements ITmfGraphVisitor  {
    /**
     * Atomic long to assign each entry the same unique ID every time the data
     * provider is queried
     */
    private static final AtomicLong ATOMIC_LONG = new AtomicLong();
    private static final @NonNull Map<@NonNull String, @NonNull OutputElementStyle> STATE_MAP = new HashMap<>();

    private final ITmfTrace fTmfTrace;
    private final ITmfGraph fTmfGraph;
    private final Map<String, CriticalPathEntry> fHostEntries;
    private final Map<IGraphWorker, CriticalPathEntry> fWorkers;
    private final TmfGraphStatistics fStatistics;
    private final TreeMultimap<Long, ITimeGraphState> fEntryIdToStates;
    private final Map<String, Long> fHostIdToEntryId;
    private final BiMap<IGraphWorker, Long> fWorkerToEntryId;


    private long fStart;
    private long fEnd;
    private @Nullable List<@NonNull CriticalPathEntry> fCached;
    /**
     * Cache the links once the graph has been traversed.
     */
    private @Nullable List<@NonNull ITimeGraphArrow> fGraphLinks;




    public CriticalPathVisitor(ITmfTrace tmfTrace, ITmfGraph tmfGraph, IGraphWorker worker) {
        fTmfGraph = tmfGraph;
        fTmfTrace = tmfTrace;
        fHostEntries = new HashMap<>();
        fWorkers = new LinkedHashMap<>();
        fStatistics = new TmfGraphStatistics();
        // Sorted by start time.
        fEntryIdToStates = TreeMultimap.create(
                Comparator.naturalOrder(),
                Comparator.comparingLong(ITimeGraphState::getStartTime));
        fHostIdToEntryId = new HashMap<>();
        fWorkerToEntryId = HashBiMap.create();

        fStart = getTmfTrace().getStartTime().toNanos();
        fEnd = getTmfTrace().getEndTime().toNanos();

        ITmfVertex head = tmfGraph.getHead();
        if (head != null) {
            fStart = Long.min(fStart, head.getTimestamp());
            for (IGraphWorker w : tmfGraph.getWorkers()) {
                ITmfVertex tail = tmfGraph.getTail(w);
                if (tail != null) {
                    fEnd = Long.max(fEnd, tail.getTimestamp());
                }
            }
        }
        fStatistics.computeGraphStatistics(tmfGraph, worker);
    }

//    public CriticalPathVisitor(ITmfTrace tmfTrace, ITmfGraph tmfGraph, TmfVertex startVertex) {
//        fTmfGraph = tmfGraph;
//        fTmfTrace = tmfTrace;
//        fHostEntries = new HashMap<>();
//        fWorkers = new LinkedHashMap<>();
//        fStatistics = new TmfGraphStatistics();
//        // Sorted by start time.
//        fEntryIdToStates = TreeMultimap.create(
//                Comparator.naturalOrder(),
//                Comparator.comparingLong(ITimeGraphState::getStartTime));
//        fHostIdToEntryId = new HashMap<>();
//        fWorkerToEntryId = HashBiMap.create();
//
//        fStart = getTmfTrace().getStartTime().toNanos();
//        fEnd = getTmfTrace().getEndTime().toNanos();
//
//        if (startVertex != null) {
//            fStart = Long.min(fStart, startVertex.getTimestamp());
//            for (IGraphWorker w : tmfGraph.getWorkers()) {
//                ITmfVertex tail = tmfGraph.getTail(w);
//                if (tail != null) {
//                    fEnd = Long.max(fEnd, tail.getTimestamp());
//                }
//            }
//        }
//        fStatistics.computeGraphStatistics(tmfGraph, startVertex);
//
//    }

    @Override
    public void visitHead(@NonNull ITmfVertex vertex) {
        // TODO Auto-generated method stub
        IGraphWorker owner = fTmfGraph.getParentOf(vertex);
        if (owner == null) {
            return;
        }
        if (fWorkers.containsKey(owner)) {
            return;
        }
        ITmfVertex first = fTmfGraph.getHead(owner);
        ITmfVertex last = fTmfGraph.getTail(owner);
        if (first == null || last == null) {
            return;
        }
        fStart = Long.min(getTmfTrace().getStartTime().toNanos(), first.getTimestamp());
        fEnd = Long.max(getTmfTrace().getEndTime().toNanos(), last.getTimestamp());
        Long sum = fStatistics.getSum(owner);
        Double percent = fStatistics.getPercent(owner);

        // create host entry
        String host = owner.getHostId();
        long parentId = fHostIdToEntryId.computeIfAbsent(host, h -> ATOMIC_LONG.getAndIncrement());
        fHostEntries.computeIfAbsent(host, h -> new CriticalPathEntry(parentId, -1L, Collections.singletonList(host), fStart, fEnd, sum, percent));

        long entryId = fWorkerToEntryId.computeIfAbsent(owner, w -> ATOMIC_LONG.getAndIncrement());
        CriticalPathEntry entry = new CriticalPathEntry(entryId, parentId, owner, fStart, fEnd, sum, percent);

        fWorkers.put(owner, entry);

    }

    @Override
    public void visit(ITmfEdge link, boolean horizontal) {
        if (horizontal) {
            IGraphWorker parent = fTmfGraph.getParentOf(link.getVertexFrom());
            Long id = fWorkerToEntryId.get(parent);
            if (id != null) {
                String linkQualifier = link.getLinkQualifier();
                ITimeGraphState ev = new TimeGraphState(link.getVertexFrom().getTimestamp(), link.getDuration(), linkQualifier, getMatchingState(link.getEdgeContextState(), false));
                fEntryIdToStates.put(id, ev);
            }
        } else {
            IGraphWorker parentFrom = fTmfGraph.getParentOf(link.getVertexFrom());
            IGraphWorker parentTo = fTmfGraph.getParentOf(link.getVertexTo());
            CriticalPathEntry entryFrom = fWorkers.get(parentFrom);
            CriticalPathEntry entryTo = fWorkers.get(parentTo);
            List<ITimeGraphArrow> graphLinks = fGraphLinks;
            if (graphLinks != null && entryFrom != null && entryTo != null) {
                ITimeGraphArrow lk = new TimeGraphArrow(entryFrom.getId(), entryTo.getId(), link.getVertexFrom().getTimestamp(),
                        link.getVertexTo().getTimestamp() - link.getVertexFrom().getTimestamp(), getMatchingState(link.getEdgeContextState(), true));
                graphLinks.add(lk);
            }
        }
    }

    public @NonNull List<@NonNull CriticalPathEntry> getEntries() {
        if (fCached != null) {
            return fCached;
        }

        fTmfGraph.scanLineTraverse(fTmfGraph.getHead(), this);
        List<@NonNull CriticalPathEntry> list = new ArrayList<>(fHostEntries.values());
        list.addAll(fWorkers.values());
        fCached = list;
        return list;
    }


    @Override
    public void visit(@NonNull ITmfVertex vertex) {
        // Nothing to do
    }

    public ITmfTrace getTmfTrace() {
        return fTmfTrace;
    }

    public TreeMultimap<Long, ITimeGraphState> getEntryIdToStates() {
        return fEntryIdToStates;
    }

    public synchronized List<@NonNull ITimeGraphArrow> getGraphLinks() {
        if (fGraphLinks == null) {
            // the graph has not been traversed yet
            fGraphLinks = new ArrayList<>();
            fTmfGraph.scanLineTraverse(fTmfGraph.getHead(), this);
        }
        return fGraphLinks;
    }

    /**
     * Get the styles of the edge context state when visiting the critical path graph.
     *
     * This is used to show the correct styles directly to the user.
     *
     * @param contextState Context state to return element style from the process state
     * @param arrow specify if it is an arrow or not
     * @return element style
     */
    protected @NonNull OutputElementStyle getMatchingState(ITmfEdgeContextState contextState, boolean arrow) {
        OutputElementStyle outputElementStyle = new OutputElementStyle(null, contextState.getStyles());
        STATE_MAP.put(contextState.getContextEnum().name(), outputElementStyle);
        return outputElementStyle;
    }
}
