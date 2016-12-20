// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: gameReport.proto

package mirrorWar.gameReport;

public final class GameReport {
  private GameReport() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface StatusOrBuilder extends
      // @@protoc_insertion_point(interface_extends:mirrorWar.gameReport.Status)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated int32 playersLife = 1;</code>
     */
    java.util.List<java.lang.Integer> getPlayersLifeList();
    /**
     * <code>repeated int32 playersLife = 1;</code>
     */
    int getPlayersLifeCount();
    /**
     * <code>repeated int32 playersLife = 1;</code>
     */
    int getPlayersLife(int index);
  }
  /**
   * Protobuf type {@code mirrorWar.gameReport.Status}
   */
  public  static final class Status extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:mirrorWar.gameReport.Status)
      StatusOrBuilder {
    // Use Status.newBuilder() to construct.
    private Status(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Status() {
      playersLife_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Status(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
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
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                playersLife_ = new java.util.ArrayList<java.lang.Integer>();
                mutable_bitField0_ |= 0x00000001;
              }
              playersLife_.add(input.readInt32());
              break;
            }
            case 10: {
              int length = input.readRawVarint32();
              int limit = input.pushLimit(length);
              if (!((mutable_bitField0_ & 0x00000001) == 0x00000001) && input.getBytesUntilLimit() > 0) {
                playersLife_ = new java.util.ArrayList<java.lang.Integer>();
                mutable_bitField0_ |= 0x00000001;
              }
              while (input.getBytesUntilLimit() > 0) {
                playersLife_.add(input.readInt32());
              }
              input.popLimit(limit);
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
        if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
          playersLife_ = java.util.Collections.unmodifiableList(playersLife_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return mirrorWar.gameReport.GameReport.internal_static_mirrorWar_gameReport_Status_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return mirrorWar.gameReport.GameReport.internal_static_mirrorWar_gameReport_Status_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              mirrorWar.gameReport.GameReport.Status.class, mirrorWar.gameReport.GameReport.Status.Builder.class);
    }

    public static final int PLAYERSLIFE_FIELD_NUMBER = 1;
    private java.util.List<java.lang.Integer> playersLife_;
    /**
     * <code>repeated int32 playersLife = 1;</code>
     */
    public java.util.List<java.lang.Integer>
        getPlayersLifeList() {
      return playersLife_;
    }
    /**
     * <code>repeated int32 playersLife = 1;</code>
     */
    public int getPlayersLifeCount() {
      return playersLife_.size();
    }
    /**
     * <code>repeated int32 playersLife = 1;</code>
     */
    public int getPlayersLife(int index) {
      return playersLife_.get(index);
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      for (int i = 0; i < playersLife_.size(); i++) {
        output.writeInt32(1, playersLife_.get(i));
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      {
        int dataSize = 0;
        for (int i = 0; i < playersLife_.size(); i++) {
          dataSize += com.google.protobuf.CodedOutputStream
            .computeInt32SizeNoTag(playersLife_.get(i));
        }
        size += dataSize;
        size += 1 * getPlayersLifeList().size();
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof mirrorWar.gameReport.GameReport.Status)) {
        return super.equals(obj);
      }
      mirrorWar.gameReport.GameReport.Status other = (mirrorWar.gameReport.GameReport.Status) obj;

      boolean result = true;
      result = result && getPlayersLifeList()
          .equals(other.getPlayersLifeList());
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      if (getPlayersLifeCount() > 0) {
        hash = (37 * hash) + PLAYERSLIFE_FIELD_NUMBER;
        hash = (53 * hash) + getPlayersLifeList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static mirrorWar.gameReport.GameReport.Status parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static mirrorWar.gameReport.GameReport.Status parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static mirrorWar.gameReport.GameReport.Status parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static mirrorWar.gameReport.GameReport.Status parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static mirrorWar.gameReport.GameReport.Status parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static mirrorWar.gameReport.GameReport.Status parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static mirrorWar.gameReport.GameReport.Status parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static mirrorWar.gameReport.GameReport.Status parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static mirrorWar.gameReport.GameReport.Status parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static mirrorWar.gameReport.GameReport.Status parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(mirrorWar.gameReport.GameReport.Status prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
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
     * Protobuf type {@code mirrorWar.gameReport.Status}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:mirrorWar.gameReport.Status)
        mirrorWar.gameReport.GameReport.StatusOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return mirrorWar.gameReport.GameReport.internal_static_mirrorWar_gameReport_Status_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return mirrorWar.gameReport.GameReport.internal_static_mirrorWar_gameReport_Status_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                mirrorWar.gameReport.GameReport.Status.class, mirrorWar.gameReport.GameReport.Status.Builder.class);
      }

      // Construct using mirrorWar.gameReport.GameReport.Status.newBuilder()
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
      public Builder clear() {
        super.clear();
        playersLife_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return mirrorWar.gameReport.GameReport.internal_static_mirrorWar_gameReport_Status_descriptor;
      }

      public mirrorWar.gameReport.GameReport.Status getDefaultInstanceForType() {
        return mirrorWar.gameReport.GameReport.Status.getDefaultInstance();
      }

      public mirrorWar.gameReport.GameReport.Status build() {
        mirrorWar.gameReport.GameReport.Status result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public mirrorWar.gameReport.GameReport.Status buildPartial() {
        mirrorWar.gameReport.GameReport.Status result = new mirrorWar.gameReport.GameReport.Status(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) == 0x00000001)) {
          playersLife_ = java.util.Collections.unmodifiableList(playersLife_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.playersLife_ = playersLife_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof mirrorWar.gameReport.GameReport.Status) {
          return mergeFrom((mirrorWar.gameReport.GameReport.Status)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(mirrorWar.gameReport.GameReport.Status other) {
        if (other == mirrorWar.gameReport.GameReport.Status.getDefaultInstance()) return this;
        if (!other.playersLife_.isEmpty()) {
          if (playersLife_.isEmpty()) {
            playersLife_ = other.playersLife_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensurePlayersLifeIsMutable();
            playersLife_.addAll(other.playersLife_);
          }
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        mirrorWar.gameReport.GameReport.Status parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (mirrorWar.gameReport.GameReport.Status) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.util.List<java.lang.Integer> playersLife_ = java.util.Collections.emptyList();
      private void ensurePlayersLifeIsMutable() {
        if (!((bitField0_ & 0x00000001) == 0x00000001)) {
          playersLife_ = new java.util.ArrayList<java.lang.Integer>(playersLife_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated int32 playersLife = 1;</code>
       */
      public java.util.List<java.lang.Integer>
          getPlayersLifeList() {
        return java.util.Collections.unmodifiableList(playersLife_);
      }
      /**
       * <code>repeated int32 playersLife = 1;</code>
       */
      public int getPlayersLifeCount() {
        return playersLife_.size();
      }
      /**
       * <code>repeated int32 playersLife = 1;</code>
       */
      public int getPlayersLife(int index) {
        return playersLife_.get(index);
      }
      /**
       * <code>repeated int32 playersLife = 1;</code>
       */
      public Builder setPlayersLife(
          int index, int value) {
        ensurePlayersLifeIsMutable();
        playersLife_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated int32 playersLife = 1;</code>
       */
      public Builder addPlayersLife(int value) {
        ensurePlayersLifeIsMutable();
        playersLife_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated int32 playersLife = 1;</code>
       */
      public Builder addAllPlayersLife(
          java.lang.Iterable<? extends java.lang.Integer> values) {
        ensurePlayersLifeIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, playersLife_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated int32 playersLife = 1;</code>
       */
      public Builder clearPlayersLife() {
        playersLife_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:mirrorWar.gameReport.Status)
    }

    // @@protoc_insertion_point(class_scope:mirrorWar.gameReport.Status)
    private static final mirrorWar.gameReport.GameReport.Status DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new mirrorWar.gameReport.GameReport.Status();
    }

    public static mirrorWar.gameReport.GameReport.Status getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<Status>
        PARSER = new com.google.protobuf.AbstractParser<Status>() {
      public Status parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new Status(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Status> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Status> getParserForType() {
      return PARSER;
    }

    public mirrorWar.gameReport.GameReport.Status getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_mirrorWar_gameReport_Status_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_mirrorWar_gameReport_Status_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\020gameReport.proto\022\024mirrorWar.gameReport" +
      "\"\035\n\006Status\022\023\n\013playersLife\030\001 \003(\005"
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
    internal_static_mirrorWar_gameReport_Status_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_mirrorWar_gameReport_Status_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_mirrorWar_gameReport_Status_descriptor,
        new java.lang.String[] { "PlayersLife", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
