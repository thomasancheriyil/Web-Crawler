package edu.uci.ics.crawler4j.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
                                                           + "|png|mp3|mp3|zip|gz))$");
    String crawlStorageFolder = "C:\\Users\\ThomasGeorge\\workspace\\crawler4j-master\\root\\";
    static int count=0;
    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         return !((Pattern) FILTERS).matcher(href).matches()
                && href.startsWith("http://www.ebay.com/");
     }

     /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         System.out.println("URL: " + url);
         BufferedWriter bw = null;
         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText();
             String html = htmlParseData.getHtml();
             Set<WebURL> links = htmlParseData.getOutgoingUrls();

             System.out.println("Text:"+ text);
             for (WebURL s : links) {
            	    System.out.println("link: "+s.toString());
            	}
             System.out.println("Text length: " + text.length());
             System.out.println("Html length: " + html.length());
             System.out.println("Number of outgoing links: " + links.size());
             
             
             try {
                // APPEND MODE SET HERE
                bw = new BufferedWriter(new FileWriter(crawlStorageFolder+Integer.toString(count++)+".txt", true));
            bw.write(text);
            bw.newLine();
            bw.flush();
             } catch (IOException ioe) {
            ioe.printStackTrace();
             } finally {                       // always close the file
            if (bw != null) try {
               bw.close();
            } catch (IOException ioe2) {
               // just ignore it
            }
            }
         }
    }
}
