package id.ac.ui.cs.advprog.beinventorycatalog.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

@Component
public class ProductEventListener {
    
    private static final Logger logger = Logger.getLogger(ProductEventListener.class.getName());

    @Async
    @EventListener
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        // This is an Observer Pattern that handles events asynchronously
        logger.info("Asynchronous Observer logic executed. Product Event triggered: " + event.getSource().toString());
    }
}
