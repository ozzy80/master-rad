// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: TerminatedReason.proto

package com.kikkar.packet;

public final class TerminatedReasonOuterClass {
  private TerminatedReasonOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026TerminatedReason.proto\022\021com.kikkar.pac" +
      "ket*w\n\020TerminatedReason\022\021\n\rLEAVE_PROGRAM" +
      "\020\000\022\021\n\rBLOCK_TIMEOUT\020\001\022\032\n\026PACKET_NUMBER_D" +
      "ISORDER\020\002\022\r\n\tDEAD_PEER\020\003\022\022\n\016NEW_CONNECTI" +
      "ON\020\004B\002P\001b\006proto3"
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
  }

  // @@protoc_insertion_point(outer_class_scope)
}
