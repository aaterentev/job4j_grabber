package ru.job4j.grabber;

import java.util.ArrayList;
import java.util.List;

public class MemStore implements Store {
    private final List<Post> store = new ArrayList<>();
    private int postCount = 0;

    @Override
    public void save(Post post) {
        post.setId(postCount++);
        store.add(post);
    }

    @Override
    public List<Post> getAll() {
        return store;
    }

    @Override
    public Post findById(int id) {
        if (id < 0 || id >= store.size()) {
            throw new IllegalArgumentException("такого id не существует");
        }
        return store.get(id);
    }
}
