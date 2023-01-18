[//]: # (https://img.shields.io/static/v1?label=Need%20support?&message=Join%20our%20discord&color=informational&style=for-the-badge&logo=discord)

<div align="center">  
 <img src="icons/synergy_logo_no_background.png" width="300px"/>  
</div>

# Synergy Framework API
<a href="https://www.jusjus.me/synergy" target="_blank">
 <img src="https://img.shields.io/static/v1?label=Need%20more%20information&message=See%20Synergy%20website&color=%3CCOLOR%3E&style=for-the-badge" alt="synergy website">
</a>
<a href="https://discord.gg/n7xb57G7Ur" target="_blank">
 <img src="https://img.shields.io/static/v1?label=Need%20support?&message=Join%20our%20discord&color=informational&style=for-the-badge&logo=discord" alt="synergy website">
</a>
<br><br>
A framework API written for the Synergy Framework. Synergy API is a standalone API 
used for creating things like commands, events, database connections, user handling, 
threading, dependency management, file providers and more. Making your life easier while coding a plugin for either any Spigot fork or any proxy.

## Adding as a Dependency
Synergy API builds are published to the [Open Collaboration repository]().
Follow the below steps to add Synergy as a dependency to your project. It can alsone be used as a standalone API rather than shading it in your project. See [Use as standalone](#use-as-standalone) for more information.
### Maven

```xml
<repositories>
    <repository>
        <id>opencollab</id>
        <url>https://repo.opencollab.dev/maven-releases/</url>
    </repository>
</repositories>

<dependency>
    <groupId>net.synergy.jusjus</groupId>
    <artifactId>synergyutilities</artifactId>
    <version>(version here)</version>
</dependency>
```

### Gradle

```groovy
repositories {
    maven { url 'https://repo.opencollab.dev/maven-releases/' }
}

dependencies {
    implementation 'net.synergy.jusjus:synergyutilities:[version]'
}
```

## Use as API or Standalone plugin
Synergy API can be used as a standalone plugin or as an API. If you want to use it as a standalone plugin, you can download the latest version from the releases page. 
If you want to use it as an API, you can add it as a dependency to your project.

Rather than shading it in your project, you have to download the latest version from the releases page and put it in your plugins folder.
[Download it here]()

## Enable the API in your project
```java
// Get the API utilities with this instance and initialize some modules.
new SynergyAPIBuilder<>(this /*JavaPlugin*/)
  .userProvider(/*UserHandler<A, ? extends SynergyUser<? extends Player> */)
  .buildAPI();
```

## Documentation

## Third Party Libraries Used and Shaded
- [Lombok](https://projectlombok.org/)
- [GeyserMC MCProtocolLib](https://github.com/GeyserMC/MCProtocolLib) (Stress testing with fake clients)
- [HikariCP](https://github.com/brettwooldridge/HikariCP) (For SQL pooling)
- [Adventure](https://docs.adventure.kyori.net/getting-started.html) (Version controlled text & packet components)
- [BStats](https://bstats.org/) (Plugin Metrics)

[//]: # (https://github.com/lewysDavies/Java-Probability-Collection)
[//]: # (https://github.com/MrIvanPlays/AnnotationConfig)

### Other Libraries
- [Google Guava](https://github.com/google/guava)
- [Google Gson]()
- [Google Zxing]()
- [Apache Commons]()

[//]: # (-     compile group: 'com.googlecode.json-simple', name: 'json-simple', version: '1.1')
[//]: # (  compile group: 'com.google.inject', name: 'guice', version: '4.0' // INJECTION)
[//]: # (  compile group: 'com.google.code.gson', name: 'gson', version: '2.3.1' // GSON)
[//]: # (  compile group: 'com.google.zxing', name: 'core', version: '3.2.1')
[//]: # (  compile group: 'com.google.guava', name: 'guava', version: 'r05' // Guava)
[//]: # (  compile group: 'com.github.enerccio', name: 'gson-utilities', version: '1.1.0' // GSON Utility)
[//]: # (implementation 'org.bstats:bstats-bukkit:3.0.0')

## Internal & Third party methods

### MultiMap
https://github.com/google/guava/wiki/NewCollectionTypesExplained#multimap <br>
`Map<K, List<V>>` implementation that allows to store multiple values for a single key.

## Support and Development
Visit our discord server: [Synergy Discord](https://discord.gg/n7xb57G7Ur) for help and support regarding this project.