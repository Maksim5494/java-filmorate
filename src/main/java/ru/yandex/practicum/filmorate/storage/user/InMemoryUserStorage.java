package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUserStorage implements UserStorage {
    private List<User> users = new ArrayList<>();

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void removeUser(User user) {
        users.remove(user);
    }

    @Override
    public void modifyUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                break;
            }
        }
    }

    @Override
    public User findUserById(long id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public List<User> findUsersByNameOrEmail(String searchTerm) {
        List<User> foundUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getName().equals(searchTerm) || user.getEmail().equals(searchTerm)) {
                foundUsers.add(user);
            }
        }
        return foundUsers;
    }

    @Override
    public int countUsers() {
        return users.size();
    }
}


