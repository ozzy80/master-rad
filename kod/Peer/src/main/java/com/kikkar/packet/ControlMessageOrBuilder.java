// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ControlMessage.proto

package com.kikkar.packet;

public interface ControlMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.kikkar.packet.ControlMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 messageId = 1;</code>
   */
  int getMessageId();

  /**
   * <code>int32 currentDisplayedVideoNum = 2;</code>
   */
  int getCurrentDisplayedVideoNum();

  /**
   * <code>int64 timeInMilliseconds = 3;</code>
   */
  long getTimeInMilliseconds();
}
