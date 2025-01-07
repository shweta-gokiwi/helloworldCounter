package com.hello.helloworld.controller;

import com.hello.helloworld.entity.User;
import com.hello.helloworld.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@EnableCaching
public class HelloWorldCounter {
    private static final String COUNTER_KEY_PREFIX = "userCounter:";
    private static final String USER_KEY_PREFIX = "user:";

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/increment")
    public String increment(@RequestParam String name) {

        String userKey = USER_KEY_PREFIX + name;
        User user = (User) redisTemplate.opsForValue().get(userKey);

        if (user == null) {
            user = new User(name, 0);
        } else {

            user = userRepository.findByName(name);
        }

        user.setCounter(user.getCounter() + 1);
        userRepository.save(user);
        redisTemplate.opsForValue().set(userKey, user);

        return "Counter increment for " + name + " and Current value: " + user.getCounter();
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "User not found. ";
        }
        userRepository.deleteById(id);

        String counterKey = COUNTER_KEY_PREFIX + user.getCounter();
        String userKey = USER_KEY_PREFIX + user.getName();
        redisTemplate.delete(counterKey);
        redisTemplate.delete(userKey);

        return "Successfully deleted user with ID: " + id;
    }

    @PutMapping("/update/{id}")
    public String update(@PathVariable Long id, @RequestParam String newName) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "User not found";
        }

        String oldName = user.getName();
        user.setName(newName);
        userRepository.save(user);
        userRepository.deleteById(id);

        String oldCounterKey = COUNTER_KEY_PREFIX + oldName;
        String newCounterKey = COUNTER_KEY_PREFIX + newName;
        String userKey = USER_KEY_PREFIX + newName;

        Long counterValue = redisTemplate.opsForValue().get(oldCounterKey) != null ?
                Long.valueOf(redisTemplate.opsForValue().get(oldCounterKey).toString()) : 0L;
        redisTemplate.delete(oldCounterKey);
        redisTemplate.opsForValue().set(newCounterKey, counterValue);

        redisTemplate.opsForValue().set(userKey, user);

        return "User name updated successfully to " + newName;
    }

    @GetMapping("/users")
    public  List<Object> getAllUsers() {
        List<Object> users = new ArrayList<>();

        ScanOptions options = ScanOptions.scanOptions().match("user:*").count(100).build();
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options);

        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            Object value = redisTemplate.opsForValue().get(key);
            users.add(value);
        }

        return users;
    }
}

