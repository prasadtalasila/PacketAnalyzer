package in.ac.bits.protocolanalyzer.analyzer.transport;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import in.ac.bits.protocolanalyzer.analyzer.CustomAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.PacketWrapper;
import in.ac.bits.protocolanalyzer.analyzer.event.PacketTypeDetectionEvent;
import in.ac.bits.protocolanalyzer.persistence.entity.TcpEntity;
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
public class TcpAnalyzer implements CustomAnalyzer {
  private byte[] tcpHeader;

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

  private void setTcpHeader(PacketWrapper packetWrapper) {
    Packet packet = packetWrapper.getPacket();
    int tempStartByte = packetWrapper.getStartByte();
    byte[] rawPacket = packet.getRawData();
    this.tcpHeader = Arrays
    		.copyOfRange(rawPacket, tempStartByte, tempStartByte + TcpHeader.TOTAL_HEADER_LENGTH);
  }

  public void setStartByte(PacketWrapper packetWrapper) {
    this.startByte = packetWrapper.getStartByte() + TcpHeader.TOTAL_HEADER_LENGTH;
  }

  public void setEndByte(PacketWrapper packetWrapper) {
    this.endByte = packetWrapper.getEndByte();
  }

  public void publishTypeDetectionEvent(String nextPacketType, int startByte, int endByte) {
    this.eventBus
    .post(new PacketTypeDetectionEvent(nextPacketType, startByte, endByte));
  }

  /**
   * Analyze the given TCP-Header and return its SRCPort as int.                           //TOMODIFY
   * @param {tcpHeader}
   */
  public int getSrcPort(byte[] tcpHeader) {
    byte[] srcport = BitOperator
    		.parse(tcpHeader, TcpHeader.SRCPORT_START_BIT, TcpHeader.SRCPORT_END_BIT);
    return ByteOperator.parseBytesint(srcport);
  }

  /**
   * Analyze the given TCP-Header and return its DSTPort as int.                           //TOMODIFY
   * @param {tcpHeader}
   */
  public int getDstPort(byte[] tcpHeader) {
    byte[] dstport = BitOperator
    		.parse(tcpHeader, TcpHeader.DSTPORT_START_BIT, TcpHeader.DSTPORT_END_BIT);
    return ByteOperator.parseBytesint(dstport);
  }

  /**
   * Analyze the given TCP-Header and return its SeqNo as long.                           //TOMODIFY
   * @param {tcpHeader}
   */
  public long getSeqNo(byte[] tcpHeader) {
    byte[] seqno = BitOperator
    		.parse(tcpHeader, TcpHeader.SEQNO_START_BIT, TcpHeader.SEQNO_END_BIT);
    return ByteOperator.parseByteslong(seqno);
  }

  /**
   * Analyze the given TCP-Header and return its AckNo as long.                           //TOMODIFY
   * @param {tcpHeader}
   */
  public long getAckNo(byte[] tcpHeader) {
    byte[] ackno = BitOperator
    		.parse(tcpHeader, TcpHeader.ACKNO_START_BIT, TcpHeader.ACKNO_END_BIT);
    return ByteOperator.parseByteslong(ackno);
  }

  /**
   * Analyze the given TCP-Header and return its data offset as byte.
   * @param {tcpHeader}
   */
  public byte getDataOffset(byte[] tcpHeader) {
    byte[] dataoffset = BitOperator
    		.parse(tcpHeader, TcpHeader.DATAOFFSET_START_BIT, TcpHeader.DATAOFFSET_END_BIT);
    return ByteOperator.parseBytesbyte(dataoffset);
  }

  /**
   * Analyze the given TCP-Header and return its res as byte.                   //TOMODIFY
   * @param {tcpHeader}
   */
  public byte getRes(byte[] tcpHeader) {
    byte[] res = BitOperator
    		.parse(tcpHeader, TcpHeader.RES_START_BIT, TcpHeader.RES_END_BIT);
    return ByteOperator.parseBytesbyte(res);
  }

  /**
   * Analyze the given TCP-Header and return its flag as short.
   * @param {tcpHeader}
   */
  public short getFlags(byte[] tcpHeader) {
    byte[] flags = BitOperator
    		.parse(tcpHeader, TcpHeader.FLAGS_START_BIT, TcpHeader.FLAGS_END_BIT);
    return ByteOperator.parseBytesshort(flags);
  }

  /**
   * Analyze the given TCP-Header and return its window as int.
   * @param {tcpHeader}
   */
  public int getWindow(byte[] tcpHeader) {
    byte[] window = BitOperator
    		.parse(tcpHeader, TcpHeader.WINDOW_START_BIT, TcpHeader.WINDOW_END_BIT);
    return ByteOperator.parseBytesint(window);
  }

  public String getChecksum(byte[] tcpHeader) {
    byte[] checksum = BitOperator
    		.parse(tcpHeader, TcpHeader.CHECKSUM_START_BIT, TcpHeader.CHECKSUM_END_BIT);
    return Beautify.beautify(checksum, "hex");
  }

  public int getUrgentPtr(byte[] tcpHeader) {
    byte[] urgentptr = BitOperator
    		.parse(tcpHeader, TcpHeader.URGENTPTR_START_BIT, TcpHeader.URGENTPTR_END_BIT);
    return ByteOperator.parseBytesint(urgentptr);
  }

  @Subscribe
  public void analyze(PacketWrapper packetWrapper) {
    if (Protocol.get("TCP").equalsIgnoreCase(packetWrapper.getPacketType())) {
      setTcpHeader(packetWrapper);
      String nextPacketType = setNextProtocolType();
      setStartByte(packetWrapper);
      setEndByte(packetWrapper);
      publishTypeDetectionEvent(nextPacketType, startByte, endByte);
      TcpEntity entity = new TcpEntity();
      entity.setPacketId(packetWrapper.getPacketId());
      entity.setWindow(getWindow(tcpHeader));
      entity.setSeqNo(getSeqNo(tcpHeader));
      entity.setRes(getRes(tcpHeader));
      entity.setSrcPort(getSrcPort(tcpHeader));
      entity.setUrgentPtr(getUrgentPtr(tcpHeader));
      entity.setChecksum(getChecksum(tcpHeader));
      entity.setAckNo(getAckNo(tcpHeader));
      entity.setDstPort(getDstPort(tcpHeader));
      entity.setFlags(getFlags(tcpHeader));
      entity.setDataOffset(getDataOffset(tcpHeader));
      IndexQueryBuilder builder = new IndexQueryBuilder();
      IndexQuery query = builder
    		  .withIndexName(this.indexName)
    		  .withType("tcp")
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
