<div align="center">  
 <img src="icons/synergy_logo_no_background.png" width="300px"/>  
</div>

# Synergy Framework API
<a href="https://www.jusjus.me/synergy">
 <img src="https://img.shields.io/static/v1?label=Need%20more%20information&message=See%20Synergy%20website&color=%3CCOLOR%3E&style=for-the-badge" alt="synergy website">
</a>
<a href="https://www.jusjus.me/synergy">
 <img src="https://img.shields.io/static/v1?label=Need%20support?&message=Join%20our%20discord&color=informational&style=for-the-badge&logo=discord" alt="synergy website">
</a>
<br><br>
A framework API written for the Synergy Framework. Synergy API is a standalone API 
used for creating things like commands, events, database connections, user handling, 
threading, dependency management, file providers and more. Making your life easier while coding a plugin for either any Spigot fork or any proxy.

## Enable the API in your project
```java
// Get the API utilities with this instance and initialize some modules.
new SynergyAPIBuilder<>(this /*JavaPlugin*/)
  .userProvider(/*UserHandler<A, ? extends SynergyUser<? extends Player> */)
  .buildAPI();
```

## Documentation

## Usefull links

###MultiMap
https://github.com/google/guava/wiki/NewCollectionTypesExplained#multimap <br>
`Map<K, List<V>>` implementation that allows to store multiple values for a single key.

###Docs