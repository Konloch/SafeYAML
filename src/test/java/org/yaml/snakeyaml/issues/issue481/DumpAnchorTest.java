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
package org.yaml.snakeyaml.issues.issue481;

import java.io.StringReader;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Util;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.serializer.AnchorGenerator;

public class DumpAnchorTest extends TestCase {

  public void test_anchor_test() {
    String str = Util.getLocalResource("issues/issue481.yaml");
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    options.setAnchorGenerator(new AnchorGenerator() {
      @Override
      public String nextAnchor(Node node) {
        return node.getAnchor();
      }
    });
    Yaml yaml = new Yaml(options);

    Node node = yaml.compose(new StringReader(str));
    StringWriter out = new StringWriter();
    yaml.serialize(node, out);
    assertEquals(str, out.toString());
  }
}
