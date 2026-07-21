package com.example.scheduler;

import com.ligero.Ligero;
import com.ligero.scheduler.Scheduler;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Background jobs with ligero-scheduler: a repeating tick, a one-off warm-up and
 * a daily task — each on its own virtual thread. The scheduler is closed on
 * app shutdown via an onStop hook.
 */
public final class SchedulerDemoApp {

    public static void main(String[] args) throws Exception {
        Ligero app = Ligero.create(8080);
        Scheduler scheduler = new Scheduler();
        AtomicLong ticks = new AtomicLong();

        scheduler.fixedRate(Duration.ofSeconds(5),
            () -> System.out.println("tick " + ticks.incrementAndGet()));
        scheduler.once(Duration.ofSeconds(1),
            () -> System.out.println("warm-up done"));
        scheduler.dailyAt(LocalTime.of(2, 0), ZoneId.systemDefault(),
            () -> System.out.println("nightly report generated"));

        app.get("/ticks", ctx -> ctx.json(Map.of("ticks", ticks.get())));
        app.onStop(scheduler::close);
        app.start();
    }
}
