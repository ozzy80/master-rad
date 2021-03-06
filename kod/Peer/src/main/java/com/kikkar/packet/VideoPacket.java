// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: VideoPacket.proto

package com.kikkar.packet;

/**
 * Protobuf type {@code com.kikkar.packet.VideoPacket}
 */
public  final class VideoPacket extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.kikkar.packet.VideoPacket)
    VideoPacketOrBuilder {
private static final long serialVersionUID = 0L;
  // Use VideoPacket.newBuilder() to construct.
  private VideoPacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private VideoPacket() {
    videoNum_ = 0;
    chunkNum_ = 0;
    firstFrame_ = false;
    video_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private VideoPacket(
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
          case 24: {

            firstFrame_ = input.readBool();
            break;
          }
          case 34: {

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
    return com.kikkar.packet.VideoPacketOuterClass.internal_static_com_kikkar_packet_VideoPacket_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.kikkar.packet.VideoPacketOuterClass.internal_static_com_kikkar_packet_VideoPacket_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.kikkar.packet.VideoPacket.class, com.kikkar.packet.VideoPacket.Builder.class);
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

  public static final int FIRSTFRAME_FIELD_NUMBER = 3;
  private boolean firstFrame_;
  /**
   * <code>bool firstFrame = 3;</code>
   */
  public boolean getFirstFrame() {
    return firstFrame_;
  }

  public static final int VIDEO_FIELD_NUMBER = 4;
  private com.google.protobuf.ByteString video_;
  /**
   * <code>bytes video = 4;</code>
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
    if (firstFrame_ != false) {
      output.writeBool(3, firstFrame_);
    }
    if (!video_.isEmpty()) {
      output.writeBytes(4, video_);
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
    if (firstFrame_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(3, firstFrame_);
    }
    if (!video_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(4, video_);
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
    if (!(obj instanceof com.kikkar.packet.VideoPacket)) {
      return super.equals(obj);
    }
    com.kikkar.packet.VideoPacket other = (com.kikkar.packet.VideoPacket) obj;

    boolean result = true;
    result = result && (getVideoNum()
        == other.getVideoNum());
    result = result && (getChunkNum()
        == other.getChunkNum());
    result = result && (getFirstFrame()
        == other.getFirstFrame());
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
    hash = (37 * hash) + FIRSTFRAME_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getFirstFrame());
    hash = (37 * hash) + VIDEO_FIELD_NUMBER;
    hash = (53 * hash) + getVideo().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.kikkar.packet.VideoPacket parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.VideoPacket parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.VideoPacket parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.VideoPacket parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.VideoPacket parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.VideoPacket parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.VideoPacket parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.VideoPacket parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.kikkar.packet.VideoPacket parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.VideoPacket parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.kikkar.packet.VideoPacket parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.VideoPacket parseFrom(
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
  public static Builder newBuilder(com.kikkar.packet.VideoPacket prototype) {
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
   * Protobuf type {@code com.kikkar.packet.VideoPacket}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.kikkar.packet.VideoPacket)
      com.kikkar.packet.VideoPacketOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.kikkar.packet.VideoPacketOuterClass.internal_static_com_kikkar_packet_VideoPacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.kikkar.packet.VideoPacketOuterClass.internal_static_com_kikkar_packet_VideoPacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.kikkar.packet.VideoPacket.class, com.kikkar.packet.VideoPacket.Builder.class);
    }

    // Construct using com.kikkar.packet.VideoPacket.newBuilder()
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

      firstFrame_ = false;

      video_ = com.google.protobuf.ByteString.EMPTY;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.kikkar.packet.VideoPacketOuterClass.internal_static_com_kikkar_packet_VideoPacket_descriptor;
    }

    @java.lang.Override
    public com.kikkar.packet.VideoPacket getDefaultInstanceForType() {
      return com.kikkar.packet.VideoPacket.getDefaultInstance();
    }

    @java.lang.Override
    public com.kikkar.packet.VideoPacket build() {
      com.kikkar.packet.VideoPacket result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.kikkar.packet.VideoPacket buildPartial() {
      com.kikkar.packet.VideoPacket result = new com.kikkar.packet.VideoPacket(this);
      result.videoNum_ = videoNum_;
      result.chunkNum_ = chunkNum_;
      result.firstFrame_ = firstFrame_;
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
      if (other instanceof com.kikkar.packet.VideoPacket) {
        return mergeFrom((com.kikkar.packet.VideoPacket)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.kikkar.packet.VideoPacket other) {
      if (other == com.kikkar.packet.VideoPacket.getDefaultInstance()) return this;
      if (other.getVideoNum() != 0) {
        setVideoNum(other.getVideoNum());
      }
      if (other.getChunkNum() != 0) {
        setChunkNum(other.getChunkNum());
      }
      if (other.getFirstFrame() != false) {
        setFirstFrame(other.getFirstFrame());
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
      com.kikkar.packet.VideoPacket parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.kikkar.packet.VideoPacket) e.getUnfinishedMessage();
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

    private boolean firstFrame_ ;
    /**
     * <code>bool firstFrame = 3;</code>
     */
    public boolean getFirstFrame() {
      return firstFrame_;
    }
    /**
     * <code>bool firstFrame = 3;</code>
     */
    public Builder setFirstFrame(boolean value) {
      
      firstFrame_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bool firstFrame = 3;</code>
     */
    public Builder clearFirstFrame() {
      
      firstFrame_ = false;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString video_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes video = 4;</code>
     */
    public com.google.protobuf.ByteString getVideo() {
      return video_;
    }
    /**
     * <code>bytes video = 4;</code>
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
     * <code>bytes video = 4;</code>
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


    // @@protoc_insertion_point(builder_scope:com.kikkar.packet.VideoPacket)
  }

  // @@protoc_insertion_point(class_scope:com.kikkar.packet.VideoPacket)
  private static final com.kikkar.packet.VideoPacket DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.kikkar.packet.VideoPacket();
  }

  public static com.kikkar.packet.VideoPacket getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<VideoPacket>
      PARSER = new com.google.protobuf.AbstractParser<VideoPacket>() {
    @java.lang.Override
    public VideoPacket parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new VideoPacket(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<VideoPacket> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<VideoPacket> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.kikkar.packet.VideoPacket getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

