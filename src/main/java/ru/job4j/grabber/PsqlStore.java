package ru.job4j.grabber;

import ru.job4j.html.SqlRuParse;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.InputStream;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties properties) {
        try {
            Class.forName(properties.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    properties.getProperty("url"),
                    properties.getProperty("username"),
                    properties.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        createTableIfNotExist();
    }

    /**
     * Создает таблицу, используя post.sql из директории db
     * считывание схемы sql идет через сканнер.
     */
    private void createTableIfNotExist() {
        try (Scanner sc = new Scanner(Path.of("./db/post.sql").toFile());
             Statement statement = cnn.createStatement()) {
            StringBuilder sql = new StringBuilder();
            while (sc.hasNextLine()) {
                sql.append(sc.nextLine());
            }
            statement.execute(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Добавляет в таблицу новую запись.
     * Используется возвращаемое значение, индекс присваивается посту.
     * @param post {@link Post} с сайта SqlRu
     */
    @Override
    public void save(Post post) {
        String sql = "insert into post(name, text, link, created) "
                + "values (?, ?, ?, ?);";
        try (PreparedStatement statement = cnn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Запрашивает все записи с БД
     * @return List<<Post>{@link Post}>
     */
    @Override
    public List<Post> getAll() {
        String sql = "select * from post;";
        List<Post> rsl = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rsl.add(createPostByResult(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    /**
     * Находит запись с требуемым индексом
     * @param id индекс записи
     * @return {@link Post}
     */
    @Override
    public Post findById(int id) {
        String sql = "select * from post where id = ?;";
        Post post = null;
        try (PreparedStatement statement = cnn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = createPostByResult(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    private Post createPostByResult(ResultSet resultSet) throws SQLException {
        Post rsl = new Post();
        rsl.setId(resultSet.getInt("id"));
        rsl.setLink(resultSet.getString("link"));
        rsl.setTitle(resultSet.getString("name"));
        rsl.setDescription(resultSet.getString("text"));
        rsl.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        return rsl;
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("post.properties")) {
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PsqlStore store = new PsqlStore(properties);
        SqlRuParse parser = new SqlRuParse(new SqlRuDateTimeParser());
        System.out.println("===Начат парсинг===");
        String url = "https://www.sql.ru/forum/job-offers/";
        List<Post> postsFromUrl = parser.list(url);
        System.out.println("===Парсинг закончен, записываем в БД 10 постов ===");
        for (int i = 0; i < 10; i++) {
            store.save(postsFromUrl.get(i));
        }
        System.out.println("===Закончена запись 10 постов в БД,"
                + " запрашиваем данные с БД и выводим в консоль===");
        store.getAll().forEach(System.out::println);
        System.out.println("===Запрашиваем в БД пост с индексом 1 и выводим в консоль===");
        System.out.println(store.findById(1));
        System.out.println("===Выполнено===");
    }
}
