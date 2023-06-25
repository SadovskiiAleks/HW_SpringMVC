package org.example.repository;

import org.example.model.Post;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryImpl implements PostRepository {
    private final Map<Long, Post> mapOfPost = new ConcurrentHashMap<>();
    private final AtomicLong lastId = new AtomicLong(0);
    private final long startID = 0l;


    public List<Post> all() {
        return mapOfPost.values().stream().filter(x -> x.isRemoved() == false).collect(Collectors.toList());
    }

    public Optional<Post> getById(long id) {
        if (mapOfPost.containsKey(id) && !mapOfPost.get(id).isRemoved()) {
            return Optional.ofNullable(mapOfPost.get(id));
        } else {
            return Optional.ofNullable(null);
        }
    }

    public Optional<Post> save(Post post) {

        if (post.getId() == startID) {
            post.setId(lastId.incrementAndGet());
            mapOfPost.putIfAbsent(post.getId(), post);

        } else if (!mapOfPost.containsKey(post.getId())) {
            post.setId(lastId.incrementAndGet());
            mapOfPost.putIfAbsent(post.getId(), post);

        } else if (mapOfPost.containsKey(post.getId()) && mapOfPost.get(post.getId()).isRemoved()) {
            return Optional.ofNullable(null);

        } else if (mapOfPost.containsKey(post.getId())) {
            mapOfPost.putIfAbsent(post.getId(), post);
        }

        return Optional.ofNullable(post);
    }

    public void removeById(long id) {
        if (mapOfPost.containsKey(id)) {
            mapOfPost.get(id).setRemoved(true);
        }
    }
}
