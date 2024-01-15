package ru.practicum.user.service;

import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserMapper;
import ru.practicum.user.storage.UserStorage;
import ru.practicum.util.UnionService;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UnionService unionService;

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {

        User user = UserMapper.returnUser(userDto);
        userStorage.save(user);

        return UserMapper.returnUserDto(user);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (ids == null) {
            return UserMapper.returnUserDtoList(userStorage.findAll(pageRequest));
        } else {
            return UserMapper.returnUserDtoList(userStorage.findByIdInOrderByIdAsc(ids, pageRequest));
        }
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {

        unionService.getUserOrNotFound(userId);
        userStorage.deleteById(userId);
    }
}