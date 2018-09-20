// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ControlMessage.proto

package com.kikkar.packet;

/**
 * Protobuf type {@code com.kikkar.packet.ControlMessage}
 */
public  final class ControlMessage extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.kikkar.packet.ControlMessage)
    ControlMessageOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ControlMessage.newBuilder() to construct.
  private ControlMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ControlMessage() {
    messageId_ = 0;
    currentDisplayedVideoNum_ = 0;
    timeInMilliseconds_ = 0L;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ControlMessage(
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

            messageId_ = input.readInt32();
            break;
          }
          case 16: {

            currentDisplayedVideoNum_ = input.readInt32();
            break;
          }
          case 24: {

            timeInMilliseconds_ = input.readInt64();
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
    return com.kikkar.packet.ControlMessageOuterClass.internal_static_com_kikkar_packet_ControlMessage_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.kikkar.packet.ControlMessageOuterClass.internal_static_com_kikkar_packet_ControlMessage_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.kikkar.packet.ControlMessage.class, com.kikkar.packet.ControlMessage.Builder.class);
  }

  public static final int MESSAGEID_FIELD_NUMBER = 1;
  private int messageId_;
  /**
   * <code>int32 messageId = 1;</code>
   */
  public int getMessageId() {
    return messageId_;
  }

  public static final int CURRENTDISPLAYEDVIDEONUM_FIELD_NUMBER = 2;
  private int currentDisplayedVideoNum_;
  /**
   * <code>int32 currentDisplayedVideoNum = 2;</code>
   */
  public int getCurrentDisplayedVideoNum() {
    return currentDisplayedVideoNum_;
  }

  public static final int TIMEINMILLISECONDS_FIELD_NUMBER = 3;
  private long timeInMilliseconds_;
  /**
   * <code>int64 timeInMilliseconds = 3;</code>
   */
  public long getTimeInMilliseconds() {
    return timeInMilliseconds_;
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
    if (messageId_ != 0) {
      output.writeInt32(1, messageId_);
    }
    if (currentDisplayedVideoNum_ != 0) {
      output.writeInt32(2, currentDisplayedVideoNum_);
    }
    if (timeInMilliseconds_ != 0L) {
      output.writeInt64(3, timeInMilliseconds_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (messageId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, messageId_);
    }
    if (currentDisplayedVideoNum_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, currentDisplayedVideoNum_);
    }
    if (timeInMilliseconds_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(3, timeInMilliseconds_);
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
    if (!(obj instanceof com.kikkar.packet.ControlMessage)) {
      return super.equals(obj);
    }
    com.kikkar.packet.ControlMessage other = (com.kikkar.packet.ControlMessage) obj;

    boolean result = true;
    result = result && (getMessageId()
        == other.getMessageId());
    result = result && (getCurrentDisplayedVideoNum()
        == other.getCurrentDisplayedVideoNum());
    result = result && (getTimeInMilliseconds()
        == other.getTimeInMilliseconds());
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
    hash = (37 * hash) + MESSAGEID_FIELD_NUMBER;
    hash = (53 * hash) + getMessageId();
    hash = (37 * hash) + CURRENTDISPLAYEDVIDEONUM_FIELD_NUMBER;
    hash = (53 * hash) + getCurrentDisplayedVideoNum();
    hash = (37 * hash) + TIMEINMILLISECONDS_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getTimeInMilliseconds());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.kikkar.packet.ControlMessage parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.ControlMessage parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.ControlMessage parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.ControlMessage parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.ControlMessage parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.ControlMessage parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.ControlMessage parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.ControlMessage parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.kikkar.packet.ControlMessage parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.ControlMessage parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.kikkar.packet.ControlMessage parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.ControlMessage parseFrom(
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
  public static Builder newBuilder(com.kikkar.packet.ControlMessage prototype) {
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
   * Protobuf type {@code com.kikkar.packet.ControlMessage}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.kikkar.packet.ControlMessage)
      com.kikkar.packet.ControlMessageOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.kikkar.packet.ControlMessageOuterClass.internal_static_com_kikkar_packet_ControlMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.kikkar.packet.ControlMessageOuterClass.internal_static_com_kikkar_packet_ControlMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.kikkar.packet.ControlMessage.class, com.kikkar.packet.ControlMessage.Builder.class);
    }

    // Construct using com.kikkar.packet.ControlMessage.newBuilder()
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
      messageId_ = 0;

      currentDisplayedVideoNum_ = 0;

      timeInMilliseconds_ = 0L;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.kikkar.packet.ControlMessageOuterClass.internal_static_com_kikkar_packet_ControlMessage_descriptor;
    }

    @java.lang.Override
    public com.kikkar.packet.ControlMessage getDefaultInstanceForType() {
      return com.kikkar.packet.ControlMessage.getDefaultInstance();
    }

    @java.lang.Override
    public com.kikkar.packet.ControlMessage build() {
      com.kikkar.packet.ControlMessage result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.kikkar.packet.ControlMessage buildPartial() {
      com.kikkar.packet.ControlMessage result = new com.kikkar.packet.ControlMessage(this);
      result.messageId_ = messageId_;
      result.currentDisplayedVideoNum_ = currentDisplayedVideoNum_;
      result.timeInMilliseconds_ = timeInMilliseconds_;
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
      if (other instanceof com.kikkar.packet.ControlMessage) {
        return mergeFrom((com.kikkar.packet.ControlMessage)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.kikkar.packet.ControlMessage other) {
      if (other == com.kikkar.packet.ControlMessage.getDefaultInstance()) return this;
      if (other.getMessageId() != 0) {
        setMessageId(other.getMessageId());
      }
      if (other.getCurrentDisplayedVideoNum() != 0) {
        setCurrentDisplayedVideoNum(other.getCurrentDisplayedVideoNum());
      }
      if (other.getTimeInMilliseconds() != 0L) {
        setTimeInMilliseconds(other.getTimeInMilliseconds());
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
      com.kikkar.packet.ControlMessage parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.kikkar.packet.ControlMessage) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int messageId_ ;
    /**
     * <code>int32 messageId = 1;</code>
     */
    public int getMessageId() {
      return messageId_;
    }
    /**
     * <code>int32 messageId = 1;</code>
     */
    public Builder setMessageId(int value) {
      
      messageId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 messageId = 1;</code>
     */
    public Builder clearMessageId() {
      
      messageId_ = 0;
      onChanged();
      return this;
    }

    private int currentDisplayedVideoNum_ ;
    /**
     * <code>int32 currentDisplayedVideoNum = 2;</code>
     */
    public int getCurrentDisplayedVideoNum() {
      return currentDisplayedVideoNum_;
    }
    /**
     * <code>int32 currentDisplayedVideoNum = 2;</code>
     */
    public Builder setCurrentDisplayedVideoNum(int value) {
      
      currentDisplayedVideoNum_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 currentDisplayedVideoNum = 2;</code>
     */
    public Builder clearCurrentDisplayedVideoNum() {
      
      currentDisplayedVideoNum_ = 0;
      onChanged();
      return this;
    }

    private long timeInMilliseconds_ ;
    /**
     * <code>int64 timeInMilliseconds = 3;</code>
     */
    public long getTimeInMilliseconds() {
      return timeInMilliseconds_;
    }
    /**
     * <code>int64 timeInMilliseconds = 3;</code>
     */
    public Builder setTimeInMilliseconds(long value) {
      
      timeInMilliseconds_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 timeInMilliseconds = 3;</code>
     */
    public Builder clearTimeInMilliseconds() {
      
      timeInMilliseconds_ = 0L;
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


    // @@protoc_insertion_point(builder_scope:com.kikkar.packet.ControlMessage)
  }

  // @@protoc_insertion_point(class_scope:com.kikkar.packet.ControlMessage)
  private static final com.kikkar.packet.ControlMessage DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.kikkar.packet.ControlMessage();
  }

  public static com.kikkar.packet.ControlMessage getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ControlMessage>
      PARSER = new com.google.protobuf.AbstractParser<ControlMessage>() {
    @java.lang.Override
    public ControlMessage parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ControlMessage(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ControlMessage> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ControlMessage> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.kikkar.packet.ControlMessage getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
