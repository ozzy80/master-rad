// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: PongMessage.proto

package com.kikkar.packet;

public interface PongMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.kikkar.packet.PongMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 responsePingId = 1;</code>
   */
  int getResponsePingId();

  /**
   * <code>int32 uploadLinkNum = 2;</code>
   */
  int getUploadLinkNum();

  /**
   * <code>int32 downloadLinkNum = 3;</code>
   */
  int getDownloadLinkNum();

  /**
   * <code>int32 bufferVideoNum = 4;</code>
   */
  int getBufferVideoNum();
}
