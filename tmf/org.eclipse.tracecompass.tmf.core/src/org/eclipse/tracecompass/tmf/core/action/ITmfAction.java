package org.eclipse.tracecompass.tmf.core.action;

/**
 * @author Ahmad Faour
 * @since 9.2
 */
@SuppressWarnings("javadoc")
public interface ITmfAction {

    public void run();

    public void cancel();

}
