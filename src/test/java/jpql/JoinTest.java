package jpql;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.Transactional;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class JoinTest {
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

    Member memberSet(String username, int age, Team team) {
        Member member = new Member();
        member.setUsername(username);
        member.setAge(age);
        member.setTeam(team);
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
    void innerJoinTest() {
        Team teamA = teamSet("teamA");
        memberSet("memberA", 10, teamA);

        String query = "select m from Member m inner join m.team t";
        List<Member> result = entityManager.createQuery(query, Member.class)
                .getResultList();
        assertThat(result.get(0).getTeam().getName()).isEqualTo(teamA.getName());
    }

    @Test
    @Transactional
    void outerJoinTest() {
        Team teamA = teamSet("teamA");
        memberSet("memberA", 10, teamA);

        String query = "select m from Member m left outer join m.team t";
        List<Member> result = entityManager.createQuery(query, Member.class)
                .getResultList();
        assertThat(result.get(0).getTeam().getName()).isEqualTo(teamA.getName());
    }

    @Test
    @Transactional
    void thetaJoinTest() {
        Team teamA = teamSet("teamA");
        memberSet("memberA", 10, teamA);

        String query = "select m from Member m,Team t where m.username = t.name";
        List<Member> result = entityManager.createQuery(query, Member.class)
                .getResultList();
        assertThat(result.size()).isZero();
    }

    @Test
    @DisplayName("회원과 팀을 조인하여 팀 이름이 'teamA'인 팀만 조회")
    @Transactional
    void TeamNameIsAJoinTest() {
        Team teamA = teamSet("teamA");
        Team teamB = teamSet("teamB");
        Member memberA = memberSet("memberA", 10, teamA);
        Member memberB = memberSet("memberB", 10, teamB);
        String query = "select m from Member m LEFT JOIN m.team t on t.name = 'teamA'";
        List<Member> resultList = entityManager.createQuery(query, Member.class)
                .getResultList();
        List<Member> collect = resultList.stream().filter(m -> (m.getTeam() == null)).collect(Collectors.toList());
        for (Member resultMember : collect) {
            assertThat(resultMember.getUsername()).isEqualTo(memberA.getUsername());
            assertThat(resultMember.getTeam().getName()).isEqualTo(teamA.getName());
        }

    }

    @Test
    @DisplayName("회원의 이름과 팀의 이름이 같은 대상 내부 조인")
    @Transactional
    void memberUsernameIsSameTeamNameTest() {
        Team team = teamSet("memberA");
        Member memberA = memberSet(team.getName(), 10, team);
        Member memberB = memberSet("memberB", 20, team);
        String query = "SELECT  m FROM Member m LEFT JOIN Team t on m.username = t.name";
        List<Member> resultList = entityManager.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member.toString());
        }
    }


}
