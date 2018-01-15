package in.ac.bits.protocolanalyzer.analyzer.network;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import in.ac.bits.protocolanalyzer.analyzer.CustomAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.PacketWrapper;
import in.ac.bits.protocolanalyzer.analyzer.event.PacketTypeDetectionEvent;
import in.ac.bits.protocolanalyzer.persistence.entity.IPv4Entity;
import in.ac.bits.protocolanalyzer.persistence.repository.AnalysisRepository;
import in.ac.bits.protocolanalyzer.protocol.Protocol;
import in.ac.bits.protocolanalyzer.utils.Beautify;
import in.ac.bits.protocolanalyzer.utils.BitOperator;
import in.ac.bits.protocolanalyzer.utils.ByteOperator;

import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.pcap4j.packet.Packet;

import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IPv4Analyzer implements CustomAnalyzer {
  private byte[] ipv4Header;

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

  private void setIPv4Header(PacketWrapper packetWrapper) {
    Packet packet = packetWrapper.getPacket();
    int tempStartByte = packetWrapper.getStartByte();
    byte[] rawPacket = packet.getRawData();
    this.ipv4Header = Arrays
    		.copyOfRange(rawPacket, tempStartByte, tempStartByte + IPv4Header.TOTAL_HEADER_LENGTH);
  }

  public void setStartByte(PacketWrapper packetWrapper) {
    this.startByte = packetWrapper.getStartByte() + IPv4Header.TOTAL_HEADER_LENGTH;
  }

  public void setEndByte(PacketWrapper packetWrapper) {
    this.endByte = packetWrapper.getEndByte();
  }

  public void publishTypeDetectionEvent(String nextPacketType, int startByte, int endByte) {
    this.eventBus.post(new PacketTypeDetectionEvent(nextPacketType, startByte, endByte));
  }
  
  /**
   * returns the version of given ipv4Header.
   * @param {ipv4Header}
   */
  public byte getVersion(byte[] ipv4Header) {
    byte[] version = BitOperator
    		.parse(ipv4Header, IPv4Header.VERSION_START_BIT, IPv4Header.VERSION_END_BIT);
    return ByteOperator.parseBytesbyte(version);
  }
  
  /**
   * returns the IHL value of given ipv4Header.
   * @param {ipv4Header}
   */
  public byte getIhl(byte[] ipv4Header) {
    byte[] ihl = BitOperator
    		.parse(ipv4Header, IPv4Header.IHL_START_BIT, IPv4Header.IHL_END_BIT);
    return ByteOperator.parseBytesbyte(ihl);
  }

  /**
   * returns the Differentiated services as short of given ipv4Header.
   * @param {ipv4Header}
   */
  public short getDiffserv(byte[] ipv4Header) {
    byte[] diffserv = BitOperator
    		.parse(ipv4Header, IPv4Header.DIFFSERV_START_BIT, IPv4Header.DIFFSERV_END_BIT);
    return ByteOperator.parseBytesshort(diffserv);
  }

  public String getTotalLen(byte[] ipv4Header) {
    byte[] totallen = BitOperator
    		.parse(ipv4Header, IPv4Header.TOTALLEN_START_BIT, IPv4Header.TOTALLEN_END_BIT);
    return Beautify.beautify(totallen, "hex");
  }

  /**
   * returns the identifier as int of given ipv4Header.
   * @param {ipv4Header}
   */
  public int getIdentification(byte[] ipv4Header) {
    byte[] identification = BitOperator
    		.parse(ipv4Header, IPv4Header.IDENTIFICATION_START_BIT, IPv4Header.IDENTIFICATION_END_BIT);
    return ByteOperator.parseBytesint(identification);
  }
  
  /**
   * returns the flag as byte of given ipv4Header.
   * @param {ipv4Header}
   */
  public byte getFlags(byte[] ipv4Header) {
    byte[] flags = BitOperator
    		.parse(ipv4Header, IPv4Header.FLAGS_START_BIT, IPv4Header.FLAGS_END_BIT);
    return ByteOperator.parseBytesbyte(flags);
  }
  
  /**
   * returns the frag offset of given ipv4Header.
   * @param {ipv4Header}
   */
  public short getFragOffset(byte[] ipv4Header) {
    byte[] fragoffset = BitOperator
    		.parse(ipv4Header, IPv4Header.FRAGOFFSET_START_BIT, IPv4Header.FRAGOFFSET_END_BIT);
    return  ByteOperator.parseBytesshort(fragoffset);
  }
  
  /**
   * returns the TTL value of given ipv4Header.
   * @param {ipv4Header}
   */
  public short getTtl(byte[] ipv4Header) {
    byte[] ttl = BitOperator
    		.parse(ipv4Header, IPv4Header.TTL_START_BIT, IPv4Header.TTL_END_BIT);
    return ByteOperator.parseBytesshort(ttl);
  }

  public String getProtocol(byte[] ipv4Header) {
    byte[] protocol = BitOperator
    		.parse(ipv4Header, IPv4Header.PROTOCOL_START_BIT, IPv4Header.PROTOCOL_END_BIT);
    return Hex.encodeHexString(protocol);
  }

  public String getHdrChecksum(byte[] ipv4Header) {
    byte[] hdrchecksum = BitOperator
    		.parse(ipv4Header, IPv4Header.HDRCHECKSUM_START_BIT, IPv4Header.HDRCHECKSUM_END_BIT);
    return Beautify.beautify(hdrchecksum, "hex");
  }

  public String getSrcAddr(byte[] ipv4Header) {
    byte[] srcaddr = BitOperator
    		.parse(ipv4Header, IPv4Header.SRCADDR_START_BIT, IPv4Header.SRCADDR_END_BIT);
    return Beautify.beautify(srcaddr, "ip4");
  }

  public String getDstAddr(byte[] ipv4Header) {
    byte[] dstaddr = BitOperator
    		.parse(ipv4Header, IPv4Header.DSTADDR_START_BIT, IPv4Header.DSTADDR_END_BIT);
    return Beautify.beautify(dstaddr, "ip4");
  }

  /**
   * Analyze the given PacketWrapper object and save it in repository
   * @param {packetWrapper}
   */
  @Subscribe
  public void analyze(PacketWrapper packetWrapper) {
    if (Protocol.get("IPV4").equalsIgnoreCase(packetWrapper.getPacketType())) {
      setIPv4Header(packetWrapper);
      String nextPacketType = setNextProtocolType();
      setStartByte(packetWrapper);
      setEndByte(packetWrapper);
      publishTypeDetectionEvent(nextPacketType, startByte, endByte);
      IPv4Entity entity = new IPv4Entity();
      entity.setPacketId(packetWrapper.getPacketId());
      entity.setTotalLen(getTotalLen(ipv4Header));
      entity.setDstAddr(getDstAddr(ipv4Header));
      entity.setDiffserv(getDiffserv(ipv4Header));
      entity.setVersion(getVersion(ipv4Header));
      entity.setHdrChecksum(getHdrChecksum(ipv4Header));
      entity.setIdentification(getIdentification(ipv4Header));
      entity.setSrcAddr(getSrcAddr(ipv4Header));
      entity.setFlags(getFlags(ipv4Header));
      entity.setFragOffset(getFragOffset(ipv4Header));
      entity.setIhl(getIhl(ipv4Header));
      entity.setProtocol(getProtocol(ipv4Header));
      entity.setTtl(getTtl(ipv4Header));
      IndexQueryBuilder builder = new IndexQueryBuilder();
      IndexQuery query = builder.withIndexName(this.indexName)
    		  .withType("ipv4").withId(String.valueOf(packetWrapper.getPacketId()))
    		  .withObject(entity).build();
      repository.save(query);
    }
  }

  /**
   * returns the next header-type as string.
   */  
  public String setNextProtocolType() {
    String nextHeaderType = getProtocol(this.ipv4Header);
    switch(nextHeaderType) {
      case "11": return Protocol.get("UDP");
      case "06": return Protocol.get("TCP");
      case "01": return Protocol.get("ICMP");
      default: return Protocol.get("END_PROTOCOL");
    }
  }
}
