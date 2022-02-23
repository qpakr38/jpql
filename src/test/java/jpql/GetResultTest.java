package jpql;


import org.junit.jupiter.api.*;

import javax.persistence.*;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

public class GetResultTest {
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
    void memberSetTest() {
        //given
        memberSet("memberA", 10);
        //when
        TypedQuery<Member> query = entityManager.createQuery("select m from Member m", Member.class);
        Assertions.assertEquals(query.getResultList().size(), 1);
        //then
        Assertions.assertEquals(query.getResultList().get(0).getUsername(),
                "memberA");
    }

    @Test
    @DisplayName("getSingleResult 2개이상의 값을 호출한 경우")
    @Transactional
    void Set2MemberGetSingleResult() {
        //given
        memberSet("memberA", 10);
        memberSet("memberB", 10);
        memberSet("memberC", 10);
        //when
        TypedQuery<Member> query = entityManager.createQuery("select m from Member m where m.age = 10", Member.class);
        assertThat(query.getResultList().size()).isGreaterThan(2);
        //then
        Assertions.assertThrows(javax.persistence.NonUniqueResultException.class, () -> {
            Member singleResult = query.getSingleResult();
        });
    }

    @Test
    @DisplayName("Member를 set 하지않고 getSingleResult 값을 호출할 경우")
    @Transactional
    void SetNonGetSingleResultTest() {
        //given
        //when
        TypedQuery<Member> query = entityManager.createQuery("select m from Member m ", Member.class);
        assertThat(query.getResultList().size()).isZero();
        //then
        Assertions.assertThrows(javax.persistence.NoResultException.class, () -> {
            Member singleResult = query.getSingleResult();
        });
    }

    @Test
    @DisplayName("이름 기준 파라미터 바인딩")
    @Transactional
    void namedParametersTest() {
        String username = "memberA";
        memberSet(username, 10);
        TypedQuery<Member> query = entityManager.createQuery("select m from Member  m where m.username = :username", Member.class);
        query.setParameter("username", username);
        Member findMember = query.getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("기존 코드에 메소드체인 방식을 적용하여 가독성을 높임")
    @Transactional
    void namedParametersMethodChainTest() {
        String username = "memberA";
        memberSet(username, 10);
        Member findMember = entityManager.createQuery("select m from Member  m where m.username = :username", Member.class)
                .setParameter("username", username)
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("위치 기반 파라미터 바인딩")
    @Transactional
    void positionalParametersTest(){
        String username = "memberA";
        memberSet(username, 10);
        Member findMember = entityManager.createQuery("select m from Member  m where m.username = ?1", Member.class)
                .setParameter(1, username)
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo(username);
    }
}
