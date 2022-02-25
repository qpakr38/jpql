package jpql;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class SubQueryTest {
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
    void ageAvgUpCaseTest() {
        for (int i = 0; i < 30; i++) {
            memberSet("member" + i, i);
        }
        String findAllQuery = "select m from Member m";
        List<Member> findAllMembers = entityManager.createQuery(findAllQuery,Member.class).getResultList();
        assertThat(findAllMembers.size()).isEqualTo(30);


        String query = "select m from Member m where m.username in :userNames";
        List<String> userNames = new ArrayList<>();
        userNames.add("member1");
        userNames.add("member2");
        List<Member> findMembers = entityManager.createQuery(query, Member.class)
                .setParameter("userNames", userNames)
                .getResultList();
        assertThat(findMembers.size()).isEqualTo(2);
        for (Member member : findMembers) {
            System.out.println("member = " + member);
        }
    }
}
