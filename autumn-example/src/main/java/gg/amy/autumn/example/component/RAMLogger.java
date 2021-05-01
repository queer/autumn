package gg.amy.autumn.example.component;

import gg.amy.autumn.di.annotation.Component;
import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.di.annotation.Run;
import gg.amy.autumn.di.annotation.Singleton;
import org.slf4j.Logger;

import java.lang.management.ManagementFactory;

/**
 * @author amy
 * @since 5/1/21.
 */
@Component
@Singleton
public class RAMLogger {
    @Inject
    private Logger logger;

    @Run
    @SuppressWarnings("DuplicatedCode")
    public void log() {
        final var heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        final var nonHeap = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        final var heapUsed = heap.getUsed() / (1024L * 1024L);
        final var heapAllocated = heap.getCommitted() / (1024L * 1024L);
        final var heapTotal = heap.getMax() / (1024L * 1024L);
        final var heapInit = heap.getInit() / (1024L * 1024L);
        final var nonHeapUsed = nonHeap.getUsed() / (1024L * 1024L);
        final var nonHeapAllocated = nonHeap.getCommitted() / (1024L * 1024L);
        final var nonHeapTotal = nonHeap.getMax() / (1024L * 1024L);
        final var nonHeapInit = nonHeap.getInit() / (1024L * 1024L);

        final var out =
                "[HEAP]\n" +
                "     [Init] " + heapInit + "MB\n" +
                "     [Used] " + heapUsed + "MB\n" +
                "    [Alloc] " + heapAllocated + "MB\n" +
                "    [Total] " + heapTotal + "MB\n" +
                "[NONHEAP]\n" +
                "     [Init] " + nonHeapInit + "MB\n" +
                "     [Used] " + nonHeapUsed + "MB\n" +
                "    [Alloc] " + nonHeapAllocated + "MB\n" +
                "    [Total] " + nonHeapTotal + "MB\n";

        logger.info("RAM stats:\n{}", out);
    }
}
