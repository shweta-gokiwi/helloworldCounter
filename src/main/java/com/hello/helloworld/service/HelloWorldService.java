package com.hello.helloworld.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gokiwi.core.utils.helper.CacheHelper;
import com.hello.helloworld.admin.payload.response.HelloWorldResponse;
import com.hello.helloworld.entity.User;
import com.hello.helloworld.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class HelloWorldService {

    private static final String COUNTER_KEY_PREFIX = "userCounter:";
    private static final String USER_KEY_PREFIX = "user:";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheHelper cacheHelper;

    @Value("${cache.namespace}")
    private String cacheNamespace;

    public HelloWorldResponse incrementCounter(String name) {

    String userKey = USER_KEY_PREFIX + name;
    String cachedUser = cacheHelper.get(userKey);

    int newCounterValue;
    HelloWorldResponse response;

    if (cachedUser == null) {
        long currentMaxCounter = userRepository.getMaxCounterByName(name); //retrieve the max_counter value from the database for the user
        newCounterValue = (currentMaxCounter == 0) ? 1 : (int) currentMaxCounter + 1;

        User newUser = new User(name, newCounterValue); // Create a new user entry with incremented counter
        userRepository.save(newUser);
        cacheHelper.put(userKey, 3600, serializeUser(newUser)); // Store the user in the cache (serialize and set expiration)

        response = new HelloWorldResponse(200, "Counter incremented for " + name + ". Current value: " + newCounterValue);
    } else {
        User user = deserializeUser(cachedUser);
        newCounterValue = user.getCounter() + 1;
        user.setCounter(newCounterValue);

        userRepository.save(user);   // save the incremented counter value
        cacheHelper.put(userKey, 3600, serializeUser(user));  // Update cache
        response = new HelloWorldResponse(200, "Counter incremented for " + name + ". Current value: " + newCounterValue);
    }
    cacheHelper.increment(COUNTER_KEY_PREFIX + name);
    return response;
}

    public HelloWorldResponse deleteUser(Long id) {
        HelloWorldResponse response;
        User user = userRepository.findById(id).orElse(null);  //fetched by id
        if (user == null) {
            response = new HelloWorldResponse(200, "User not found with id: " + id);  // if user not found
            return response;
        }

        userRepository.deleteById(id);  // delete the user

        cacheHelper.delete(USER_KEY_PREFIX + user.getName());  // delete name from cache
        cacheHelper.delete(COUNTER_KEY_PREFIX + user.getCounter());  // delete counter from cache

        response = new HelloWorldResponse(200, "Successfully deleted user with ID: " + id);
        return response;
    }

    public HelloWorldResponse updateUser(Long id, String newName) {
        HelloWorldResponse response;

        User user = userRepository.findById(id).orElse(null); // fetching the user by ID
        if (user == null) {
            response = new HelloWorldResponse(200, "User not found."); //user not found
            return response;
        }

        // Updating the user details
        String oldName = user.getName();
        user.setName(newName);
        userRepository.save(user);

        // Updating cache
        cacheHelper.delete(USER_KEY_PREFIX + oldName);
        cacheHelper.put(USER_KEY_PREFIX + newName, 3600, serializeUser(user)); // Cache new user for 1 hour


        response = new HelloWorldResponse(200, "User name updated successfully to " + newName); // Prepare success response
        return response;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Fetch all cached user keys with key
        List<String> cachedKeys = cacheHelper.getListKeysWithPrefix("user:");
        cachedKeys.addAll(cacheHelper.getListKeysWithPrefix("userCounter:"));


        if (cachedKeys.isEmpty()) {
            System.out.println("No cached keys found for prefix: " + USER_KEY_PREFIX);
            users = userRepository.findAll();
            return users;        // Return users fetched from the database
        }

        for (String key : cachedKeys) {
            String value = cacheHelper.get(key);
            if (value != null) {
                try {
                    if (value.startsWith("{") && value.endsWith("}")) {
                        User user = objectMapper.readValue(value, User.class);
                        users.add(user);
                    }
                } catch (Exception e) {
                    System.out.println("Error deserializing cache value for key " + key + ": " + e.getMessage());
                }
            } else {
                System.out.println("Cache miss for key: " + key);
            }
        }

        return users;
    }

    private String serializeUser(User user) {
        return user.getName() + ":" + user.getCounter();
    }

    private User deserializeUser(String data) {
    try {
        String[] parts = data.split(":");
        return new User(parts[0], Integer.parseInt(parts[1]));
    } catch (Exception e) {
        throw new RuntimeException("Invalid user data in cache: " + data);
    }
}

}
