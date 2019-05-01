package assignment2.e1;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

public class FileVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        super.start();

        EventBus eventBus = this.vertx.eventBus();

        eventBus.consumer(BusAddresses.FILE_ADDED, message -> {
            System.out.println("File added: " + message.body());
        });
    }
}
