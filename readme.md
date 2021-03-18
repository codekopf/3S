### TODO ###

* Create data structure in which:
  * From which page the link come -> important information for failure
  * Word count
  * Page size
* Create config map for special cases
  * e.g. link.contains("#") - same URL but with internal anchor -> remove duplicats 
  * e.g. link.contains("gp/"))
* Check scrapping failure for IllegalArgumentException
* Use Reactive Java
* Make Lombok use LOG for log anotation 
