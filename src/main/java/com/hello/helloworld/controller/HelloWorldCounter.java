package com.hello.helloworld.controller;

import com.hello.helloworld.admin.payload.response.HelloWorldResponse;
import com.hello.helloworld.entity.User;
import com.hello.helloworld.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hello.helloworld.service.HelloWorldService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloWorldCounter {

    @Autowired
    private HelloWorldService helloWorldService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns a simple greeting message.
     *
     * @return A string message "Hello World".
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    /**
     * Increments the counter for user with id,name,counter
     * This method checks if a user with the specified name exists in the cache. If the user is not found in the cache,
     * creates a new user entry in the database. The new user is also stored in the cache for future access.
     *
     * @param name
     * @return A response containing the updated counter information:
     *        - HTTP status code 200 on success.
     *        - A message indicating the new counter value for the user.
     *        - Example response: "Counter incremented for USERName Current value: 2"
     */
    @PostMapping("/increment")
    public ResponseEntity<HelloWorldResponse> incrementCounter(@RequestParam String name) {
        HelloWorldResponse response = helloWorldService.incrementCounter(name);
        return ResponseEntity.ok(response);
    }

    /**
     * delete a user by their ID.
     * This method fetches the user from the database using their ID. If the user exists, it deletes the user
     * from the database and clears any related entries in the cache (both user-specific and counter-specific cache keys).
     * If the user does not exist, it returns a message indicating that the user was not found.
     *
     * @param id The unique identifier of the user to be deleted.
     *           - Example: 123
     *
     * @return A `HelloWorldResponse` object containing:
     *         - Status 200 indicating the operation result.
     *         - A message indicating:
     *           - Success: "Successfully deleted user with ID: {id}".
     *           - Failure: "User not found with id: {id}".
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HelloWorldResponse> deleteById(@PathVariable Long id) {
        HelloWorldResponse response = helloWorldService.deleteUser(id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Updates the name of a user by their unique ID.
     * This API endpoint allows updating the name of a user in the database and ensures the cache is updated accordingly.
     * If the user with the specified ID does not exist, it returns a message indicating that no user was found.
     *
     * @param id The unique identifier of the user whose name needs to be updated.
     *           - Example: 123
     * @param newName The new name to be assigned to the user.
     *                - Example: "ABC XYZ"
     *
     * @return A response in a `HelloWorldResponse` object:
     *         - Status code 200 indicating the success or failure of the operation.
     *         - A message describing the outcome:
     *           - Success: "Username updated successfully to {newName}".
     *           - Failure: "User not found."
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<HelloWorldResponse> update(@PathVariable Long id, @RequestParam String newName) {
        HelloWorldResponse response = helloWorldService.updateUser(id, newName);
        return ResponseEntity.ok(response);
    }

    /**
     * Fetching list of all users.
     * This API endpoint fetches all users either from the cache or the database. If user data is available in the cache,
     * it retrieves and deserializes the data. If not, it queries the database to fetch all users. The response includes
     * the list of users and appropriate status messages.
     *
     * @return A response wrapped in a `Map<String, Object>`:
     *         - `status`: Indicates whether the operation was successful or not ("ok" or "failure").
     *         - `message`: A descriptive message about the operation.
     *         - `data`: The list of users, or an empty list if no users are found.
     *         Example successful response:
     *         {
     *             "status": "ok",
     *             "message": "Users fetched successfully",
     *             "data": [ { "id": 1, "name": "ABC XYZ" }, { "id": 2, "name": "Name SurName" } ]
     *         }
     *         Example failure response:
     *         {
     *             "status": "failure",
     *             "message": "No users found",
     *             "data": []
     *         }
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = helloWorldService.getAllUsers();

        Map<String, Object> response = new HashMap<>(); // response structure
        if (users != null && !users.isEmpty()) {
            response.put("status", "ok");
            response.put("message", "Users fetched successfully");
            response.put("data", users);  // Returning the list of users
        } else {
            response.put("status", "failure");
            response.put("message", "No users found");
        }
        return ResponseEntity.ok(response);
    }
}

