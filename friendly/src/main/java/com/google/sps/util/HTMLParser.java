package com.google.sps.util;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
* Java Program to parse/read HTML documents from File using Jsoup library.
*
*/
public class HTMLParser{
    
    public static String parseHTML(String HTMLFilePath) throws IOException{
        Document document = Jsoup.connect(HTMLFilePath).get();
        String hTMLString = document.toString();
        return hTMLString;
    }


}