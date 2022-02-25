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

public class JpqlFunction {
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

    Member memberSet(String username, int age,Team team) {
        Member member = new Member();
        member.setUsername(username);
        member.setAge(age);
        member.changeTeam(team);
        entityManager.persist(member);
        return member;
    }

    Team teamSet(String name) {
        Team team = new Team();
        team.setName(name);
        entityManager.persist(team);
        return team;
    }
    @Test
    @Transactional
    void concatTest(){
        memberSet("member",10,teamSet("team1"));
        String query = "SELECT 'a' || 'b' FROM Member m";
        String result = entityManager.createQuery(query, String.class).getSingleResult();
        assertThat(result).isEqualTo("ab");
        query = "SELECT CONCAT('a', 'b') FROM Member m";
        result = entityManager.createQuery(query, String.class).getSingleResult();
        assertThat(result).isEqualTo("ab");
    }
    @Test
    @Transactional
    void sizeTest(){
        Team team = teamSet("TeamA");
        memberSet("memberA",10,team);
        memberSet("memberB",10,team);
        memberSet("memberC",10,team);
        String query="SELECT size(t.members) FROM Team t";
        Integer singleResult = entityManager.createQuery(query, Integer.class).getSingleResult();
        assertThat(singleResult).isEqualTo(3);
    }
    @Test
    @DisplayName("사용자정의함수")
    @Transactional
    void userCustomFunctionTest(){
        memberSet("member1",10,teamSet("team1"));
        memberSet("member2",10,teamSet("team1"));
        String query = "SELECT FUNCTION('group_concat',m.username) FROM Member m";
        String result = entityManager.createQuery(query, String.class).getSingleResult();
        assertThat(result).isEqualTo("member1,member2");
    }


}
