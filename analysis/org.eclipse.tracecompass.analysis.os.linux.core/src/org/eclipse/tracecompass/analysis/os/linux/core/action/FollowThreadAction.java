package org.eclipse.tracecompass.analysis.os.linux.core.action;



import org.eclipse.tracecompass.analysis.os.linux.core.model.HostThread;
import org.eclipse.tracecompass.analysis.os.linux.core.signals.TmfThreadSelectedSignal;
import org.eclipse.tracecompass.tmf.core.action.ITmfAction;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalManager;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

/**
 *
 */
@SuppressWarnings("javadoc")
public class FollowThreadAction implements ITmfAction {
    private static String ACTION_ID = "FollowThreadAction";
    private HostThread fHostThread;


    public static String getActionId() {
        return ACTION_ID;
    }
    /**
     * Constructor
     *
     * @param source
     *            the view that is generating the signal, but also shall
     *            broadcast it
     * @param threadName
     *            the thread name, can be null
     * @param threadId
     *            the thread id
     * @param trace
     *            the trace containing the thread
     */
    public FollowThreadAction(int threadId, ITmfTrace tmfTrace) {
        this(new HostThread(tmfTrace.getHostId(), threadId));
    }

    /**
     * Constructor
     *
     * @param source
     *            the view that is generating the signal, but also shall broadcast
     *            it
     * @param threadName
     *            the thread name, can be null
     * @param ht
     *            The HostThread to follow
     */
    public FollowThreadAction(HostThread hostThread) {
        this.fHostThread = hostThread;
    }

    @Override
    public void run() {
        TmfSignalManager.dispatchSignal(new TmfThreadSelectedSignal(this, this.fHostThread));
    }

    @Override
    public void cancel() {
        this.fHostThread = new HostThread("", -1);
        TmfSignalManager.dispatchSignal(new TmfThreadSelectedSignal(this, this.fHostThread));
    }
}
