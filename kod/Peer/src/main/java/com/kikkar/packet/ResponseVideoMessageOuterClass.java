// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ResponseVideoMessage.proto

package com.kikkar.packet;

public final class ResponseVideoMessageOuterClass {
  private ResponseVideoMessageOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_kikkar_packet_ResponseVideoMessage_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_kikkar_packet_ResponseVideoMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\032ResponseVideoMessage.proto\022\021com.kikkar" +
      ".packet\"I\n\024ResponseVideoMessage\022\020\n\010video" +
      "Num\030\001 \001(\005\022\020\n\010chunkNum\030\002 \001(\005\022\r\n\005video\030\003 \001" +
      "(\014B\002P\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_com_kikkar_packet_ResponseVideoMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_kikkar_packet_ResponseVideoMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_kikkar_packet_ResponseVideoMessage_descriptor,
        new java.lang.String[] { "VideoNum", "ChunkNum", "Video", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
