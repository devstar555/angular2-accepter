package modules;
import com.google.inject.AbstractModule;

import play.libs.akka.AkkaGuiceSupport;

public class SchedulerModule extends AbstractModule implements AkkaGuiceSupport {
	@Override
	protected void configure() {
		bind(SchedulerTask.class).asEagerSingleton();
	}
}
