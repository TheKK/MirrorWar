// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: charger.proto

package netGameNodeSDK.charger;

public final class Charger {
  private Charger() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ChargerStateOrBuilder extends
      // @@protoc_insertion_point(interface_extends:netGameNodeSDK.charger.ChargerState)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required uint32 id = 1;</code>
     */
    boolean hasId();
    /**
     * <code>required uint32 id = 1;</code>
     */
    int getId();

    /**
     * <code>required double x = 2;</code>
     */
    boolean hasX();
    /**
     * <code>required double x = 2;</code>
     */
    double getX();

    /**
     * <code>required double y = 3;</code>
     */
    boolean hasY();
    /**
     * <code>required double y = 3;</code>
     */
    double getY();

    /**
     * <code>required .netGameNodeSDK.charger.ChargerState.Animation animation = 4;</code>
     */
    boolean hasAnimation();
    /**
     * <code>required .netGameNodeSDK.charger.ChargerState.Animation animation = 4;</code>
     */
    netGameNodeSDK.charger.Charger.ChargerState.Animation getAnimation();
  }
  /**
   * Protobuf type {@code netGameNodeSDK.charger.ChargerState}
   */
  public  static final class ChargerState extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:netGameNodeSDK.charger.ChargerState)
      ChargerStateOrBuilder {
    // Use ChargerState.newBuilder() to construct.
    private ChargerState(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ChargerState() {
      id_ = 0;
      x_ = 0D;
      y_ = 0D;
      animation_ = 1;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private ChargerState(
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
              bitField0_ |= 0x00000001;
              id_ = input.readUInt32();
              break;
            }
            case 17: {
              bitField0_ |= 0x00000002;
              x_ = input.readDouble();
              break;
            }
            case 25: {
              bitField0_ |= 0x00000004;
              y_ = input.readDouble();
              break;
            }
            case 32: {
              int rawValue = input.readEnum();
              netGameNodeSDK.charger.Charger.ChargerState.Animation value = netGameNodeSDK.charger.Charger.ChargerState.Animation.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(4, rawValue);
              } else {
                bitField0_ |= 0x00000008;
                animation_ = rawValue;
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
      return netGameNodeSDK.charger.Charger.internal_static_netGameNodeSDK_charger_ChargerState_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return netGameNodeSDK.charger.Charger.internal_static_netGameNodeSDK_charger_ChargerState_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              netGameNodeSDK.charger.Charger.ChargerState.class, netGameNodeSDK.charger.Charger.ChargerState.Builder.class);
    }

    /**
     * Protobuf enum {@code netGameNodeSDK.charger.ChargerState.Animation}
     */
    public enum Animation
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>IS_CHARGED = 1;</code>
       */
      IS_CHARGED(1),
      /**
       * <code>NORMAL = 2;</code>
       */
      NORMAL(2),
      ;

      /**
       * <code>IS_CHARGED = 1;</code>
       */
      public static final int IS_CHARGED_VALUE = 1;
      /**
       * <code>NORMAL = 2;</code>
       */
      public static final int NORMAL_VALUE = 2;


      public final int getNumber() {
        return value;
      }

      /**
       * @deprecated Use {@link #forNumber(int)} instead.
       */
      @java.lang.Deprecated
      public static Animation valueOf(int value) {
        return forNumber(value);
      }

      public static Animation forNumber(int value) {
        switch (value) {
          case 1: return IS_CHARGED;
          case 2: return NORMAL;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<Animation>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static final com.google.protobuf.Internal.EnumLiteMap<
          Animation> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<Animation>() {
              public Animation findValueByNumber(int number) {
                return Animation.forNumber(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(ordinal());
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return netGameNodeSDK.charger.Charger.ChargerState.getDescriptor().getEnumTypes().get(0);
      }

      private static final Animation[] VALUES = values();

      public static Animation valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int value;

      private Animation(int value) {
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:netGameNodeSDK.charger.ChargerState.Animation)
    }

    private int bitField0_;
    public static final int ID_FIELD_NUMBER = 1;
    private int id_;
    /**
     * <code>required uint32 id = 1;</code>
     */
    public boolean hasId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required uint32 id = 1;</code>
     */
    public int getId() {
      return id_;
    }

    public static final int X_FIELD_NUMBER = 2;
    private double x_;
    /**
     * <code>required double x = 2;</code>
     */
    public boolean hasX() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required double x = 2;</code>
     */
    public double getX() {
      return x_;
    }

    public static final int Y_FIELD_NUMBER = 3;
    private double y_;
    /**
     * <code>required double y = 3;</code>
     */
    public boolean hasY() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required double y = 3;</code>
     */
    public double getY() {
      return y_;
    }

    public static final int ANIMATION_FIELD_NUMBER = 4;
    private int animation_;
    /**
     * <code>required .netGameNodeSDK.charger.ChargerState.Animation animation = 4;</code>
     */
    public boolean hasAnimation() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>required .netGameNodeSDK.charger.ChargerState.Animation animation = 4;</code>
     */
    public netGameNodeSDK.charger.Charger.ChargerState.Animation getAnimation() {
      netGameNodeSDK.charger.Charger.ChargerState.Animation result = netGameNodeSDK.charger.Charger.ChargerState.Animation.valueOf(animation_);
      return result == null ? netGameNodeSDK.charger.Charger.ChargerState.Animation.IS_CHARGED : result;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasX()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasY()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasAnimation()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeUInt32(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeDouble(2, x_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeDouble(3, y_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeEnum(4, animation_);
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(2, x_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(3, y_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(4, animation_);
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
      if (!(obj instanceof netGameNodeSDK.charger.Charger.ChargerState)) {
        return super.equals(obj);
      }
      netGameNodeSDK.charger.Charger.ChargerState other = (netGameNodeSDK.charger.Charger.ChargerState) obj;

      boolean result = true;
      result = result && (hasId() == other.hasId());
      if (hasId()) {
        result = result && (getId()
            == other.getId());
      }
      result = result && (hasX() == other.hasX());
      if (hasX()) {
        result = result && (
            java.lang.Double.doubleToLongBits(getX())
            == java.lang.Double.doubleToLongBits(
                other.getX()));
      }
      result = result && (hasY() == other.hasY());
      if (hasY()) {
        result = result && (
            java.lang.Double.doubleToLongBits(getY())
            == java.lang.Double.doubleToLongBits(
                other.getY()));
      }
      result = result && (hasAnimation() == other.hasAnimation());
      if (hasAnimation()) {
        result = result && animation_ == other.animation_;
      }
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
      if (hasId()) {
        hash = (37 * hash) + ID_FIELD_NUMBER;
        hash = (53 * hash) + getId();
      }
      if (hasX()) {
        hash = (37 * hash) + X_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            java.lang.Double.doubleToLongBits(getX()));
      }
      if (hasY()) {
        hash = (37 * hash) + Y_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            java.lang.Double.doubleToLongBits(getY()));
      }
      if (hasAnimation()) {
        hash = (37 * hash) + ANIMATION_FIELD_NUMBER;
        hash = (53 * hash) + animation_;
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static netGameNodeSDK.charger.Charger.ChargerState parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static netGameNodeSDK.charger.Charger.ChargerState parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static netGameNodeSDK.charger.Charger.ChargerState parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static netGameNodeSDK.charger.Charger.ChargerState parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static netGameNodeSDK.charger.Charger.ChargerState parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static netGameNodeSDK.charger.Charger.ChargerState parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static netGameNodeSDK.charger.Charger.ChargerState parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static netGameNodeSDK.charger.Charger.ChargerState parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static netGameNodeSDK.charger.Charger.ChargerState parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static netGameNodeSDK.charger.Charger.ChargerState parseFrom(
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
    public static Builder newBuilder(netGameNodeSDK.charger.Charger.ChargerState prototype) {
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
     * Protobuf type {@code netGameNodeSDK.charger.ChargerState}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:netGameNodeSDK.charger.ChargerState)
        netGameNodeSDK.charger.Charger.ChargerStateOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return netGameNodeSDK.charger.Charger.internal_static_netGameNodeSDK_charger_ChargerState_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return netGameNodeSDK.charger.Charger.internal_static_netGameNodeSDK_charger_ChargerState_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                netGameNodeSDK.charger.Charger.ChargerState.class, netGameNodeSDK.charger.Charger.ChargerState.Builder.class);
      }

      // Construct using netGameNodeSDK.charger.Charger.ChargerState.newBuilder()
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
        id_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        x_ = 0D;
        bitField0_ = (bitField0_ & ~0x00000002);
        y_ = 0D;
        bitField0_ = (bitField0_ & ~0x00000004);
        animation_ = 1;
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return netGameNodeSDK.charger.Charger.internal_static_netGameNodeSDK_charger_ChargerState_descriptor;
      }

      public netGameNodeSDK.charger.Charger.ChargerState getDefaultInstanceForType() {
        return netGameNodeSDK.charger.Charger.ChargerState.getDefaultInstance();
      }

      public netGameNodeSDK.charger.Charger.ChargerState build() {
        netGameNodeSDK.charger.Charger.ChargerState result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public netGameNodeSDK.charger.Charger.ChargerState buildPartial() {
        netGameNodeSDK.charger.Charger.ChargerState result = new netGameNodeSDK.charger.Charger.ChargerState(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.x_ = x_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.y_ = y_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.animation_ = animation_;
        result.bitField0_ = to_bitField0_;
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
        if (other instanceof netGameNodeSDK.charger.Charger.ChargerState) {
          return mergeFrom((netGameNodeSDK.charger.Charger.ChargerState)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(netGameNodeSDK.charger.Charger.ChargerState other) {
        if (other == netGameNodeSDK.charger.Charger.ChargerState.getDefaultInstance()) return this;
        if (other.hasId()) {
          setId(other.getId());
        }
        if (other.hasX()) {
          setX(other.getX());
        }
        if (other.hasY()) {
          setY(other.getY());
        }
        if (other.hasAnimation()) {
          setAnimation(other.getAnimation());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        if (!hasId()) {
          return false;
        }
        if (!hasX()) {
          return false;
        }
        if (!hasY()) {
          return false;
        }
        if (!hasAnimation()) {
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        netGameNodeSDK.charger.Charger.ChargerState parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (netGameNodeSDK.charger.Charger.ChargerState) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int id_ ;
      /**
       * <code>required uint32 id = 1;</code>
       */
      public boolean hasId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required uint32 id = 1;</code>
       */
      public int getId() {
        return id_;
      }
      /**
       * <code>required uint32 id = 1;</code>
       */
      public Builder setId(int value) {
        bitField0_ |= 0x00000001;
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required uint32 id = 1;</code>
       */
      public Builder clearId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        id_ = 0;
        onChanged();
        return this;
      }

      private double x_ ;
      /**
       * <code>required double x = 2;</code>
       */
      public boolean hasX() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required double x = 2;</code>
       */
      public double getX() {
        return x_;
      }
      /**
       * <code>required double x = 2;</code>
       */
      public Builder setX(double value) {
        bitField0_ |= 0x00000002;
        x_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required double x = 2;</code>
       */
      public Builder clearX() {
        bitField0_ = (bitField0_ & ~0x00000002);
        x_ = 0D;
        onChanged();
        return this;
      }

      private double y_ ;
      /**
       * <code>required double y = 3;</code>
       */
      public boolean hasY() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required double y = 3;</code>
       */
      public double getY() {
        return y_;
      }
      /**
       * <code>required double y = 3;</code>
       */
      public Builder setY(double value) {
        bitField0_ |= 0x00000004;
        y_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required double y = 3;</code>
       */
      public Builder clearY() {
        bitField0_ = (bitField0_ & ~0x00000004);
        y_ = 0D;
        onChanged();
        return this;
      }

      private int animation_ = 1;
      /**
       * <code>required .netGameNodeSDK.charger.ChargerState.Animation animation = 4;</code>
       */
      public boolean hasAnimation() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>required .netGameNodeSDK.charger.ChargerState.Animation animation = 4;</code>
       */
      public netGameNodeSDK.charger.Charger.ChargerState.Animation getAnimation() {
        netGameNodeSDK.charger.Charger.ChargerState.Animation result = netGameNodeSDK.charger.Charger.ChargerState.Animation.valueOf(animation_);
        return result == null ? netGameNodeSDK.charger.Charger.ChargerState.Animation.IS_CHARGED : result;
      }
      /**
       * <code>required .netGameNodeSDK.charger.ChargerState.Animation animation = 4;</code>
       */
      public Builder setAnimation(netGameNodeSDK.charger.Charger.ChargerState.Animation value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000008;
        animation_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <code>required .netGameNodeSDK.charger.ChargerState.Animation animation = 4;</code>
       */
      public Builder clearAnimation() {
        bitField0_ = (bitField0_ & ~0x00000008);
        animation_ = 1;
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


      // @@protoc_insertion_point(builder_scope:netGameNodeSDK.charger.ChargerState)
    }

    // @@protoc_insertion_point(class_scope:netGameNodeSDK.charger.ChargerState)
    private static final netGameNodeSDK.charger.Charger.ChargerState DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new netGameNodeSDK.charger.Charger.ChargerState();
    }

    public static netGameNodeSDK.charger.Charger.ChargerState getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<ChargerState>
        PARSER = new com.google.protobuf.AbstractParser<ChargerState>() {
      public ChargerState parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new ChargerState(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ChargerState> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ChargerState> getParserForType() {
      return PARSER;
    }

    public netGameNodeSDK.charger.Charger.ChargerState getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_netGameNodeSDK_charger_ChargerState_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_netGameNodeSDK_charger_ChargerState_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rcharger.proto\022\026netGameNodeSDK.charger\"" +
      "\234\001\n\014ChargerState\022\n\n\002id\030\001 \002(\r\022\t\n\001x\030\002 \002(\001\022" +
      "\t\n\001y\030\003 \002(\001\022A\n\tanimation\030\004 \002(\0162..netGameN" +
      "odeSDK.charger.ChargerState.Animation\"\'\n" +
      "\tAnimation\022\016\n\nIS_CHARGED\020\001\022\n\n\006NORMAL\020\002"
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
    internal_static_netGameNodeSDK_charger_ChargerState_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_netGameNodeSDK_charger_ChargerState_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_netGameNodeSDK_charger_ChargerState_descriptor,
        new java.lang.String[] { "Id", "X", "Y", "Animation", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
