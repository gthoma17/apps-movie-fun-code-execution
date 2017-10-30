package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Map;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private final AlbumsUpdater albumsUpdater;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AlbumsUpdateScheduler(AlbumsUpdater albumsUpdater, DataSource dataSource) {
        this.albumsUpdater = albumsUpdater;
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 2 * MINUTES)
    public void run() {
        if (UpdateShouldRun()){
            runUpdate();
        }
    }

    private void runUpdate() {
        setUpdateRunningFlag();
        try {
            logger.debug("Starting albums update");
            albumsUpdater.update();

            logger.debug("Finished albums update");

        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
    }

    private void setUpdateRunningFlag() {
        jdbcTemplate.update("UPDATE album_scheduler_task" +
                " SET started_at = now()" +
                " WHERE started_at < date_sub(now(), INTERVAL 2 MINUTE)");
    }

    private boolean UpdateShouldRun(){
        Map<String, Object> mostRecentUpdate = jdbcTemplate
                .queryForMap("SELECT MAX(started_at) FROM album_scheduler_task LIMIT 1");
        Timestamp mostRecentUpdateTime = (Timestamp) mostRecentUpdate.get("MAX(started_at)");
        int millisecondsInTwoMinutes = 2 * 60 * 1000;
        Timestamp twoMinutesBeforeNow = new Timestamp(System.currentTimeMillis() - millisecondsInTwoMinutes);

        return mostRecentUpdateTime.before(twoMinutesBeforeNow);
    }
}
