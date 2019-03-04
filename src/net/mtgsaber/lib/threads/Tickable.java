package net.mtgsaber.lib.threads;

/**
 * @author MtgSaber
 * @version 0.0.1
 *
 * Intended to be used with the <class>Clock</class> class.
 * <method>tick()</method> will be called routinely on a fixed-duration cycle.
 */
public interface Tickable {
    void tick();
}
