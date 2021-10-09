package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {
    private static Properties properties;

    private static void readProp() {
        properties = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getInterval() {
        readProp();
        int rsl = 0;
        try {
            rsl = Integer.parseInt(properties.getProperty("rabbit.interval"));
        } catch (Exception e) {
            throw new IllegalArgumentException("file rabbit.properties is corrupted");
        }
        return rsl;
    }

    private static Connection initConnection() {
        readProp();
        Connection cn = null;
        try {
            Class.forName(properties.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    properties.getProperty("url"),
                    properties.getProperty("username"),
                    properties.getProperty("password")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cn;
    }

    public static void main(String[] args) {
        Connection cn = initConnection();
        String sql = "insert into rabbit(created_date) values(?);";
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", cn);
            data.put("sql", sql);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        private LocalDateTime created = LocalDateTime.now();

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            String sql = (String) context.getJobDetail().getJobDataMap().get("sql");
            this.add(cn, sql);
        }

        private void add(Connection cn, String sql) {
            try (PreparedStatement statement = cn.prepareStatement(sql)) {
                statement.setTimestamp(1, getTimestamp(this.created));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private LocalDateTime getLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

        private Timestamp getTimestamp(LocalDateTime localDateTime) {
            return Timestamp.valueOf(localDateTime);
        }
    }
}
