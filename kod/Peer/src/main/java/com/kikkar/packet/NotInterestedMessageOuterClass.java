// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NotInterestedMessage.proto

package com.kikkar.packet;

public final class NotInterestedMessageOuterClass {
  private NotInterestedMessageOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_kikkar_packet_NotInterestedMessage_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_kikkar_packet_NotInterestedMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\032NotInterestedMessage.proto\022\021com.kikkar" +
      ".packet\"(\n\024NotInterestedMessage\022\020\n\010video" +
      "Num\030\001 \001(\005B\002P\001b\006proto3"
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
    internal_static_com_kikkar_packet_NotInterestedMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_kikkar_packet_NotInterestedMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_kikkar_packet_NotInterestedMessage_descriptor,
        new java.lang.String[] { "VideoNum", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
