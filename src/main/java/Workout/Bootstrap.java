package Workout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Bootstrap implements ApplicationListener<ApplicationReadyEvent> {

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class);
        Process process = new Process();
        process.configure();
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        for (String prof : event.getApplicationContext().getEnvironment().getActiveProfiles()) {
            System.out.println("Profile " + prof);
        }
    }

}
