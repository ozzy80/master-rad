// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: RequestMessage.proto

package com.kikkar.packet;

public final class RequestMessageOuterClass {
  private RequestMessageOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_kikkar_packet_RequestMessage_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_kikkar_packet_RequestMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\024RequestMessage.proto\022\021com.kikkar.packe" +
      "t\032\024ConnectionType.proto\"r\n\016RequestMessag" +
      "e\022\021\n\trequestId\030\001 \001(\005\022\022\n\nclubNumber\030\002 \001(\005" +
      "\0229\n\016connectionType\030\003 \001(\0162!.com.kikkar.pa" +
      "cket.ConnectionTypeB\002P\001b\006proto3"
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
          com.kikkar.packet.ConnectionTypeOuterClass.getDescriptor(),
        }, assigner);
    internal_static_com_kikkar_packet_RequestMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_kikkar_packet_RequestMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_kikkar_packet_RequestMessage_descriptor,
        new java.lang.String[] { "RequestId", "ClubNumber", "ConnectionType", });
    com.kikkar.packet.ConnectionTypeOuterClass.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
