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
package org.yaml.snakeyaml.issues.issue351;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class MergedOrderTest {

  @SuppressWarnings("rawtypes")
  @Test
  public void mergedLinkedMapOrder() throws IOException {
    Yaml yaml = new Yaml();
    InputStream inputStream = MergedOrderTest.class.getResourceAsStream("/issues/issue351_1.yaml");
    Map<?, ?> bean = yaml.loadAs(inputStream, Map.class);

    Object first = bean.get("prize_cashBack_fixture");
    Object second = bean.get("prize_cashBack_sendEmail_fixture");

    assertThat(first, instanceOf(Map.class));
    assertThat(second, instanceOf(Map.class));

    assertArrayEquals(((Map) first).entrySet().toArray(), ((Map) second).entrySet().toArray());

    inputStream.close();
  }
}
