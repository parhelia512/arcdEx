// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: engine/ddf/src/ddf/ddf_extensions.proto

package com.dynamo.proto;

public final class DdfExtensions {
  private DdfExtensions() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
    registry.add(com.dynamo.proto.DdfExtensions.alias);
    registry.add(com.dynamo.proto.DdfExtensions.structAlign);
    registry.add(com.dynamo.proto.DdfExtensions.resource);
    registry.add(com.dynamo.proto.DdfExtensions.fieldAlign);
    registry.add(com.dynamo.proto.DdfExtensions.displayName);
    registry.add(com.dynamo.proto.DdfExtensions.ddfNamespace);
    registry.add(com.dynamo.proto.DdfExtensions.ddfIncludes);
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public static final int ALIAS_FIELD_NUMBER = 50000;
  /**
   * <code>extend .google.protobuf.MessageOptions { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessage.GeneratedExtension<
      com.google.protobuf.DescriptorProtos.MessageOptions,
      java.lang.String> alias = com.google.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.String.class,
        null);
  public static final int STRUCT_ALIGN_FIELD_NUMBER = 50003;
  /**
   * <code>extend .google.protobuf.MessageOptions { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessage.GeneratedExtension<
      com.google.protobuf.DescriptorProtos.MessageOptions,
      java.lang.Boolean> structAlign = com.google.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.Boolean.class,
        null);
  public static final int RESOURCE_FIELD_NUMBER = 50100;
  /**
   * <code>extend .google.protobuf.FieldOptions { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessage.GeneratedExtension<
      com.google.protobuf.DescriptorProtos.FieldOptions,
      java.lang.Boolean> resource = com.google.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.Boolean.class,
        null);
  public static final int FIELD_ALIGN_FIELD_NUMBER = 50004;
  /**
   * <code>extend .google.protobuf.FieldOptions { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessage.GeneratedExtension<
      com.google.protobuf.DescriptorProtos.FieldOptions,
      java.lang.Boolean> fieldAlign = com.google.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.Boolean.class,
        null);
  public static final int DISPLAYNAME_FIELD_NUMBER = 50200;
  /**
   * <code>extend .google.protobuf.EnumValueOptions { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessage.GeneratedExtension<
      com.google.protobuf.DescriptorProtos.EnumValueOptions,
      java.lang.String> displayName = com.google.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.String.class,
        null);
  public static final int DDF_NAMESPACE_FIELD_NUMBER = 50001;
  /**
   * <code>extend .google.protobuf.FileOptions { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessage.GeneratedExtension<
      com.google.protobuf.DescriptorProtos.FileOptions,
      java.lang.String> ddfNamespace = com.google.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.String.class,
        null);
  public static final int DDF_INCLUDES_FIELD_NUMBER = 50002;
  /**
   * <code>extend .google.protobuf.FileOptions { ... }</code>
   */
  public static final
    com.google.protobuf.GeneratedMessage.GeneratedExtension<
      com.google.protobuf.DescriptorProtos.FileOptions,
      java.lang.String> ddfIncludes = com.google.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.String.class,
        null);

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\'engine/ddf/src/ddf/ddf_extensions.prot" +
      "o\032 google/protobuf/descriptor.proto:0\n\005a" +
      "lias\022\037.google.protobuf.MessageOptions\030\320\206" +
      "\003 \001(\t:7\n\014struct_align\022\037.google.protobuf." +
      "MessageOptions\030\323\206\003 \001(\010:1\n\010resource\022\035.goo" +
      "gle.protobuf.FieldOptions\030\264\207\003 \001(\010:4\n\013fie" +
      "ld_align\022\035.google.protobuf.FieldOptions\030" +
      "\324\206\003 \001(\010:8\n\013displayName\022!.google.protobuf" +
      ".EnumValueOptions\030\230\210\003 \001(\t:5\n\rddf_namespa" +
      "ce\022\034.google.protobuf.FileOptions\030\321\206\003 \001(\t" +
      ":4\n\014ddf_includes\022\034.google.protobuf.FileO" +
      "ptions\030\322\206\003 \001(\tB!\n\020com.dynamo.protoB\rDdfE" +
      "xtensions"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.DescriptorProtos.getDescriptor(),
        });
    alias.internalInit(descriptor.getExtensions().get(0));
    structAlign.internalInit(descriptor.getExtensions().get(1));
    resource.internalInit(descriptor.getExtensions().get(2));
    fieldAlign.internalInit(descriptor.getExtensions().get(3));
    displayName.internalInit(descriptor.getExtensions().get(4));
    ddfNamespace.internalInit(descriptor.getExtensions().get(5));
    ddfIncludes.internalInit(descriptor.getExtensions().get(6));
    com.google.protobuf.DescriptorProtos.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
