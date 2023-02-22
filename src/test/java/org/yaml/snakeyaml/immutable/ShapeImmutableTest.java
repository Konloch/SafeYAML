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
package org.yaml.snakeyaml.immutable;

import junit.framework.TestCase;
import org.yaml.snakeyaml.Util;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.YamlCreator;

public class ShapeImmutableTest extends TestCase {

  public void testColor() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    Color loaded = yaml.load("!!org.yaml.snakeyaml.immutable.Color BLACK");
    assertEquals("BLACK", loaded.getName());
  }

  public void testCode() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    Code loaded = yaml.load("!!org.yaml.snakeyaml.immutable.Code 123");
    assertEquals(Integer.valueOf(123), loaded.getCode());
  }

  public void testSuperColor() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    SuperColor superColor = yaml.load(
        "!!org.yaml.snakeyaml.immutable.SuperColor [!!org.yaml.snakeyaml.immutable.Color BLACK]");
    assertEquals("BLACK", superColor.getColor().getName());
  }

  public void testSuperColorFail() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    try {
      yaml.load("!!org.yaml.snakeyaml.immutable.SuperColor BLACK");
      fail("SuperColor requires Color and not a String.");
    } catch (Exception e) {
      assertTrue(e.getMessage().startsWith(
          "Can't construct a java object for tag:yaml.org,2002:org.yaml.snakeyaml.immutable.SuperColor; exception=Unsupported class: class org.yaml.snakeyaml.immutable.Color"));
    }
  }

  public void testCode2() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    Code2 code2 = yaml.load("!!org.yaml.snakeyaml.immutable.Code2 555");
    assertEquals(Integer.valueOf(555), code2.getCode());
  }

  public void testCode3() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    try {
      yaml.load("!!org.yaml.snakeyaml.immutable.Code3 777");
      fail("There must be 1 constructor with 1 argument for scalar.");
    } catch (Exception e) {
      assertTrue(e.getMessage().startsWith(
          "Can't construct a java object for tag:yaml.org,2002:org.yaml.snakeyaml.immutable.Code3; exception=No single argument constructor found for class org.yaml.snakeyaml.immutable.Code3"));
    }
  }

  public void testCode4() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    try {
      yaml.load("!!org.yaml.snakeyaml.immutable.Code4 777");
      fail("Constructor with String is required.");
    } catch (Exception e) {
      assertEquals(
          "Can't construct a java object for tag:yaml.org,2002:org.yaml.snakeyaml.immutable.Code4; exception=Can't construct a java object for scalar tag:yaml.org,2002:org.yaml.snakeyaml.immutable.Code4; No String constructor found. Exception=org.yaml.snakeyaml.immutable.Code4.<init>(java.lang.String)\n"
              + " in 'string', line 1, column 1:\n" + "    !!org.yaml.snakeyaml.immutable.C ... \n"
              + "    ^\n",
          e.getMessage());
    }
  }

  public void testPoint() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    Point loaded = yaml.load("!!org.yaml.snakeyaml.immutable.Point [1.17, 3.14]");
    assertEquals(1.17, loaded.getX());
    assertEquals(3.14, loaded.getY());
  }

  public void testPointBlock() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    Point loaded = yaml.load("!!org.yaml.snakeyaml.immutable.Point\n- 1.17\n- 3.14");
    assertEquals(1.17, loaded.getX());
    assertEquals(3.14, loaded.getY());
  }

  public void testPointOnlyOneArgument() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    try {
      yaml.load("!!org.yaml.snakeyaml.immutable.Point\n- 1.17");
      fail("Two arguments required.");
    } catch (Exception e) {
      assertEquals(
          "Can't construct a java object for tag:yaml.org,2002:org.yaml.snakeyaml.immutable.Point; exception=No suitable constructor with 1 arguments found for class org.yaml.snakeyaml.immutable.Point\n"
              + " in 'string', line 1, column 1:\n" + "    !!org.yaml.snakeyaml.immutable.Point\n"
              + "    ^\n",
          e.getMessage());
    }
  }

  public void testPoint2() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    Point2 loaded = yaml.load("!!org.yaml.snakeyaml.immutable.Point2\n- 1\n- 3");
    assertEquals(Integer.valueOf(1), loaded.getX());
    assertEquals(Integer.valueOf(3), loaded.getY());
  }

  public void testPoint3d() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    Point3d loaded = yaml.load(
        "!!org.yaml.snakeyaml.immutable.Point3d [!!org.yaml.snakeyaml.immutable.Point [1.17, 3.14], 345.1]");
    assertEquals(345.1, loaded.getZ());
  }

  public void testShape() {
    Yaml yaml = YamlCreator.allowClassPrefix("org.yaml.snakeyaml");
    String source = Util.getLocalResource("immutable/shape1.yaml");
    Shape loaded = yaml.load(source);
    assertEquals(Integer.valueOf(123), loaded.getId());
  }

  public void testShapeNoTags() {
    String source = Util.getLocalResource("immutable/shapeNoTags.yaml");
    Yaml beanLoader = new Yaml();
    Shape loaded = beanLoader.loadAs(source, Shape.class);
    assertEquals(Integer.valueOf(123), loaded.getId());
    assertEquals("BLACK", loaded.getColor().getName());
    assertEquals(1.17, loaded.getPoint().getX());
    assertEquals(3.14, loaded.getPoint().getY());
    assertEquals(345.1, loaded.getPoint3d().getZ());
    assertEquals(1.96, loaded.getPoint3d().getPoint().getX());
    assertEquals(1.78, loaded.getPoint3d().getPoint().getY());
  }
}
