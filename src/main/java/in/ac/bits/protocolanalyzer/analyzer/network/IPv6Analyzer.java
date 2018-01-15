package in.ac.bits.protocolanalyzer.analyzer.network;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import in.ac.bits.protocolanalyzer.analyzer.CustomAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.PacketWrapper;
import in.ac.bits.protocolanalyzer.analyzer.event.PacketTypeDetectionEvent;
import in.ac.bits.protocolanalyzer.persistence.entity.IPv6Entity;
import in.ac.bits.protocolanalyzer.persistence.repository.AnalysisRepository;
import in.ac.bits.protocolanalyzer.protocol.Protocol;
import in.ac.bits.protocolanalyzer.utils.Beautify;
import in.ac.bits.protocolanalyzer.utils.BitOperator;
import in.ac.bits.protocolanalyzer.utils.ByteOperator;
import java.lang.String;
import java.util.Arrays;
import org.apache.commons.codec.binary.Hex;
import org.pcap4j.packet.Packet;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IPv6Analyzer implements CustomAnalyzer {
  private byte[] ipv6Header;

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

  private void setIPv6Header(PacketWrapper packetWrapper) {
    Packet packet = packetWrapper.getPacket();
    int tempStartByte = packetWrapper.getStartByte();
    byte[] rawPacket = packet.getRawData();
    this.ipv6Header = Arrays
    		.copyOfRange(rawPacket, tempStartByte, tempStartByte + IPv6Header.TOTAL_HEADER_LENGTH);
  }

  public void setStartByte(PacketWrapper packetWrapper) {
    this.startByte = packetWrapper.getStartByte() + IPv6Header.TOTAL_HEADER_LENGTH;
  }

  public void setEndByte(PacketWrapper packetWrapper) {
    this.endByte = packetWrapper.getEndByte();
  }

  public void publishTypeDetectionEvent(String nextPacketType, int startByte, int endByte) {
    this.eventBus
    .post(new PacketTypeDetectionEvent(nextPacketType, startByte, endByte));
  }

  /**
   * returns the version of given ipv6Header.
   * @param {ipv6Header}
   */
  public byte getVersion(byte[] ipv6Header) {
    byte[] version = BitOperator.
    		parse(ipv6Header, IPv6Header.VERSION_START_BIT, IPv6Header.VERSION_END_BIT);
    return ByteOperator.parseBytesbyte(version);
  }

  public short getTrafficClass(byte[] ipv6Header) {
    byte[] trafficclass = BitOperator.
    		parse(ipv6Header, IPv6Header.TRAFFICCLASS_START_BIT, IPv6Header.TRAFFICCLASS_END_BIT);
    return ByteOperator.parseBytesshort(trafficclass);
  }

  /**
   * returns the flow label as int.
   */ 
  public int getFlowLabel(byte[] ipv6Header) {
    byte[] flowlabel = BitOperator.
    		parse(ipv6Header, IPv6Header.FLOWLABEL_START_BIT, IPv6Header.FLOWLABEL_END_BIT);
    return ByteOperator.parseBytesint(flowlabel);
  }

  /**
   * returns the payload length as int.
   */ 
  public int getPayloadLen(byte[] ipv6Header) {
    byte[] payloadlen = BitOperator.
    		parse(ipv6Header, IPv6Header.PAYLOADLEN_START_BIT, IPv6Header.PAYLOADLEN_END_BIT);
    return ByteOperator.parseBytesint(payloadlen);
  }

  public String getNextHdr(byte[] ipv6Header) {
    byte[] nexthdr = BitOperator.
    		parse(ipv6Header, IPv6Header.NEXTHDR_START_BIT, IPv6Header.NEXTHDR_END_BIT);
    return Hex.encodeHexString(nexthdr);
  }

  /**
   * returns the hop limit as short.
   */ 
  public short getHopLimit(byte[] ipv6Header) {
    byte[] hoplimit = BitOperator.
    		parse(ipv6Header, IPv6Header.HOPLIMIT_START_BIT, IPv6Header.HOPLIMIT_END_BIT);
    return ByteOperator.parseBytesshort(hoplimit);
  }

  public String getSrcAddr(byte[] ipv6Header) {
    byte[] srcaddr = BitOperator.
    		parse(ipv6Header, IPv6Header.SRCADDR_START_BIT, IPv6Header.SRCADDR_END_BIT);
    return Beautify.beautify(srcaddr, "hex4");
  }

  public String getDstAddr(byte[] ipv6Header) {
    byte[] dstaddr = BitOperator.
    		parse(ipv6Header, IPv6Header.DSTADDR_START_BIT, IPv6Header.DSTADDR_END_BIT);
    return Beautify.beautify(dstaddr, "hex4");
  }

  /**
   * Analyze the given PacketWrapper object and save it in repository
   * @param {packetWrapper}
   */
  @Subscribe
  public void analyze(PacketWrapper packetWrapper) {
    if (Protocol.get("IPV6").equalsIgnoreCase(packetWrapper.getPacketType())) {
      setIPv6Header(packetWrapper);
      String nextPacketType = setNextProtocolType();
      setStartByte(packetWrapper);
      setEndByte(packetWrapper);
      publishTypeDetectionEvent(nextPacketType, startByte, endByte);
      IPv6Entity entity = new IPv6Entity();
      entity.setPacketId(packetWrapper.getPacketId());
      entity.setFlowLabel(getFlowLabel(ipv6Header));
      entity.setDstAddr(getDstAddr(ipv6Header));
      entity.setNextHdr(getNextHdr(ipv6Header));
      entity.setVersion(getVersion(ipv6Header));
      entity.setHopLimit(getHopLimit(ipv6Header));
      entity.setPayloadLen(getPayloadLen(ipv6Header));
      entity.setTrafficClass(getTrafficClass(ipv6Header));
      entity.setSrcAddr(getSrcAddr(ipv6Header));
      IndexQueryBuilder builder = new IndexQueryBuilder();
      IndexQuery query = builder.withIndexName(this.indexName)
    		  .withType("ipv6")
    		  .withId(String.valueOf(packetWrapper.getPacketId()))
    		  .withObject(entity).build();
      repository.save(query);
    }
  }

  /**
   * returns the next header-type as string.
   */ 
  public String setNextProtocolType() {
    String nextHeaderType = getNextHdr(this.ipv6Header);
    switch(nextHeaderType) {
      case "11": return Protocol.get("UDP");
      case "06": return Protocol.get("TCP");
      case "01": return Protocol.get("ICMP");
      default: return Protocol.get("END_PROTOCOL");
    }
  }
}
