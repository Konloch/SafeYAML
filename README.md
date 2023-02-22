Drop in replacement for Snake YAML 1.33, the default constructors have been changed to no longer allow remote execution during deserialization.

For more information read - https://bitbucket.org/snakeyaml/snakeyaml/issues/561/cve-2022-1471-vulnerability-in

You probably don't need this dependency if you're not familiar with the issue, feel free to just ignore it.

This library is unlikely to get updated beyond importing changes from upstream, so if you have an idea (not security related) please just forward it upstream.