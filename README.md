## Infinispan Cluster Tests

Create N nodes cluster of infinispan nodes:

`./mvnw spring-boot:run -Djgroups.tcp.port=7801 -Dnode.cacheOps=write,delete -Dnode.name=node1`

`./mvnw spring-boot:run -Djgroups.tcp.port=7802 -Dnode.cacheOps=write,delete -Dnode.name=node2`

`./mvnw spring-boot:run -Djgroups.tcp.port=7803 -Dnode.cacheOps=write,delete -Dnode.name=node3`


The three nodes read, write (-Dnode.cacheOps=write) and delete (-Dnode.cacheOps=delete) the same entry in the cache.
