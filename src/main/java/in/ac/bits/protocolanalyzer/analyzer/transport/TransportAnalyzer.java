/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.bits.protocolanalyzer.analyzer.transport;

import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;

import in.ac.bits.protocolanalyzer.analyzer.GenericAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.PacketWrapper;
import in.ac.bits.protocolanalyzer.persistence.entity.TransportAnalyzerEntity;

/**
 *
 * @author amit
 * @author crygnus
 * 
 */

@Component
public class TransportAnalyzer implements GenericAnalyzer {

    private EventBus transportEventBus;

    public void setEventBus(EventBus eventBus) {
        this.transportEventBus = eventBus;
    }

    public void publishToEventBus(PacketWrapper packetWrapper) {
        /* post the event to corresponding event-bus */
        this.transportEventBus.post(packetWrapper);
    }

    public void analyzePacket(PacketWrapper packetWrapper) {
        // analyze and pass to hooks
        TransportAnalyzerEntity tae = new TransportAnalyzerEntity();
        tae.setPacketId(packetWrapper.getPacketId());

        publishToEventBus(packetWrapper);

    }

}
