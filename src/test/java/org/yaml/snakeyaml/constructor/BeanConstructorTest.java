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
package org.yaml.snakeyaml.constructor;

import java.math.BigInteger;
import junit.framework.TestCase;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Util;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.YamlCreator;
import org.yaml.snakeyaml.error.YAMLException;

public class BeanConstructorTest extends TestCase {

  public void testPrimitivesConstructor() {
    Yaml yaml = new Yaml(new Constructor(TestBean1.class, new LoaderOptions()));
    String document = Util.getLocalResource("constructor/test-primitives1.yaml");
    TestBean1 result = yaml.load(document);
    assertNotNull(result);
    assertEquals(Byte.valueOf((byte) 1), result.getByteClass());
    assertEquals((byte) -3, result.getBytePrimitive());
    assertEquals(Short.valueOf((short) 0), result.getShortClass());
    assertEquals((short) -13, result.getShortPrimitive());
    assertEquals(Integer.valueOf(5), result.getInteger());
    assertEquals(17, result.getIntPrimitive());
    assertEquals("the text", result.getText());
    assertEquals("13", result.getId());
    assertEquals(Long.valueOf(11111111111L), result.getLongClass());
    assertEquals(9999999999L, result.getLongPrimitive());
    assertEquals(Boolean.TRUE, result.getBooleanClass());
    assertTrue(result.isBooleanPrimitive());
    assertEquals(Character.valueOf('2'), result.getCharClass());
    assertEquals('#', result.getCharPrimitive());
    assertEquals(new BigInteger("1234567890123456789012345678901234567890"),
        result.getBigInteger());
    assertEquals(Float.valueOf(2), result.getFloatClass());
    assertEquals(Float.valueOf(3.1416f), result.getFloatPrimitive());
    assertEquals(Double.valueOf(4), result.getDoubleClass());
    assertEquals(Double.valueOf(11200), result.getDoublePrimitive());
    assertEquals(1199836800000L, result.getDate().getTime());
    assertEquals("public", result.publicField);
    //
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    Yaml yamlToDump = new Yaml(options);
    String output = yamlToDump.dump(result);
    TestBean1 result2 = YamlCreator.allowClassPrefix("org.yaml.snakeyaml").load(output);
    assertNotNull(result2);
    TestBean1 result3 = YamlCreator.allowClassPrefix("org.yaml.snakeyaml").load(output);
    assertNotNull(result3);
  }

  public void testNoClassConstructor() {
    try {
      new Yaml(new Constructor((Class<? extends Object>) null, new LoaderOptions()));
      fail("Class must be provided.");
    } catch (NullPointerException e) {
      assertEquals("Root class must be provided.", e.getMessage());
    }
  }

  public void testNoClassConstructorString() throws ClassNotFoundException {
    try {
      new Yaml(new Constructor((String) null, new LoaderOptions()));
      fail("Class must be provided.");
    } catch (NullPointerException e) {
      assertEquals("Root type must be provided.", e.getMessage());
    }
  }

  public void testNoClassConstructorEmptyString() throws ClassNotFoundException {
    try {
      new Yaml(new Constructor(" ", new LoaderOptions()));
      fail("Class must be provided.");
    } catch (YAMLException e) {
      assertEquals("Root type must be provided.", e.getMessage());
    }
  }

  public void testCharacter() {
    Yaml yaml = new Yaml(new Constructor(TestBean1.class, new LoaderOptions()));
    String document = "charClass: id";
    try {
      yaml.load(document);
      fail("Only one char must be allowed.");
    } catch (Exception e) {
      assertTrue(e.getMessage(),
          e.getMessage().contains("Invalid node Character: 'id'; length: 2"));
    }
    document = "charClass: #";
    TestBean1 bean = yaml.load(document);
    assertNull("Null must be accepted.", bean.getCharClass());
    document = "charClass: ''";
    bean = yaml.load(document);
    assertNull("Null must be accepted.", bean.getCharClass());
    document = "charClass:\n";
    bean = yaml.load(document);
    assertNull("Null must be accepted.", bean.getCharClass());
    document = "charClass: 1\n";
    bean = yaml.load(document);
    assertEquals(Character.valueOf('1'), bean.getCharClass());
  }

  public void testNoEmptyConstructor() {
    Yaml yaml = new Yaml(new Constructor(TestBean2.class, new LoaderOptions()));
    String document = "text: qwerty";
    try {
      yaml.load(document);
      fail("No empty constructor available");
    } catch (Exception e) {
      assertTrue(e.getMessage(), e.getMessage().contains("NoSuchMethodException"));
    }
    TestBean2 bean = new TestBean2();
    assertEquals("", bean.getText());
  }

  private class TestBean2 {

    private String text;

    public TestBean2() {
      setText("");
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }
  }

  public void testPrivateMethod() {
    Yaml yaml = new Yaml(new Constructor(TestBean2.class, new LoaderOptions()));
    String document = "text: qwerty";
    try {
      yaml.load(document);
      fail("Private method cannot be called.");
    } catch (Exception e) {
      assertTrue(e.getMessage(), e.getMessage().contains("NoSuchMethodException"));
    }
  }

  public void testKeyNotScalar() {
    Yaml yaml = new Yaml(new Constructor(TestBean1.class, new LoaderOptions()));
    String document = "[1, 2]: qwerty";
    try {
      yaml.load(document);
      fail("Keys must be scalars.");
    } catch (Exception e) {
      assertTrue(e.getMessage(), e.getMessage().contains("Keys must be scalars but found"));
    }
  }

  public void testInvalidKey() {
    Yaml yaml = new Yaml(new Constructor(TestBean1.class, new LoaderOptions()));
    String document = "something: qwerty";
    try {
      yaml.load(document);
      fail("Non-existing property must fail.");
    } catch (Exception e) {
      assertTrue(e.getMessage(), e.getMessage().contains("Unable to find property 'something'"));
    }
  }

  public void testStaticField() {
    Yaml yaml = new Yaml(new Constructor(TestBean1.class, new LoaderOptions()));
    String document = "staticInteger: 123";
    try {
      yaml.load(document);
      fail("Staic variables must not be used.");
    } catch (Exception e) {
      assertTrue(e.getMessage(),
          e.getMessage().contains("Unable to find property 'staticInteger'"));
    }
  }

  public void testScalarContructor() {
    Yaml yaml = new Yaml(new Constructor(Parent1.class, new LoaderOptions()));
    String document = "id: 123\nchild: 25";
    Parent1 parent = yaml.load(document);
    assertEquals("123", parent.getId());
    Child1 child = parent.getChild();
    assertEquals(Integer.valueOf(25), child.getCode());
  }

  public void testScalarContructorException() {
    Yaml yaml = new Yaml(new Constructor(ExceptionParent.class, new LoaderOptions()));
    String document = "id: 123\nchild: 25";
    try {
      yaml.load(document);
      fail("ExceptionParent should not be created.");
    } catch (Exception e) {
      assertTrue(e.getMessage(), e.getMessage()
          .contains("Can't construct a java object for scalar tag:yaml.org,2002:int"));
    }
  }

  static public class ExceptionParent {

    private String id;
    private ExceptionChild child;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public ExceptionChild getChild() {
      return child;
    }

    public void setChild(ExceptionChild child) {
      this.child = child;
    }

  }

  public static class ExceptionChild {

    private final Integer code;

    public ExceptionChild(Integer code) {
      throw new RuntimeException("ExceptionChild cannot be created.");
    }

    public Integer getCode() {
      return code;
    }
  }
}
