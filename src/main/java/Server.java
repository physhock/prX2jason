/**
 * Server interface with HTTP API.
 */
public interface Server {
    /**
     * Bind server to HTTP port and start listening.
     * <p>
     * May be called only once.
     */
    void start();

    /**
     * Stop listening and free all the resources.
     * <p>
     * May be called only once and after {@link #start()}.
     */
    void stop();
}
