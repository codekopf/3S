### Dictionary ###

* **domain** - Domain name registered in DNS
* **websiteURL** - URL on which domain can be accessed in browser URL field.
* **root** - Starting websiteURL from which crawling starts. Usually it is **websiteURL**.
* **page** - Accessing URL I get HTML page (in request response body). Page is everything inside HTML code. 
* **parent** - If page has a link on another page, the page which is linking is parent towards the linked page. The linked page is **child**.
* **child** - It is a page linked and accessed from its parent.
* **level** - Number of hops from parent to child to get from root to certain **page**. More info in graph theory and distance from **root** to graph node.  

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
* Add redirect check
* How to handle link on image
  * e.g. https://codepills.com/wp-content/uploads/2016/03/osx-xampp-wordpress-ftp-install-error-directory-settings.png
  * Another exception - org.jsoup.UnsupportedMimeTypeException: Unhandled content type. Must be text/*, application/xml, or application/*+xml
* Do not follow redirect / Check on redirect
* Create frontend
  * Create option for crawling e.g: 
    * used selected browser specs
    * follow redirect true/false
    * leave urls spec characters in it, ...
    * crawl whole site
    * crawl single url
    * crawl to level 1,2, ... from given page.
    * take into account robot.txt or not during the crawling
* Ability to stop and continue (2 files, list of unprocessed and processed links which can be taken in any time and process).
* Order URLs links in a output file
  * alphabetically, by length etc.
