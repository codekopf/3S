### Dictionary ###

* **domain** - Domain name registered in DNS
* **websiteURL** - URL on which domain can be accessed in browser URL field.
* **root** - Starting websiteURL from which crawling starts. Usually it is **websiteURL**.
* **page** - Accessing URL I get HTML page (in request response body). Page is everything inside HTML code. 
* **parent** - If page has a link on another page, the page which is linking is a parent towards the linked page. The linked page is **child**.
* **child** - It is a page linked and accessed from its parent.
* **level** - Number of hops from parent to child to get from root to certain **page**. More info in graph theory and distance from **root** to graph node.  

### TODO ###

#### FEATURES ####
* Save results to CSV and EXCEL file
* Create data structure in which I keep page's:
  * word count          - document.body().text(); // whole text
  * title               - document.title();
  * size                - (document.head() + document.body()) >> SIZE 
  * number of images
* Create frontend
  * Create options for crawling e.g:
    * used selected browser specs
    * follow redirect true/false            - redirect TRUE - document.location();
    * leave urls spec characters in it, ...
    * crawl whole site
    * crawl single url
    * crawl to level 1,2, ... from given page.
    * take into account robot.txt or not during the crawling
* Ability to stop and continue (2 files, list of unprocessed and processed links which can be taken in any time and process).
* Order URLs links in a output file
  * alphabetically, by length etc.
* Possibility to set crawling page delay - protection against script being classified as bot and being blocked
  * normal - one second
  * random - bell curve of 1 minute range 

#### CODE ####
* Check scrapping failure for IllegalArgumentException
* Do not follow redirect / Check on redirect
* Use Reactive Java - event on a processed page between frontend-backend
* Make Lombok use LOG for log anotation
* Add redirect check
* Replace get() method for execute

        final Response response = Jsoup.connect("https://web.azurewebsites.net/Data/DownloadFile")
                                       .ignoreContentType(true)
                                       .method(Method.GET)
                                       .data(queryParameters())
                                       .execute();


#### OTHERS ####
* Create config map for special cases
  * e.g. link.contains("#") - same URL but with internal anchor -> remove duplicats 
  * e.g. link.contains("gp/"))
* How to handle link on image?
  * e.g. https://codepills.com/wp-content/uploads/2016/03/osx-xampp-wordpress-ftp-install-error-directory-settings.png
  * Another exception - org.jsoup.UnsupportedMimeTypeException: Unhandled content type. Must be text/*, application/xml, or application/*+xml
