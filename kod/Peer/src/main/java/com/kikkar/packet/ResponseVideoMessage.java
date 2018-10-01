// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ResponseVideoMessage.proto

package com.kikkar.packet;

/**
 * Protobuf type {@code com.kikkar.packet.ResponseVideoMessage}
 */
public  final class ResponseVideoMessage extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.kikkar.packet.ResponseVideoMessage)
    ResponseVideoMessageOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ResponseVideoMessage.newBuilder() to construct.
  private ResponseVideoMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ResponseVideoMessage() {
    videoNum_ = 0;
    chunkNum_ = 0;
    video_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ResponseVideoMessage(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            videoNum_ = input.readInt32();
            break;
          }
          case 16: {

            chunkNum_ = input.readInt32();
            break;
          }
          case 26: {

            video_ = input.readBytes();
            break;
          }
          default: {
            if (!parseUnknownFieldProto3(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.kikkar.packet.ResponseVideoMessageOuterClass.internal_static_com_kikkar_packet_ResponseVideoMessage_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.kikkar.packet.ResponseVideoMessageOuterClass.internal_static_com_kikkar_packet_ResponseVideoMessage_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.kikkar.packet.ResponseVideoMessage.class, com.kikkar.packet.ResponseVideoMessage.Builder.class);
  }

  public static final int VIDEONUM_FIELD_NUMBER = 1;
  private int videoNum_;
  /**
   * <code>int32 videoNum = 1;</code>
   */
  public int getVideoNum() {
    return videoNum_;
  }

  public static final int CHUNKNUM_FIELD_NUMBER = 2;
  private int chunkNum_;
  /**
   * <code>int32 chunkNum = 2;</code>
   */
  public int getChunkNum() {
    return chunkNum_;
  }

  public static final int VIDEO_FIELD_NUMBER = 3;
  private com.google.protobuf.ByteString video_;
  /**
   * <code>bytes video = 3;</code>
   */
  public com.google.protobuf.ByteString getVideo() {
    return video_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (videoNum_ != 0) {
      output.writeInt32(1, videoNum_);
    }
    if (chunkNum_ != 0) {
      output.writeInt32(2, chunkNum_);
    }
    if (!video_.isEmpty()) {
      output.writeBytes(3, video_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (videoNum_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, videoNum_);
    }
    if (chunkNum_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, chunkNum_);
    }
    if (!video_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(3, video_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.kikkar.packet.ResponseVideoMessage)) {
      return super.equals(obj);
    }
    com.kikkar.packet.ResponseVideoMessage other = (com.kikkar.packet.ResponseVideoMessage) obj;

    boolean result = true;
    result = result && (getVideoNum()
        == other.getVideoNum());
    result = result && (getChunkNum()
        == other.getChunkNum());
    result = result && getVideo()
        .equals(other.getVideo());
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + VIDEONUM_FIELD_NUMBER;
    hash = (53 * hash) + getVideoNum();
    hash = (37 * hash) + CHUNKNUM_FIELD_NUMBER;
    hash = (53 * hash) + getChunkNum();
    hash = (37 * hash) + VIDEO_FIELD_NUMBER;
    hash = (53 * hash) + getVideo().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.kikkar.packet.ResponseVideoMessage parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.ResponseVideoMessage parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.kikkar.packet.ResponseVideoMessage prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code com.kikkar.packet.ResponseVideoMessage}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.kikkar.packet.ResponseVideoMessage)
      com.kikkar.packet.ResponseVideoMessageOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.kikkar.packet.ResponseVideoMessageOuterClass.internal_static_com_kikkar_packet_ResponseVideoMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.kikkar.packet.ResponseVideoMessageOuterClass.internal_static_com_kikkar_packet_ResponseVideoMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.kikkar.packet.ResponseVideoMessage.class, com.kikkar.packet.ResponseVideoMessage.Builder.class);
    }

    // Construct using com.kikkar.packet.ResponseVideoMessage.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      videoNum_ = 0;

      chunkNum_ = 0;

      video_ = com.google.protobuf.ByteString.EMPTY;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.kikkar.packet.ResponseVideoMessageOuterClass.internal_static_com_kikkar_packet_ResponseVideoMessage_descriptor;
    }

    @java.lang.Override
    public com.kikkar.packet.ResponseVideoMessage getDefaultInstanceForType() {
      return com.kikkar.packet.ResponseVideoMessage.getDefaultInstance();
    }

    @java.lang.Override
    public com.kikkar.packet.ResponseVideoMessage build() {
      com.kikkar.packet.ResponseVideoMessage result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.kikkar.packet.ResponseVideoMessage buildPartial() {
      com.kikkar.packet.ResponseVideoMessage result = new com.kikkar.packet.ResponseVideoMessage(this);
      result.videoNum_ = videoNum_;
      result.chunkNum_ = chunkNum_;
      result.video_ = video_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return (Builder) super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.kikkar.packet.ResponseVideoMessage) {
        return mergeFrom((com.kikkar.packet.ResponseVideoMessage)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.kikkar.packet.ResponseVideoMessage other) {
      if (other == com.kikkar.packet.ResponseVideoMessage.getDefaultInstance()) return this;
      if (other.getVideoNum() != 0) {
        setVideoNum(other.getVideoNum());
      }
      if (other.getChunkNum() != 0) {
        setChunkNum(other.getChunkNum());
      }
      if (other.getVideo() != com.google.protobuf.ByteString.EMPTY) {
        setVideo(other.getVideo());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.kikkar.packet.ResponseVideoMessage parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.kikkar.packet.ResponseVideoMessage) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int videoNum_ ;
    /**
     * <code>int32 videoNum = 1;</code>
     */
    public int getVideoNum() {
      return videoNum_;
    }
    /**
     * <code>int32 videoNum = 1;</code>
     */
    public Builder setVideoNum(int value) {
      
      videoNum_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 videoNum = 1;</code>
     */
    public Builder clearVideoNum() {
      
      videoNum_ = 0;
      onChanged();
      return this;
    }

    private int chunkNum_ ;
    /**
     * <code>int32 chunkNum = 2;</code>
     */
    public int getChunkNum() {
      return chunkNum_;
    }
    /**
     * <code>int32 chunkNum = 2;</code>
     */
    public Builder setChunkNum(int value) {
      
      chunkNum_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 chunkNum = 2;</code>
     */
    public Builder clearChunkNum() {
      
      chunkNum_ = 0;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString video_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes video = 3;</code>
     */
    public com.google.protobuf.ByteString getVideo() {
      return video_;
    }
    /**
     * <code>bytes video = 3;</code>
     */
    public Builder setVideo(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      video_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes video = 3;</code>
     */
    public Builder clearVideo() {
      
      video_ = getDefaultInstance().getVideo();
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFieldsProto3(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:com.kikkar.packet.ResponseVideoMessage)
  }

  // @@protoc_insertion_point(class_scope:com.kikkar.packet.ResponseVideoMessage)
  private static final com.kikkar.packet.ResponseVideoMessage DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.kikkar.packet.ResponseVideoMessage();
  }

  public static com.kikkar.packet.ResponseVideoMessage getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ResponseVideoMessage>
      PARSER = new com.google.protobuf.AbstractParser<ResponseVideoMessage>() {
    @java.lang.Override
    public ResponseVideoMessage parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ResponseVideoMessage(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ResponseVideoMessage> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ResponseVideoMessage> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.kikkar.packet.ResponseVideoMessage getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

