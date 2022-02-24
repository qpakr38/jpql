package jpql;

import org.junit.jupiter.api.*;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ProjectionTest {
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
    @DisplayName("엔티티 프로젝션은 영속성컨텍스트에서 관리된다.")
    @Transactional
    void entityProjectionTest() {
        //given
        memberSet("memberA", 10);
        //when
        List<Member> result = entityManager.createQuery("select m from Member m", Member.class)
                .getResultList();
        Member findMember = result.get(0);
        findMember.setAge(20);
        entityManager.flush();
        entityManager.clear();
        Member resultMember = entityManager.createQuery("select m from Member  m where m.id = :findId", Member.class)
                .setParameter("findId", findMember.getId()).getSingleResult();
        //then
        assertThat(resultMember.getAge()).isEqualTo(20);
        assertThat(findMember.getAge()).isEqualTo(resultMember.getAge());
    }

    @Test
    @DisplayName("임베디드 타입 프로젝션")
    @Transactional
    void embeddedProjectionTest() {
        List<Address> resultList = entityManager.createQuery("select o.address from Order o", Address.class).getResultList();
        //entityManager.createQuery("select a from Address a",Address.class).getResultList();

    }

    @Test
    @DisplayName("스칼라 타입 프로젝션")
    @Transactional
    void scalaProjectionTest() {
        String username = "memberA";
        int age = 10;
        memberSet(username, age);
        List resultList = entityManager.createQuery("select m.username, m.age from Member m").getResultList();
    }
    @Test
    @DisplayName("프로젝션_여러개 값 조회-Query 타입으로 조회")
    @Transactional
    void multipleProjectionQueryTest() {
        String username = "memberA";
        int age = 10;
        memberSet(username, age);
        List resultList = entityManager.createQuery("select m.username, m.age from Member m").getResultList();
        Object[] object = (Object[]) resultList.get(0);
        assertThat(object[0]).isEqualTo(username);
        assertThat(object[1]).isEqualTo(age);
    }

    @Test
    @DisplayName("프로젝션_여러개 값 조회-Object[] 타입으로 조회")
    @Transactional
    void multipleProjectionObject() {
        String username = "memberA";
        int age = 10;
        memberSet(username, age);
        List<Object[]> resultList = entityManager.createQuery("select m.username, m.age from Member m").getResultList();
        Object[] object = resultList.get(0);
        assertThat(object[0]).isEqualTo(username);
        assertThat(object[1]).isEqualTo(age);
    }

    @Test
    @DisplayName("프로젝션_여러개 값 조회-new 명령어로 조회")
    @Transactional
    void multipleProjectionNewTest() {
        String username = "memberA";
        int age = 10;
        memberSet(username, age);
        List<MemberDto> resultList = entityManager.createQuery("select new jpql.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();
        MemberDto memberDto = resultList.get(0);
        assertThat(memberDto.getUsername()).isEqualTo(username);
        assertThat(memberDto.getAge()).isEqualTo(age);
    }


}
