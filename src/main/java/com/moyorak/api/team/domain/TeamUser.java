package com.moyorak.api.team.domain;

import com.moyorak.infra.orm.AuditInformation;
import com.moyorak.infra.orm.BooleanYnConverter;
import com.moyorak.infra.orm.TeamRoleConverter;
import com.moyorak.infra.orm.TeamUserStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "team_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamUser extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @Comment("팀 가입 상태")
    @Convert(converter = TeamUserStatusConverter.class)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(16)")
    private TeamUserStatus status;

    @Comment("팀 내 역할")
    @Convert(converter = TeamRoleConverter.class)
    @Column(name = "role", nullable = false, columnDefinition = "varchar(16)")
    private TeamRole role;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Comment("유저 고유 ID")
    @Column(name = "member_id", nullable = false, columnDefinition = "bigint")
    private Long userId;

    @Comment("팀 고유 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Builder(access = AccessLevel.PRIVATE)
    private TeamUser(TeamUserStatus status, TeamRole role, boolean use, Long userId, Team team) {
        this.status = status;
        this.role = role;
        this.use = use;
        this.userId = userId;
        this.team = team;
    }

    /**
     * 팀에 최초로 가입하는 사용자를 위한 메서드
     *
     * @param team
     * @param userId
     * @return
     */
    public static TeamUser firstJoin(final Team team, final Long userId) {
        return TeamUser.builder()
                .team(team)
                .userId(userId)
                .status(TeamUserStatus.APPROVED)
                .role(TeamRole.TEAM_ADMIN)
                .use(true)
                .build();
    }

    public boolean isNotApproved() {
        return status != TeamUserStatus.APPROVED;
    }

    public boolean isTeamAdmin() {
        return role == TeamRole.TEAM_ADMIN;
    }

    public boolean isTeam(Long teamId) {
        return Objects.equals(team.getId(), teamId);
    }

    public void approve() {
        this.status = TeamUserStatus.APPROVED;
    }

    public void reject() {
        status = TeamUserStatus.REJECTED;
        use = false;
    }

    public void restore() {
        status = TeamUserStatus.PENDING;
        use = true;
    }

    public void withdraw() {
        status = TeamUserStatus.WITHDRAWN;
        use = false;
    }

    public void changeRole(TeamRole role) {
        this.role = role;
    }

    public static TeamUser create(
            Team team, Long userId, TeamRole teamRole, TeamUserStatus teamUserStatus) {
        TeamUser teamUser = new TeamUser();
        teamUser.team = team;
        teamUser.userId = userId;
        teamUser.role = teamRole;
        teamUser.status = teamUserStatus;

        return teamUser;
    }
}
