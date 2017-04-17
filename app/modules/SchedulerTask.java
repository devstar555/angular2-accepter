package modules;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import akka.actor.ActorSystem;
import model.config.DatabaseConfigCache;
import model.filter.DatabaseFiltersCache;
import model.platformaccountgroup.DatabasePlatformAccountGroupCache;
import model.zipcode.DatabaseZipCodeCache;
import play.Logger;
import play.api.inject.ApplicationLifecycle;
import scala.concurrent.duration.Duration;

public class SchedulerTask {

	private boolean isRunning = false;

	@Inject
	public SchedulerTask(final ActorSystem system) {
		// Initial delay and frequency
		system.scheduler().schedule(Duration.create(0, TimeUnit.MILLISECONDS), Duration.create(5, TimeUnit.MINUTES),
				run(), system.dispatcher());
	}

	private Runnable run() {
		return new Runnable() {
			@Override
			public void run() {
				isRunning = true;
				refreshZipCodeCache();
				refreshConfigCache();
				refreshFilterCache();
				refreshPlatformAccountGroupCache();
				isRunning = false;
			}
		};
	}

	private void refreshZipCodeCache() {
		try {
			DatabaseZipCodeCache.refreshCache();
		} catch (Exception e) {
			Logger.error("Error occued while refreshing zipcode cache", e);
		}
	}

	private void refreshConfigCache() {
		try {
			DatabaseConfigCache.refreshCache();
		} catch (Exception e) {
			Logger.error("Error occued while refreshing config cache", e);
		}
	}

	private void refreshFilterCache() {
		try {
			DatabaseFiltersCache.refreshCache();
		} catch (Exception e) {
			Logger.error("Error occued while refreshing filter cache", e);
		}
	}

	private void refreshPlatformAccountGroupCache() {
		try {
			DatabasePlatformAccountGroupCache.refreshCache();
		} catch (Exception e) {
			Logger.error("Error occued while refreshing platformAccountGroup cache", e);
		}
	}

	@Inject
	public void waitForSchedulerTaskCompletion(ApplicationLifecycle lifecycle) {
		lifecycle.addStopHook(() -> {
			int waitCounter = 0;
			while (isRunning && ++waitCounter <= 5) {
				Logger.warn("App is going to stop, hence waiting on running scheduler task");
				Thread.sleep(100);
			
				if (!isRunning)
					Logger.info("Scheduler task has stopped");
			}
			
			return CompletableFuture.completedFuture(null);
		});
	}
}