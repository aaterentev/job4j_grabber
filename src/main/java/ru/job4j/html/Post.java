package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Post {
    private static final String NL = System.lineSeparator();
    private static final String DATE_TIME_PATTERN = "d M yyyy HH:mm";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime created;

    public Post(String url) throws IOException {
        this.link = url;
        try {
            parse(url);
        } catch (IOException e) {
            throw new IOException("Ошибка парсинга ссылки.");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    private void parse(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element first = doc.selectFirst(".msgTable");
        this.title = first.select(".messageHeader").get(0).ownText();
        this.description = first.select(".msgBody").get(1).ownText();
        var date = first.select(".msgFooter").get(0).ownText().substring(0, 16);
        date = validateDate(date);
        DateTimeParser parser = new SqlRuDateTimeParser();
        this.created = parser.parse(date);
    }

    /**
     * Метод устраняет ошибку при получении даты, у которой день
     * составляет 1 цифру, т.е. до 10 числа
     *
     * @param date дата
     * @return строка из 15 символов
     */
    private String validateDate(String date) {
        String rsl = date;
        if (date.endsWith(" ")) {
            rsl = date.substring(0, 15);
        }
        return rsl;
    }

    /**
     * Метод выводит текст поста в оформленном виде
     *
     * @return читабельный вид поста
     */
    @Override
    public String toString() {
        return String.format(
                "Post:%s{%sid: %d, %stitle: %s,%slink: %s,%sdescription: %s,%screated: %s%s}",
                NL, NL, id, NL, title, NL, link, NL, description, NL,
                created.format(FORMATTER), NL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Post p = (Post) o;

        if (id != p.id) {
            return false;
        }

        return Objects.equals(title, p.title) && Objects.equals(link, p.link)
                && Objects.equals(created, p.created);
    }

    @Override
    public int hashCode() {
        int rsl = id;
        rsl = 31 * rsl + (title == null ? 0 : title.hashCode());
        rsl = 31 * rsl + (link == null ? 0 : link.hashCode());
        rsl = 31 * rsl + (created == null ? 0 : created.hashCode());
        return rsl;
    }

    /**
     * Пример считывания 2х постов: с датой из 2х цифр и с 1 цифрой.
     *
     * @param args null
     * @throws IOException ошибка парсинга
     */
    public static void main(String[] args) throws IOException {
        Post p = new Post("https://www.sql.ru/forum/1325330/"
                + "lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
        System.out.println(p);
        Post newPost = new Post("https://www.sql.ru/forum/1340678/"
                + "ishhem-razrabotchika-v-krupnyy-amerikanskiy-vendor-moskva-ofis");
        System.out.println(newPost);
    }
}
