package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }


    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen()  {
        //given
        Member m1 = saveMember("AAA","BBB");

        //when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        //then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery(){
        Member m1 = saveMember("AAA","BBB");

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery()  {
        //given
        Member m1 = saveMember("AAA","BBB");
        //when
        List<Member> result = memberRepository.findUser("AAA", 10);
        //then
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList(){
        //given
        Member m1 = saveMember("AAA","BBB");
        //when
        List<String> usernameList = memberRepository.findUsernameList();
        //then
        for (String name : usernameList) {
            System.out.println("name = " + name);
        }

    }

    @Test
    public void findMemberDto(){
        //given
        Team team = new Team("teamA");
        teamRepository.save(team);
        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        //when
        List<MemberDto> memberDto = memberRepository.findMemberDto();

        //then
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findNames(){
        //given
        Team team = new Team("teamA");
        teamRepository.save(team);
        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        //when
        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA","BBB"));

        //then
        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType(){
        //given
        saveMember("AAA", "BBB");

        //when
        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member aaa1 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("AAA");

        //then
        System.out.println("aaa.get(0) = " + aaa.get(0));
        System.out.println("aaa1 = " + aaa1);
        System.out.println("aaa2.get() = " + aaa2.get());
    }

    @Test
    public void paging(){
        //given
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 10);
        Member m3 = new Member("member3", 10);
        Member m4 = new Member("member4", 10);
        Member m5 = new Member("member5", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        long totalElements = page.getTotalElements();
        System.out.println("totalElements = " + totalElements);
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        //given
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 19);
        Member m3 = new Member("member3", 20);
        Member m4 = new Member("member4", 21);
        Member m5 = new Member("member5", 40);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        //then
        assertThat(resultCount).isEqualTo(3);

    }

    @Test
    public void findMemberLazy(){
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        saveMemberTeam("member1","member2",teamA,teamB);
        
        em.flush();
        em.clear();
        //when

        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @BeforeTestClass
    private Member saveMember(String name1, String name2) {
        Member m1 = new Member(name1, 10);
        Member m2 = new Member(name2, 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        return m1;
    }

    @Test
    public void queryHint(){
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
        //then

    }

    @Test
    public void lock(){
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        //when
        List<Member> result = memberRepository.findLockByUsername("member1");
        //then

    }

    @Test
    public void callCustom(){
        //given
        List<Member> result = memberRepository.findMemberCustom();
        //when

        //then

    }

    @BeforeTestClass
    private Member saveMemberTeam(String name1, String name2, Team team1, Team team2) {
        Member m1 = new Member(name1, 10,team1);
        Member m2 = new Member(name2, 20,team2);
        memberRepository.save(m1);
        memberRepository.save(m2);
        return m1;
    }
}