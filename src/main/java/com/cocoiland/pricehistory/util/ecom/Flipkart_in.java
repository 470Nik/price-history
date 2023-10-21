package com.cocoiland.pricehistory.util.ecom;

import com.cocoiland.pricehistory.dto.ProductIdAndLsp;
import com.cocoiland.pricehistory.dto.UserInputDetails;
import com.cocoiland.pricehistory.entity.ProductDetails;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Flipkart_in implements EcommerceSite {
    private String productUrl;

    public Flipkart_in(String productUrl){
        //TODO: clean and repair the url
        this.productUrl = productUrl;
    }

    @Override
    public ProductIdAndLsp findProductIdAndPrice() throws IOException {
        String url = productUrl;
        ProductIdAndLsp productIdAndLsp = new ProductIdAndLsp();
        if(url.length() > 0 && url.charAt(0) != 'h')//TODO: improve this line
            url = repairUrl(url);
        Document document = Jsoup.connect(url).get();

        //Scrape and set pid
        scrapeAndSetProductId(productIdAndLsp, document);
        //Scrape and set product price
        scrapeAndSetProductLastSellingPrice(productIdAndLsp, document);

        return productIdAndLsp;
    }

    @Override
    public ProductDetails getProductDetailsFromEcommerceSite() throws IOException {
        String url = productUrl;
        if(url.length() > 0 && url.charAt(0) != 'h')
            url = repairUrl(url);
        ProductDetails  productDetails = new ProductDetails();
        Document document = Jsoup.connect(url).get();

        //Scrape and set product name
        scrapeAndSetProductName(productDetails, document);
        //Scrape and set product rating
        scrapeAndSetProductRating(productDetails, document);
        //Scrape and set product image url
        scrapeAndSetProductImageUrl(productDetails, document);

        return productDetails;
    }

    private void scrapeAndSetProductImageUrl(ProductDetails productDetails, Document document) {
        Element imgDiv = document.getElementsByClass("_3kidJX").first();
        if(imgDiv != null){
            Element img = imgDiv.select("img").first();
            if(img != null && StringUtils.isNotEmpty(img.attr("abs:src")))
                productDetails.setImageUrl(img.attr("abs:src"));
        }
    }

    private void scrapeAndSetProductRating(ProductDetails productDetails, Document document) {
        Element rating = document.getElementsByClass("_3LWZlK").first();
        if(rating != null){
            if(StringUtils.isNotEmpty(rating.text())){
                productDetails.setRating(Float.valueOf(rating.text()));
            }
        }
    }

    private void scrapeAndSetProductName(ProductDetails productDetails, Document document) {
        Element nameElement = document.getElementsByClass("_1LJS6T _2whKao _1QoaG0").first();
        if(nameElement != null) {
            Element pElement = nameElement.select("p").first();
            if(pElement != null)
                productDetails.setName(pElement.text());
        }
    }

    private void scrapeAndSetProductLastSellingPrice(ProductIdAndLsp productIdAndLsp, Document document) {
        double lsp = -1.0;
        Element element = document.getElementsByClass("_30jeq3 _16Jk6d").first();
        if(element != null && StringUtils.isNotEmpty(element.text())) {
            String price = element.text();
            price = price.substring(1);
            price = price.replaceAll(",", "");
            lsp = Double.parseDouble(price);
        }
        productIdAndLsp.setLsp(lsp);
    }

    private void scrapeAndSetProductId(ProductIdAndLsp productIdAndLsp, Document document){
        String pid = null;
        String url = document.location();
        if(url!=null && url.contains("?pid=")){
            int startIndex = url.indexOf("?pid=");
            if(startIndex != -1){
                startIndex += 5;
                pid = url.substring(startIndex, startIndex+16);
            }
        }
        productIdAndLsp.setPid(pid);
    }

    private String repairUrl(String url) {
        return "https://" + url;
    }

}
