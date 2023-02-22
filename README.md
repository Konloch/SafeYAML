# SafeYAML
Drop in replacement for Snake YAML 1.33, this is a fork of the latest changes. The default constructors have been changed to no longer allow remote execution during deserialization.

For more information read - https://bitbucket.org/snakeyaml/snakeyaml/issues/561/cve-2022-1471-vulnerability-in

You probably don't need this dependency if you're not familiar with the issue, feel free to just ignore it.

This library is unlikely to get updated beyond importing changes from upstream, so if you have an idea (not security related) please just forward it upstream.

## How To Add As Library - SnakeYAML 1.33 with the safe changes
***Recommended** - If you're using SnakeYAML as a transitive dependency this is the one you want*
Add it as a maven dependency or just [download the latest release](https://github.com/Konloch/SafeYAML/releases).
```xml
<dependency>
  <groupId>com.konloch</groupId>
  <artifactId>safeyaml</artifactId>
  <version>1.33.0</version>
</dependency>
```

## How To Add As Library - SnakeYAML 1.34/2.0-INDEV with the safe changes
***Not Recommended** - I recommend using `1.33.0` as this version drops compatability for older features and may cause API breaks if used as a transitive dependency.*
Add it as a maven dependency or just [download the latest release](https://github.com/Konloch/SafeYAML/releases).
```xml
<dependency>
  <groupId>com.konloch</groupId>
  <artifactId>safeyaml</artifactId>
  <version>1.34.0</version>
</dependency>
```

## Disclaimer
+ The namespace `org.yaml.snakeyaml` has been maintained along with all of the existing names, none of the API has been changed to maintain 1:1 compatability between libraries.
+ All tests have been moved to the [test branch](https://github.com/Konloch/SafeYAML/tree/tests)
+ This library has been built and is released for Java-8, if you require Java-7 please let me know and I can look into that.
+ Slightly different versioning numbers, we use `1.34.0` and upstream uses `1.34`
