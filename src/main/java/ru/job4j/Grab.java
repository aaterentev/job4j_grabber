package ru.job4j;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.Store;

public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
