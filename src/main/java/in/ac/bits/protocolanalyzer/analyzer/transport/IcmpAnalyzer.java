package in.ac.bits.protocolanalyzer.analyzer.transport;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import in.ac.bits.protocolanalyzer.analyzer.CustomAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.PacketWrapper;
import in.ac.bits.protocolanalyzer.analyzer.event.PacketTypeDetectionEvent;
import in.ac.bits.protocolanalyzer.persistence.entity.IcmpEntity;
import in.ac.bits.protocolanalyzer.persistence.repository.AnalysisRepository;
import in.ac.bits.protocolanalyzer.protocol.Protocol;
import in.ac.bits.protocolanalyzer.utils.Beautify;
import in.ac.bits.protocolanalyzer.utils.BitOperator;
import in.ac.bits.protocolanalyzer.utils.ByteOperator;
import java.lang.String;
import java.util.Arrays;
import org.pcap4j.packet.Packet;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IcmpAnalyzer implements CustomAnalyzer {
  private byte[] icmpHeader;

  private String indexName;

  private AnalysisRepository repository;

  private int startByte;

  private int endByte;

  private EventBus eventBus;

  /**
   * Configures the instance according to the parameters.
   * @param {eventBus}
   * @param {repository}
   * @param {sessionName}
   */
  public void configure(EventBus eventBus, AnalysisRepository repository, String sessionName) {
    this.eventBus = eventBus;
    this.eventBus.register(this);
    this.repository = repository;
    this.indexName = "protocol_" + sessionName;
  }

  private void setIcmpHeader(PacketWrapper packetWrapper) {
    Packet packet = packetWrapper.getPacket();
    int startByte = packetWrapper.getStartByte();
    byte[] rawPacket = packet.getRawData();
    this.icmpHeader = Arrays
    		.copyOfRange(rawPacket, startByte, startByte + IcmpHeader.TOTAL_HEADER_LENGTH);
  }

  public void setStartByte(PacketWrapper packetWrapper) {
    this.startByte = packetWrapper.getStartByte() + IcmpHeader.TOTAL_HEADER_LENGTH;
  }

  public void setEndByte(PacketWrapper packetWrapper) {
    this.endByte = packetWrapper.getEndByte();
  }

  public void publishTypeDetectionEvent(String nextPacketType, int startByte, int endByte) {
    this.eventBus
    .post(new PacketTypeDetectionEvent(nextPacketType, startByte, endByte));
  }

  /**
   * Analyze the given ICMP-Header and return its type as short.
   * @param {icmpHeader}
   */
  public short getType(byte[] icmpHeader) {
    byte[] type = BitOperator
    		.parse(icmpHeader, IcmpHeader.TYPE__START_BIT, IcmpHeader.TYPE__END_BIT);
    return  ByteOperator.parseBytesshort(type);
  }

  /**
   * Analyze the given ICMP-Header and return its code as short.
   * @param {icmpHeader}
   */
  public short getCode(byte[] icmpHeader) {
    byte[] code = BitOperator
    		.parse(icmpHeader, IcmpHeader.CODE_START_BIT, IcmpHeader.CODE_END_BIT);
   return ByteOperator.parseBytesshort(code);
  }

  public String getHdrChecksum(byte[] icmpHeader) {
    byte[] hdrchecksum = BitOperator
    		.parse(icmpHeader, IcmpHeader.HDRCHECKSUM_START_BIT, IcmpHeader.HDRCHECKSUM_END_BIT);
    return Beautify.beautify(hdrchecksum, "hex");
  }

  /**
   * Analyze the given PacketWrapper object and save it in repository
   * @param {packetWrapper}
   */
  @Subscribe
  public void analyze(PacketWrapper packetWrapper) {
    if (Protocol.get("ICMP").equalsIgnoreCase(packetWrapper.getPacketType())) {
      setIcmpHeader(packetWrapper);
      String nextPacketType = setNextProtocolType();
      setStartByte(packetWrapper);
      setEndByte(packetWrapper);
      publishTypeDetectionEvent(nextPacketType, startByte, endByte);
      IcmpEntity entity = new IcmpEntity();
      entity.setPacketId(packetWrapper.getPacketId());
      entity.setHdrChecksum(getHdrChecksum(icmpHeader));
      entity.setType_(getType(icmpHeader));
      entity.setCode(getCode(icmpHeader));
      IndexQueryBuilder builder = new IndexQueryBuilder();
      IndexQuery query = builder
    		  .withIndexName(this.indexName)
    		  .withType("icmp")
    		  .withId(String.valueOf(packetWrapper.getPacketId()))
    		  .withObject(entity).build();
      repository.save(query);
    }
  }

  /**
   * returns the next header-type as string.
   */ 
  public String setNextProtocolType() {
    String nextHeaderType = "NO_CONDITIONAL_HEADER_FIELD";
    return Protocol.get("END_PROTOCOL");
  }
}
