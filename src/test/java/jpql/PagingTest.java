package jpql;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class PagingTest {
    static EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;
    EntityTransaction transaction;


    @BeforeAll
    static void beforeAll() {
        entityManagerFactory = Persistence.createEntityManagerFactory("hello");
    }

    @BeforeEach
    void beforeEach() {
        entityManager = entityManagerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
        entityManager.close();
    }

    @AfterAll
    static void afterAll() {
        entityManagerFactory.close();
    }

    void memberSet(String username, int age) {
        Member member = new Member();
        member.setUsername(username);
        member.setAge(age);
        entityManager.persist(member);
    }

    @Test
    @Transactional
    void pagingTest() {
        String username = "member";
        int first = 0;
        int max = 10;
        for (int i = 0; i < 100; i++) {
            memberSet(username + i, i);
        }
        List<Member> resultList = entityManager.createQuery("select m from Member m order by m.age desc ", Member.class)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
        for (Member member : resultList) {
            first+=1;
            assertThat(member.getUsername()).isEqualTo(username+(100-first));
            assertThat(member.getAge()).isEqualTo(100-first);
        }
    }
}
