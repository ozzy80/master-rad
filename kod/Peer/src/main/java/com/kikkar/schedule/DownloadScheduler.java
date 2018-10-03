package com.kikkar.schedule;

import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;

public interface DownloadScheduler {

	void startDownload();

	Pair<String, PacketWrapper> getNextPacket();

	void processPacket(Pair<String, PacketWrapper> packetPair);
}
