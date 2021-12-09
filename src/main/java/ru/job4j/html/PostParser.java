package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;

public class PostParser {

    /**
     * Метод получает данные по указанной ссылке и создает из этих данных
     * новый Пост
     * @param url ссылка на источник
     * @return  {@link ru.job4j.html.Post}
     * @throws IOException ошибка при получении данных.
     * Тело страницы сайта не содержит требуемых полей
     */
    public Post parseFromUrl(String url) throws IOException {
        Post post = new Post();
        post.setLink(url);
        Document doc = Jsoup.connect(url).get();
        Element first = doc.selectFirst(".msgTable");
        post.setTitle(first.select(".messageHeader").get(0).ownText());
        post.setDescription(first.select(".msgBody").get(1).ownText());
        var date = first.select(".msgFooter").get(0).ownText().substring(0, 16);
        DateTimeParser parser = new SqlRuDateTimeParser();
        post.setCreated(parser.parse(date));
        return post;
    }
}
