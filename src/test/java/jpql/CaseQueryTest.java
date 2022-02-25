package jpql;

import jpql.dto.MemberAdminDto;
import jpql.dto.MemberRateClassDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class CaseQueryTest {
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
    void basicCaseTest() {
        memberSet("memberA", 10);
        memberSet("memberB", 30);
        memberSet("memberC", 60);
        String query = "SELECT new jpql.dto.MemberRateClassDto(m.username, m.age , " +
                "CASE WHEN m.age <= 10 THEN '학생요금' " +
                "WHEN m.age >= 60 THEN '노인요금' " +
                "ELSE '일반요금' " +
                "END  )" +
                "FROM Member m";
        List<MemberRateClassDto> resultMembers = entityManager.createQuery(query, MemberRateClassDto.class).getResultList();
        assertThat(resultMembers.size()).isEqualTo(3);
        for (MemberRateClassDto resultMember : resultMembers) {
            if (resultMember.getAge() <= 10) {
                assertThat(resultMember.getRateClassName()).isEqualTo("학생요금");
            } else if (resultMember.getAge() >= 60) {
                assertThat(resultMember.getRateClassName()).isEqualTo("노인요금");
            } else {
                assertThat(resultMember.getRateClassName()).isEqualTo("일반요금");
            }
        }
    }

    @Test
    @Transactional
    void simpleCaseTest() {
        memberSet("memberA", 10);
        memberSet("memberB", 30);
        memberSet("memberC", 60);
        String query = "SELECT new jpql.dto.MemberRateClassDto(m.username, m.age , " +
                "CASE m.username " +
                "WHEN 'memberA' THEN '학생요금' " +
                "WHEN 'memberB' THEN '노인요금' " +
                "ELSE '일반요금' " +
                "END  )" +
                "FROM Member m";
        List<MemberRateClassDto> resultMembers = entityManager.createQuery(query, MemberRateClassDto.class).getResultList();
        assertThat(resultMembers.size()).isEqualTo(3);
        for (MemberRateClassDto resultMember : resultMembers) {
            if (resultMember.getUsername().equals("memberA")) {
                assertThat(resultMember.getRateClassName()).isEqualTo("학생요금");
            } else if (resultMember.getUsername().equals("memberB")) {
                assertThat(resultMember.getRateClassName()).isEqualTo("노인요금");
            } else {
                assertThat(resultMember.getRateClassName()).isEqualTo("일반요금");
            }
        }
    }

    @Test
    @Transactional
    void coalesceTest() {
        String memberName = "이름없는 회원";
        memberSet(null, 10);
        String query = "SELECT COALESCE(m.username, :memberName) FROM Member m";
        String result = entityManager.createQuery(query, String.class)
                .setParameter("memberName",memberName)
                .getSingleResult();
        assertThat(result).isEqualTo(memberName);
    }

    @Test
    @Transactional
    void nullifTest() {
        memberSet("관리자", 10);
        memberSet("memberA", 20);
        String query = "SELECT new jpql.dto.MemberAdminDto(m.username, m.age, NULLIF(m.username,'관리자') as adminName) FROM Member m order by m.age";
        List<MemberAdminDto> resultList = entityManager.createQuery(query, MemberAdminDto.class).getResultList();
        for (MemberAdminDto memberAdminDto : resultList) {
            if (memberAdminDto.getUsername().equals("관리자")) {
                assertThat(memberAdminDto.getAdminName()).isNull();
            } else {
                assertThat(memberAdminDto.getAdminName()).isEqualTo(memberAdminDto.getUsername());
            }
        }
    }


}
