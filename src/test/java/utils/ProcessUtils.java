package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility to clean up orphaned browser-driver processes (chromedriver,
 * geckodriver, msedgedriver, IEDriverServer) that can be left running if a
 * WebDriver session is not closed cleanly (crashed test, killed JVM, CI
 * timeout, etc). This prevents zombie processes from piling up on build
 * agents/dev machines across repeated test runs.
 *
 * Uses the JDK's ProcessHandle API so it works cross-platform without
 * shelling out to taskkill/pkill.
 */
public final class ProcessUtils {

    private static final Logger log = LoggerFactory.getLogger(ProcessUtils.class);

    private static final List<String> DRIVER_PROCESS_NAMES = List.of(
            "chromedriver",
            "geckodriver",
            "msedgedriver",
            "IEDriverServer"
    );

    private ProcessUtils() {
    }

    /**
     * Scans running processes and force-destroys any leftover driver
     * executables. Safe to call after every scenario/suite - it only
     * targets known driver binary names, never generic browser processes
     * that might belong to the user's own open windows.
     */
    public static void killOrphanDriverProcesses() {
        ProcessHandle.allProcesses()
                .filter(ProcessUtils::isDriverProcess)
                .forEach(ProcessUtils::destroy);
    }

    private static boolean isDriverProcess(ProcessHandle handle) {
        String command = handle.info().command().orElse("").toLowerCase();
        return DRIVER_PROCESS_NAMES.stream()
                .map(String::toLowerCase)
                .anyMatch(command::contains);
    }

    private static void destroy(ProcessHandle handle) {
        long pid = handle.pid();
        boolean requested = handle.destroy();
        if (!requested) {
            handle.destroyForcibly();
        }
        log.info("Terminated orphan driver process (pid={})", pid);
    }

    /**
     * Convenience method to list currently running driver processes,
     * mainly useful for debugging/logging.
     */
    public static List<String> listRunningDriverProcesses() {
        return ProcessHandle.allProcesses()
                .filter(ProcessUtils::isDriverProcess)
                .map(h -> h.info().command().orElse("unknown") + " (pid=" + h.pid() + ")")
                .collect(Collectors.toList());
    }
}
