# SafeYAML
Drop in replacement for Snake YAML 1.33, this is a fork of the latest changes. The default constructors have been changed to no longer allow remote execution during deserialization.

For more information read - https://bitbucket.org/snakeyaml/snakeyaml/issues/561/cve-2022-1471-vulnerability-in

You probably don't need this dependency if you're not familiar with the issue, feel free to just ignore it.

This library is unlikely to get updated beyond importing changes from upstream, so if you have an idea (not security related) please just forward it upstream.

## How To Add As Library
Add it as a maven dependency or just [download the latest release](https://github.com/Konloch/SafeYAML/releases).
```xml
<dependency>
  <groupId>com.konloch</groupId>
  <artifactId>SafeYAML</artifactId>
  <version>1.34.1</version>
</dependency>
```

## Disclaimer
+ All tests have been moved to the [test branch](https://github.com/Konloch/SafeYAML/tree/tests)
+ This library has been built and is released for Java-8, if you require Java-7 please let me know and I can look into that.
+ Slightly different versioning numbers, we use `1.34.0` and upstream uses `1.34`
