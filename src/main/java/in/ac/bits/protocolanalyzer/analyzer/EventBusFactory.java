package in.ac.bits.protocolanalyzer.analyzer;

import java.util.HashMap;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;

/**
 * Factory object for eventbuses. A unique eventbus id will belong to a unique
 * eventbus
 * 
 * @author crygnus
 *
 */

@Service
@Configurable
public class EventBusFactory {

    private HashSet<String> eventBusIds = new HashSet<String>();
    private HashMap<String, EventBus> eventBuses = new HashMap<String, EventBus>();

    public EventBus getEventBus(String id) {
        if (!eventBusIds.contains(id)) {
            EventBus eventBus = new EventBus(id);
            eventBusIds.add(id);
            eventBuses.put(id, eventBus);
            return eventBus;
        } else {
            return eventBuses.get(id);
        }
    }
}
