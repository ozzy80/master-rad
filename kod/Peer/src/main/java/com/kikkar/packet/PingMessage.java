// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: PingMessage.proto

package com.kikkar.packet;

/**
 * Protobuf type {@code com.kikkar.packet.PingMessage}
 */
public  final class PingMessage extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.kikkar.packet.PingMessage)
    PingMessageOrBuilder {
private static final long serialVersionUID = 0L;
  // Use PingMessage.newBuilder() to construct.
  private PingMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private PingMessage() {
    pingId_ = 0;
    clubNumber_ = 0;
    connectionType_ = 0;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private PingMessage(
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

            pingId_ = input.readInt32();
            break;
          }
          case 16: {

            clubNumber_ = input.readInt32();
            break;
          }
          case 24: {
            int rawValue = input.readEnum();

            connectionType_ = rawValue;
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
    return com.kikkar.packet.PingMessageOuterClass.internal_static_com_kikkar_packet_PingMessage_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.kikkar.packet.PingMessageOuterClass.internal_static_com_kikkar_packet_PingMessage_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.kikkar.packet.PingMessage.class, com.kikkar.packet.PingMessage.Builder.class);
  }

  public static final int PINGID_FIELD_NUMBER = 1;
  private int pingId_;
  /**
   * <code>int32 pingId = 1;</code>
   */
  public int getPingId() {
    return pingId_;
  }

  public static final int CLUBNUMBER_FIELD_NUMBER = 2;
  private int clubNumber_;
  /**
   * <code>int32 clubNumber = 2;</code>
   */
  public int getClubNumber() {
    return clubNumber_;
  }

  public static final int CONNECTIONTYPE_FIELD_NUMBER = 3;
  private int connectionType_;
  /**
   * <code>.com.kikkar.packet.ConnectionType connectionType = 3;</code>
   */
  public int getConnectionTypeValue() {
    return connectionType_;
  }
  /**
   * <code>.com.kikkar.packet.ConnectionType connectionType = 3;</code>
   */
  public com.kikkar.packet.ConnectionType getConnectionType() {
    @SuppressWarnings("deprecation")
    com.kikkar.packet.ConnectionType result = com.kikkar.packet.ConnectionType.valueOf(connectionType_);
    return result == null ? com.kikkar.packet.ConnectionType.UNRECOGNIZED : result;
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
    if (pingId_ != 0) {
      output.writeInt32(1, pingId_);
    }
    if (clubNumber_ != 0) {
      output.writeInt32(2, clubNumber_);
    }
    if (connectionType_ != com.kikkar.packet.ConnectionType.UPLOAD.getNumber()) {
      output.writeEnum(3, connectionType_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (pingId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, pingId_);
    }
    if (clubNumber_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, clubNumber_);
    }
    if (connectionType_ != com.kikkar.packet.ConnectionType.UPLOAD.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(3, connectionType_);
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
    if (!(obj instanceof com.kikkar.packet.PingMessage)) {
      return super.equals(obj);
    }
    com.kikkar.packet.PingMessage other = (com.kikkar.packet.PingMessage) obj;

    boolean result = true;
    result = result && (getPingId()
        == other.getPingId());
    result = result && (getClubNumber()
        == other.getClubNumber());
    result = result && connectionType_ == other.connectionType_;
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
    hash = (37 * hash) + PINGID_FIELD_NUMBER;
    hash = (53 * hash) + getPingId();
    hash = (37 * hash) + CLUBNUMBER_FIELD_NUMBER;
    hash = (53 * hash) + getClubNumber();
    hash = (37 * hash) + CONNECTIONTYPE_FIELD_NUMBER;
    hash = (53 * hash) + connectionType_;
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.kikkar.packet.PingMessage parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.PingMessage parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.PingMessage parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.PingMessage parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.PingMessage parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.kikkar.packet.PingMessage parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.kikkar.packet.PingMessage parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.PingMessage parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.kikkar.packet.PingMessage parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.PingMessage parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.kikkar.packet.PingMessage parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.kikkar.packet.PingMessage parseFrom(
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
  public static Builder newBuilder(com.kikkar.packet.PingMessage prototype) {
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
   * Protobuf type {@code com.kikkar.packet.PingMessage}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.kikkar.packet.PingMessage)
      com.kikkar.packet.PingMessageOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.kikkar.packet.PingMessageOuterClass.internal_static_com_kikkar_packet_PingMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.kikkar.packet.PingMessageOuterClass.internal_static_com_kikkar_packet_PingMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.kikkar.packet.PingMessage.class, com.kikkar.packet.PingMessage.Builder.class);
    }

    // Construct using com.kikkar.packet.PingMessage.newBuilder()
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
      pingId_ = 0;

      clubNumber_ = 0;

      connectionType_ = 0;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.kikkar.packet.PingMessageOuterClass.internal_static_com_kikkar_packet_PingMessage_descriptor;
    }

    @java.lang.Override
    public com.kikkar.packet.PingMessage getDefaultInstanceForType() {
      return com.kikkar.packet.PingMessage.getDefaultInstance();
    }

    @java.lang.Override
    public com.kikkar.packet.PingMessage build() {
      com.kikkar.packet.PingMessage result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.kikkar.packet.PingMessage buildPartial() {
      com.kikkar.packet.PingMessage result = new com.kikkar.packet.PingMessage(this);
      result.pingId_ = pingId_;
      result.clubNumber_ = clubNumber_;
      result.connectionType_ = connectionType_;
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
      if (other instanceof com.kikkar.packet.PingMessage) {
        return mergeFrom((com.kikkar.packet.PingMessage)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.kikkar.packet.PingMessage other) {
      if (other == com.kikkar.packet.PingMessage.getDefaultInstance()) return this;
      if (other.getPingId() != 0) {
        setPingId(other.getPingId());
      }
      if (other.getClubNumber() != 0) {
        setClubNumber(other.getClubNumber());
      }
      if (other.connectionType_ != 0) {
        setConnectionTypeValue(other.getConnectionTypeValue());
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
      com.kikkar.packet.PingMessage parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.kikkar.packet.PingMessage) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int pingId_ ;
    /**
     * <code>int32 pingId = 1;</code>
     */
    public int getPingId() {
      return pingId_;
    }
    /**
     * <code>int32 pingId = 1;</code>
     */
    public Builder setPingId(int value) {
      
      pingId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 pingId = 1;</code>
     */
    public Builder clearPingId() {
      
      pingId_ = 0;
      onChanged();
      return this;
    }

    private int clubNumber_ ;
    /**
     * <code>int32 clubNumber = 2;</code>
     */
    public int getClubNumber() {
      return clubNumber_;
    }
    /**
     * <code>int32 clubNumber = 2;</code>
     */
    public Builder setClubNumber(int value) {
      
      clubNumber_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 clubNumber = 2;</code>
     */
    public Builder clearClubNumber() {
      
      clubNumber_ = 0;
      onChanged();
      return this;
    }

    private int connectionType_ = 0;
    /**
     * <code>.com.kikkar.packet.ConnectionType connectionType = 3;</code>
     */
    public int getConnectionTypeValue() {
      return connectionType_;
    }
    /**
     * <code>.com.kikkar.packet.ConnectionType connectionType = 3;</code>
     */
    public Builder setConnectionTypeValue(int value) {
      connectionType_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>.com.kikkar.packet.ConnectionType connectionType = 3;</code>
     */
    public com.kikkar.packet.ConnectionType getConnectionType() {
      @SuppressWarnings("deprecation")
      com.kikkar.packet.ConnectionType result = com.kikkar.packet.ConnectionType.valueOf(connectionType_);
      return result == null ? com.kikkar.packet.ConnectionType.UNRECOGNIZED : result;
    }
    /**
     * <code>.com.kikkar.packet.ConnectionType connectionType = 3;</code>
     */
    public Builder setConnectionType(com.kikkar.packet.ConnectionType value) {
      if (value == null) {
        throw new NullPointerException();
      }
      
      connectionType_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>.com.kikkar.packet.ConnectionType connectionType = 3;</code>
     */
    public Builder clearConnectionType() {
      
      connectionType_ = 0;
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


    // @@protoc_insertion_point(builder_scope:com.kikkar.packet.PingMessage)
  }

  // @@protoc_insertion_point(class_scope:com.kikkar.packet.PingMessage)
  private static final com.kikkar.packet.PingMessage DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.kikkar.packet.PingMessage();
  }

  public static com.kikkar.packet.PingMessage getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<PingMessage>
      PARSER = new com.google.protobuf.AbstractParser<PingMessage>() {
    @java.lang.Override
    public PingMessage parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new PingMessage(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<PingMessage> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<PingMessage> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.kikkar.packet.PingMessage getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

