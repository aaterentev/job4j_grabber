package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;

public class SqlRuParse {
    public static void main(String[] args) throws IOException {

        String baseUrl = "https://www.sql.ru/forum/job-offers";
        String url = baseUrl;
        int counter = 0;
        for (int i = 1; i <= 5; i++) {
            if (i != 1) {
                url = baseUrl + "/" + i;
            }
            Document doc = Jsoup.connect(url).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                Element parent = td.parent();
                String s = parent.children().get(5).text();
                System.out.println(s);
                SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
                System.out.println(parser.parse(s));
                System.out.println("Номер вакансии: " + ++counter);
            }
            System.out.println("=================Конец страницы " + i + "=================");
        }
    }
}
