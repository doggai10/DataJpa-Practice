package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter @Setter
@ToString(of={"id", "username", "age"})
public class Member {

    @Id @GeneratedValue
    @Column(name= "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id")
    private Team team;

    protected Member() {
    }

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username=username;
        this.age=age;
        if(team!=null){
            changeTeam(team);
        }
    }


    public void changeUserName(String username){
        this.username=username;
    }

    public void changeTeam(Team team){
        this.team=team;
        team.getMembers().add(this);
    }
}
