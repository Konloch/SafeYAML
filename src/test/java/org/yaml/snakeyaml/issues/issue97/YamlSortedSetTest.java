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
package org.yaml.snakeyaml.issues.issue97;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.YamlCreator;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class YamlSortedSetTest {

  @Test
  public void testYaml() {
    String serialized =
        "!!org.yaml.snakeyaml.issues.issue97.Blog\n" + "posts:\n" + "  - text: Dummy\n"
            + "    title: Test\n" + "  - text: Creative\n" + "    title: Highly\n";
    // System.out.println(serialized);
    Yaml yaml2 = constructYamlParser();
    Blog rehydrated = yaml2.load(serialized);
    checkTestBlog(rehydrated);
  }

  @Test
  public void testYaml2() {
    String serialized =
        "!!org.yaml.snakeyaml.issues.issue97.Blog\n" + "posts:\n" + "  - text: Dummy\n"
            + "    title: Test\n" + "  - text: Creative\n" + "    title: Highly\n";
    // System.out.println(serialized);
    Yaml yaml2 = constructYamlParser2();
    Blog rehydrated = yaml2.load(serialized);
    checkTestBlog(rehydrated);
  }

  public void testYaml3() {
    String serialized =
        "!!org.yaml.snakeyaml.issues.issue97.Blog\n" + "posts:\n" + "  - text: Dummy\n"
            + "    title: Test\n" + "  - text: Creative\n" + "    title: Highly\n";
    // System.out.println(serialized);
    Yaml yaml3 = constructYamlParser3();
    Blog rehydrated = yaml3.loadAs(serialized, Blog.class);
    checkTestBlog(rehydrated);
  }

  public void testYamlDefault() {
    String serialized =
        "!!org.yaml.snakeyaml.issues.issue97.Blog\n" + "posts:\n" + "  - text: Dummy\n"
            + "    title: Test\n" + "  - text: Creative\n" + "    title: Highly\n";
    // System.out.println(serialized);
    Yaml yaml = new Yaml();
    yaml.setBeanAccess(BeanAccess.FIELD);
    Blog rehydrated = yaml.loadAs(serialized, Blog.class);
    checkTestBlog(rehydrated);
  }

  protected Yaml constructYamlParser2() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    yaml.addTypeDescription(new TypeDescription(SortedSet.class) {
      @Override
      public Object newInstance(Node node) {
        return new TreeSet<Object>();
      }
    });
    yaml.setBeanAccess(BeanAccess.FIELD);
    return yaml;
  }

  protected Yaml constructYamlParser3() {
    Yaml yaml = new Yaml();
    yaml.setBeanAccess(BeanAccess.FIELD);
    yaml.addTypeDescription(new TypeDescription(SortedSet.class, TreeSet.class));
    return yaml;
  }

  protected Yaml constructYamlParser() {
    Yaml yaml = new Yaml(new SetContructor());
    yaml.setBeanAccess(BeanAccess.FIELD);
    return yaml;
  }

  protected void checkTestBlog(Blog blog) {
    Set<Post> posts = blog.getPosts();
    Assert.assertTrue("posts should be SortedSet", (posts instanceof SortedSet));
    Assert.assertEquals("Blog contains 2 posts", 2, posts.size());
  }

  private class SetContructor extends Constructor {

    public SetContructor() {
      super(YamlCreator.trustPrefixLoaderOptions("org.yaml.snakeyaml"));
      yamlClassConstructors.put(NodeId.sequence, new ConstructSetFromSequence());
    }

    private class ConstructSetFromSequence extends ConstructSequence {

      @Override
      public Object construct(Node node) {
        if (SortedSet.class.isAssignableFrom(node.getType())) {
          if (node.isTwoStepsConstruction()) {
            throw new YAMLException("Set cannot be recursive.");
          } else {
            Collection<Object> result = new TreeSet<Object>();
            SetContructor.this.constructSequenceStep2((SequenceNode) node, result);
            return result;
          }
        } else {
          return super.construct(node);
        }
      }
    }
  }
}
