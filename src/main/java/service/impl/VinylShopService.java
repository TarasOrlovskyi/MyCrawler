package service.impl;

import entity.Vinyl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import service.ShopService;

import java.io.IOException;
import java.util.*;

public class VinylShopService implements ShopService {
    private final LinkedHashSet<String> allUniqueInnerLinks = new LinkedHashSet<>();
    private final HashSet<Vinyl> dataOfProducts = new HashSet<>();
    private static final String START_LINK = "http://vinyl.ua";

    private HashSet<String> getLinks(String url) throws IOException {
        HashSet<String> showcaseLinks = new HashSet<>();
        Document doc = Jsoup.connect(url).get();
        Elements innerLinks;
        innerLinks = doc.getElementsByTag("a");
        for (Element innerLink : innerLinks) {
            String link;
            if (innerLink.attr("href").contains("www") || innerLink.attr("href").contains("http")) {
                link = innerLink.attr("href");
            } else {
                link = START_LINK + innerLink.attr("href");
            }
            if (url.equals(START_LINK)) {
                if (link.contains("http://vinyl.ua/showcase/")) {
                    showcaseLinks.add(link);
                }
            } else if (link.contains("?page=") && !link.contains("ussr?page=2")) {
                allUniqueInnerLinks.add(link);
            }
        }
        return showcaseLinks;
    }

    private void readDataProductFromPage(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements pageElements = doc.getElementsByClass("vinyl-release showcase");
        for (Element pageElement : pageElements) {
            String release = pageElement.getElementsByClass("margin-top-clear margin-bot-5").text();
            //FIXME http://vinyl.ua/release/567/various-phatmix-volume-2 - there is problem, because there is no teg <a>, because there is no name Artist. There is <span>
            String artist = pageElement.getElementsByClass("text-ellipsis").select("a").text();
            String price = pageElement.getElementsByClass("pull-left margin-top-5 showcase-release-price").text();
            String vinylLink = START_LINK + pageElement.getElementsByClass("img-showcase-release").select("a").attr("href");
            String[] imageLinks = pageElement.getElementsByClass("img-showcase-release").attr("style").split("'");
            String imageLink = imageLinks[1];
            String[] genres = url.split("[/?]");
            String genre = genres[4];

            Vinyl vinyl = new Vinyl();
            vinyl.setRelease(release);
            vinyl.setArtist(artist);
            vinyl.setPrice(price);
            vinyl.setVinylLink(vinylLink);
            vinyl.setImageLink(imageLink);
            vinyl.setGenre(genre);
            dataOfProducts.add(vinyl);
        }
    }

    @Override
    public List<Vinyl> getDataProduct() throws IOException {
        HashSet<String> showcaseLinks = getLinks(START_LINK);
        for (String showcaseLink : showcaseLinks) {
            getLinks(showcaseLink);
        }
        for (String allUniqueInnerLink : allUniqueInnerLinks) {
            readDataProductFromPage(allUniqueInnerLink);
        }
        return new ArrayList<>(dataOfProducts);
    }
}
