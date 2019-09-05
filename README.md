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
* Incorrect interpretation of Openshifts' resources setting ‘requests’ and ‘limit’
