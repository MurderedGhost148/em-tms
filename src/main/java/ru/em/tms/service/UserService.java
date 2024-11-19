package ru.em.tms.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.em.tms.lib.mapper.UserMapper;
import ru.em.tms.model.db.User;
import ru.em.tms.model.dto.PageableResponse;
import ru.em.tms.model.dto.user.UserEditDTO;
import ru.em.tms.model.dto.user.UserGetDTO;
import ru.em.tms.repo.UserRepo;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepo repo;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    public PageableResponse<UserGetDTO> getAll(Pageable pageable) {
        Page<User> page = repo.findAll(pageable);

        return new PageableResponse<>(page.get()
                .map(mapper::sourceToDestination)
                .toList(),
                page.getTotalPages(),
                page.getPageable().getPageNumber(),
                page.getPageable().getPageSize());
    }

    @Transactional(readOnly = true)
    public Optional<UserGetDTO> getById(Integer id) {
        return repo.findById(id).map(mapper::sourceToDestination);
    }

    public UserGetDTO create(UserEditDTO dto) {
        var user = repo.save(User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build());

        return mapper.sourceToDestination(user);
    }

    public UserGetDTO update(Integer id, UserEditDTO dto) {
        var saved = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        saved.setEmail(dto.getEmail());
        saved.setPassword(dto.getPassword());
        saved.setRole(dto.getRole());

        return mapper.sourceToDestination(saved);
    }

    public void delete(Integer id) {
        if(getCurrentUser().getId().equals(id))
            throw new AccessDeniedException("Нельзя удалить текущий аккаунт");

        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByEmail;
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByEmail(username);
    }
}
