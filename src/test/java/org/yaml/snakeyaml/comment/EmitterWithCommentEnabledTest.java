/**
 * Copyright (c) 2008, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.yaml.snakeyaml.comment;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

public class EmitterWithCommentEnabledTest {

  private String runEmitterWithCommentsEnabled(String data) throws IOException {
    StringWriter output = new StringWriter();

    DumperOptions options = new DumperOptions();
    options.setDefaultScalarStyle(ScalarStyle.PLAIN);
    options.setDefaultFlowStyle(FlowStyle.BLOCK);
    options.setProcessComments(true);
    Serializer serializer =
        new Serializer(new Emitter(output, options), new Resolver(), options, null);

    serializer.open();
    LoaderOptions loaderOptions = new LoaderOptions().setProcessComments(true);
    Composer composer = new Composer(new ParserImpl(new StreamReader(data), loaderOptions),
        new Resolver(), loaderOptions);
    while (composer.checkNode()) {
      serializer.serialize(composer.getNode());
    }
    serializer.close();

    return output.toString();
  }

  private Emitter producePrettyFlowEmitter(StringWriter output) {
    DumperOptions options = new DumperOptions();
    options.setDefaultScalarStyle(ScalarStyle.PLAIN);
    options.setDefaultFlowStyle(FlowStyle.FLOW);
    options.setProcessComments(true);
    options.setPrettyFlow(true);
    return new Emitter(output, options);
  }

  @Test
  public void testEmpty() throws Exception {
    String data = "";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testWithOnlyComment() throws Exception {
    String data = "# Comment\n\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testCommentEndingALine() throws Exception {
    String data = "" + //
        "key: # Comment\n" + //
        "  value\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testMultiLineComment() throws Exception {
    String data = "" + //
        "key: # Comment\n" + //
        "     # lines\n" + //
        "  value\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testBlankLine() throws Exception {
    String data = "" + //
        "\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testBlankLineComments() throws Exception {
    String data = "" + //
        "\n" + //
        "abc: def # comment\n" + //
        "\n" + //
        "\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testBlockScalar() throws Exception {
    String data = "" + //
        "abc: | # Comment\n" + //
        "  def\n" + //
        "  hij\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testDirectiveLineEndComment() throws Exception {
    String data = "%YAML 1.1 #Comment\n";

    String result = runEmitterWithCommentsEnabled(data);
    // We currently strip Directive comments
    assertEquals("", result);
  }

  @Test
  public void testSequence() throws Exception {
    String data = "" + //
        "# Comment\n" + //
        "list: # InlineComment1\n" + //
        "  - # Block Comment\n" + //
        "    item # InlineComment2\n" + //
        "# Comment\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testAllComments1() throws Exception {
    String data = "" + //
        "# Block Comment1\n" + //
        "# Block Comment2\n" + //
        "key: # Inline Comment1a\n" + //
        "     # Inline Comment1b\n" + //
        "  # Block Comment3a\n" + //
        "  # Block Comment3b\n" + //
        "  value # Inline Comment2\n" + //
        "# Block Comment4\n" + //
        "list: # InlineComment3a\n" + //
        "      # InlineComment3b\n" + //
        "  - # Block Comment5\n" + //
        "    item1 # InlineComment4\n" + //
        "  - item2: [value2a, value2b] # InlineComment5\n" + //
        "  - item3: {key3a: [value3a1, value3a2], key3b: value3b} # InlineComment6\n" + //
        "# Block Comment6\n" + //
        "---\n" + //
        "# Block Comment7\n" + //
        "";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testMultiDoc() throws Exception {
    String data = "" + //
        "key: value\n" + //
        "# Block Comment\n" + //
        "---\n" + //
        "# Block Comment\n" + //
        "key: value\n" + //
        "";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testAllComments2() throws Exception {
    String data = "" + //
        "key:\n" + "  key:\n" + "    key:\n" + "    - # Block Comment1\n" + "      item1a\n"
        + "    - # Block Comment2\n" + "    - item1b\n" + "    - # Block Comment3\n"
        + "      MapKey_1: MapValue1\n" + "      MapKey_2: MapValue2\n" + "key2:\n"
        + "- # Block Comment4\n" + //
        "  # Block Comment5\n" + //
        "  item1 # Inline Comment1a\n" + //
        "        # Inline Comment1b\n" + //
        "- # Block Comment6a\n" + //
        "  # Block Comment6b\n" + //
        "  item2: value # Inline Comment2\n" + //
        "# Block Comment7\n" + //
        "";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testAllComments3() throws Exception {
    String data = "" + //
        "# Block Comment1\n" + //
        "[item1, {item2: value2}, {item3: value3}] # Inline Comment1\n" + //
        "# Block Comment2\n" + //
        "";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testKeepingNewLineInsideSequence() throws Exception {
    String data = "" + "\n" + "key:\n" +
    // " \n" + // only supported in a sequence right now
        "- item1\n" +
        // "\n" + // Per Spec this is part of plain scalar above
        "- item2\n" +
        // "\n" + // Per Spec this is part of plain scalar above
        "- item3\n" + "\n" + "key2: value2\n" + "\n" + "key3: value3\n" + "\n" + "";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testKeepingNewLineInsideSequence2() throws Exception {
    String data = "" + "apiVersion: kustomize.config.k8s.io/v1beta1\n" + "kind: Kustomization\n"
        + "\n" + "namePrefix: acquisition-gateway-\n" + "\n" + "bases:\n" +
        /**
         * Not supported right now " \n" + "#-
         * https://github.intuit.com/dev-patterns/intuit-kustomize/intuit-service-appd-noingress-base?ref=v3.1.2\n"
         * + "# Add the following base and HPA-patch.yaml, fill in correct minReplicas and
         * maxReplcias in Hpa-patch.yaml\n" + "#-
         * https://github.intuit.com/dev-patterns/intuit-kustomize//intuit-service-hpa-base?ref=v3.1.2\n"
         * +
         */
        "- https://github.intuit.com/dev-patterns/intuit-kustomize//intuit-service-canary-appd-noingress-base?ref=v3.2.0\n"
        + "- https://github.intuit.com/dev-patterns/intuit-kustomize//intuit-service-rollout-hpa-base?ref=v3.2.0\n"
        + "# resources:\n" + "# - Nginx-ConfigMap.yaml\n" + "\n" + "resources:\n"
        + "- ConfigMap-v1-splunk-sidecar-config.yaml\n" + "- CronJob-patch.yaml\n" + "\n"
        + "patchesStrategicMerge:\n" + "- app-rollout-patch.yaml\n" + "- Service-patch.yaml\n"
        + "- Service-metrics-patch.yaml\n" +
        // "\n" +
        "- Hpa-patch.yaml\n" + "#- SignalSciences-patch.yaml\n" + "\n"
        + "# Uncomment HPA-patch when you need to enable HPA\n" + "#- Hpa-patch.yaml\n"
        + "# Uncomment SignalSciences-patch when you need to enable Signal Sciences\n"
        + "#- SignalSciences-patch.yaml\n" + "";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testCommentsIndentFirstLineBlank() throws Exception {
    String data = "# Comment 1\n" + "key1:\n" + "  \n" + "  # Comment 2\n" + "  # Comment 3\n"
        + "  key2: value1\n" + "# \"Fun\" options\n" + "key3:\n" + "  # Comment 4\n"
        + "  # Comment 5\n" + "  key4: value2\n" + "key5:\n" + "  key6: value3\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testMultiLineString() throws Exception {
    String data = "# YAML load and save bug with keep block chomping indicator\n" + "example:\n"
        + "  description: |+\n" + "    These lines have a carrage return after them.\n"
        + "    And the carrage return will be duplicated with each save if the\n"
        + "    block chomping indicator + is used. (\"keep\": keep the line feed, keep trailing blank lines.)\n"
        + "\n" + "successfully-loaded: test\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void test100Comments() throws IOException {
    StringBuilder commentBuilder = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      commentBuilder.append("# Comment ").append(i).append("\n");
    }
    final String data = "" + commentBuilder + "simpleKey: simpleValue\n" + "\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(data, result);
  }

  @Test
  public void testCommentsOnReference() throws Exception {
    String data = "dummy: &a test\n" + "conf:\n" + "- # comment not ok here\n"
        + "  *a #comment not ok here\n";
    String expected = "dummy: &a test\n" + "conf:\n" + "- *a\n";

    String result = runEmitterWithCommentsEnabled(data);
    assertEquals(expected.replace("a", "id001"), result);
  }

  @Test
  public void testCommentsAtDataWindowBreak() {
    String data = getComplexConfig();

    final DumperOptions yamlOptions = new DumperOptions();
    yamlOptions.setIndent(4);
    yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

    final LoaderOptions loaderOptions = new LoaderOptions();
    final Representer yamlRepresenter = new Representer(yamlOptions);

    loaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
    final Yaml yaml =
        new Yaml(new SafeConstructor(loaderOptions), yamlRepresenter, yamlOptions, loaderOptions);

    yaml.load(data);
  }

  @Test
  public void testCommentsInFlowMapping() throws IOException {
    StringWriter output = new StringWriter();
    Emitter emitter = producePrettyFlowEmitter(output);

    emitter.emit(new StreamStartEvent(null, null));
    emitter.emit(new DocumentStartEvent(null, null, false, null, null));
    emitter
        .emit(new MappingStartEvent(null, "yaml.org,2002:map", true, null, null, FlowStyle.FLOW));
    emitter.emit(new CommentEvent(CommentType.BLOCK, " I'm first", null, null));
    ImplicitTuple allImplicit = new ImplicitTuple(true, true);
    emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, "a", null, null,
        ScalarStyle.PLAIN));
    emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, "Hello", null, null,
        ScalarStyle.PLAIN));
    emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, "b", null, null,
        ScalarStyle.PLAIN));
    emitter
        .emit(new MappingStartEvent(null, "yaml.org,2002:map", true, null, null, FlowStyle.FLOW));
    emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, "one", null, null,
        ScalarStyle.PLAIN));
    emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, "World", null, null,
        ScalarStyle.PLAIN));
    emitter.emit(new CommentEvent(CommentType.BLOCK, " also me", null, null));
    emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, "two", null, null,
        ScalarStyle.PLAIN));
    emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, "eee", null, null,
        ScalarStyle.PLAIN));
    emitter.emit(new MappingEndEvent(null, null));
    emitter.emit(new MappingEndEvent(null, null));
    emitter.emit(new DocumentEndEvent(null, null, false));
    emitter.emit(new StreamEndEvent(null, null));

    String result = output.toString();
    final String data = "{\n" + "  # I'm first\n" + "  a: Hello,\n" + "  b: {\n"
        + "    one: World,\n" + "    # also me\n" + "    two: eee\n" + "  }\n" + "}\n";

    assertEquals(data, result);
  }

  @Test
  public void testCommentInEmptyFlowMapping() throws IOException {
    StringWriter output = new StringWriter();
    Emitter emitter = producePrettyFlowEmitter(output);

    emitter.emit(new StreamStartEvent(null, null));
    emitter.emit(new DocumentStartEvent(null, null, false, null, null));
    emitter
        .emit(new MappingStartEvent(null, "yaml.org,2002:map", true, null, null, FlowStyle.FLOW));
    emitter.emit(new CommentEvent(CommentType.BLOCK, " nobody home", null, null));
    emitter.emit(new MappingEndEvent(null, null));
    emitter.emit(new DocumentEndEvent(null, null, false));
    emitter.emit(new StreamEndEvent(null, null));

    String result = output.toString();
    final String data = "{\n" + "  # nobody home\n" + "}\n";

    assertEquals(data, result);
  }

  @Test
  public void testCommentInFlowSequence() throws IOException {
    StringWriter output = new StringWriter();
    Emitter emitter = producePrettyFlowEmitter(output);
    ImplicitTuple allImplicit = new ImplicitTuple(true, true);

    emitter.emit(new StreamStartEvent(null, null));
    emitter.emit(new DocumentStartEvent(null, null, false, null, null));
    emitter
        .emit(new SequenceStartEvent(null, "yaml.org,2002:seq", true, null, null, FlowStyle.FLOW));
    emitter.emit(new CommentEvent(CommentType.BLOCK, " red", null, null));
    emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, "one", null, null,
        ScalarStyle.PLAIN));
    emitter.emit(new CommentEvent(CommentType.BLOCK, " blue", null, null));
    emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, "two", null, null,
        ScalarStyle.PLAIN));
    emitter.emit(new SequenceEndEvent(null, null));
    emitter.emit(new DocumentEndEvent(null, null, false));
    emitter.emit(new StreamEndEvent(null, null));

    String result = output.toString();
    final String data = "[\n" + "  # red\n" + "  one,\n" + "  # blue\n" + "  two\n" + "]\n";

    assertEquals(data, result);
  }

  @Test
  public void testCommentInEmptySequence() throws IOException {
    StringWriter output = new StringWriter();
    Emitter emitter = producePrettyFlowEmitter(output);

    emitter.emit(new StreamStartEvent(null, null));
    emitter.emit(new DocumentStartEvent(null, null, false, null, null));
    emitter
        .emit(new SequenceStartEvent(null, "yaml.org,2002:seq", true, null, null, FlowStyle.FLOW));
    emitter.emit(new CommentEvent(CommentType.BLOCK, " nobody home", null, null));
    emitter.emit(new SequenceEndEvent(null, null));
    emitter.emit(new DocumentEndEvent(null, null, false));
    emitter.emit(new StreamEndEvent(null, null));

    String result = output.toString();
    final String data = "[\n" + "  # nobody home\n" + "]\n";

    assertEquals(data, result);
  }

  private String getComplexConfig() {
    return "# Core configurable options for LWC\n" + "core:\n" + "\n"
        + "    # The language LWC will use, specified by the shortname. For example, English = en, French = fr, German = de,\n"
        + "    # and so on\n" + "    locale: en\n" + "\n"
        + "    # How often updates are batched to the database (in seconds). If set to a higher value than 10, you may have\n"
        + "    # some unexpected results, especially if your server is prone to crashing.\n"
        + "    flushInterval: 10\n" + "\n"
        + "    # LWC regularly caches protections locally to prevent the database from being queried as often. The default is 10000\n"
        + "    # and for most servers is OK. LWC will also fill up to <precache> when the server is started automatically.\n"
        + "    cacheSize: 10000\n" + "\n"
        + "    # How many protections are precached on startup. If set to -1, it will use the cacheSize value instead and precache\n"
        + "    # as much as possible\n" + "    precache: -1\n" + "\n"
        + "    # If true, players will be sent a notice in their chat box when they open a protection they have access to, but\n"
        + "    # not their own unless <showMyNotices> is set to true\n" + "    showNotices: true\n"
        + "\n"
        + "    # If true, players will be sent a notice in their chat box when they open a protection they own.\n"
        + "    showMyNotices: false\n";
  }

  @Test
  public void emitOnlyScalarWithComment() throws IOException {
    StringWriter output = new StringWriter();
    Emitter emitter = producePrettyFlowEmitter(output);
    emitter.emit(new StreamStartEvent(null, null));
    emitter.emit(new DocumentStartEvent(null, null, false, null, null));
    emitter.emit(new CommentEvent(CommentType.BLOCK, "Hello world!", null, null));
    emitter.emit(new ScalarEvent(null, null, new ImplicitTuple(true, true), "This is the scalar",
        null, null, ScalarStyle.DOUBLE_QUOTED));
    emitter.emit(new DocumentEndEvent(null, null, false));
    emitter.emit(new StreamEndEvent(null, null));

    final String data = "#Hello world!\n" + "\"This is the scalar\"\n";

    assertEquals(data, output.toString());
  }

  @Test
  public void emitOnlyComment() throws IOException {
    StringWriter output = new StringWriter();
    Emitter emitter = producePrettyFlowEmitter(output);
    emitter.emit(new StreamStartEvent(null, null));
    emitter.emit(new DocumentStartEvent(null, null, false, null, null));
    emitter.emit(new CommentEvent(CommentType.BLOCK, "Hello world!", null, null));
    emitter.emit(new DocumentEndEvent(null, null, false));
    emitter.emit(new StreamEndEvent(null, null));

    assertEquals("#Hello world!\n", output.toString());
  }
}
