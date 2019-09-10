# elasticsearch-on-openshift
A demo setup of Elasticsearch (ES) on Openshift 

After struggling for quite some time getting ES to run smoothly on Openshift here is a working setup.

It consist of three parts: 
* an example Openshift configuration
* a small Java program indexing generated random data 
* an Angular web frontend with a search-as-you-type input field. 

## Reasons for our troubles in the past:

* Openshift 3.9 instead of now Openshift 3.11
* NSF4 Storage instead of now OpenStack Cinder
* Using a Minishift Image (oops) ignoring memory settings and using only 512 MB 
* Using supervisord to run a NewRelic service parallel prevents seeing ES restarts caused by OOM kills
* Incorrect interpretation of Openshifts resource setting ‘requests’ and ‘limit’

## Openshift Config Files

### service.yaml
Use headless mode:
```
clusterIP: none  
```

### statefulset.yaml
It is quite important to use a statefulset.

The value of
```
discovery.zen.ping.unicast.hosts
```
is a list of elasticsearch node names. TODO: is there a better way?

Without setting the 
```
cluster.initial_master_nodes
```
value the eligible master nodes do not choose any elected master node. TODO: is this correct? Till now nowhere elsee found any hint on this.

We use a template
```
volumeClaimTemplates
```
specifiying hwo to create dynamically new volumes. The `storageClassName` is specific according to our openshift setup.

```
ES_JAVA_OPTS
```
should be always below 32GB. First best guess is to have 1/16th of the storage size. Add shards if you reach the 32 GB limit.


```
containers.resources
```
The `request` must be above the `ES_JAVA_OPTS`. Elasticsearch needs for caching a sufficient amount of unused RAM. 
To achieve this on Openshift use a high value for `containers.resources.request` (TODO: verify).

Example for a generated ticket:
```json
{
  "pointOfSale": "47",
  "time": "1970-01-01T07:49:16.923Z",
  "productInfo": {
    "id": "136",
    "from": "Hamburg",
    "to": "Milano"
  },
  "utilizationTime": {
    "from": "2019-09-19",
    "to": "2019-09-26"
  },
  "travelerInfo": {
    "lastname": "Plate",
    "firstname": "Abdu",
    "dateOfBirth": "1993-01-25"
  },
  "price": {
    "value": "09.4",
    "currency": "CHF"
  }
}
``` 


