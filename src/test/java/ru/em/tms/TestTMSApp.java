package ru.em.tms;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import ru.em.tms.repo.UserRepo;

@TestConfiguration
public class TestTMSApp {
    @SpyBean
    private UserRepo userRepo;
}
