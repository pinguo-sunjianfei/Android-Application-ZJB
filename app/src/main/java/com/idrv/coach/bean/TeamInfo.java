package com.idrv.coach.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * time: 2016/3/23
 * description:
 *
 * @author bigflower
 */
public class TeamInfo {

    /**
     * id : 1
     * name : 雷厉风行
     * ownerId : 146
     * users : [{"id":"157","nickname":"嘻嘻哈哈樊冬","headimgurl":"http://7xjo4p.com2.z0.glb.qiniucdn.com/FjY7jUKlOhcEsHdqW0oIuvQ1C7tN","sex":0},{"id":"1","nickname":"Jt·baggio","headimgurl":"http://wx.qlogo.cn/mmopen/ibyukCN2aoweicMFSXr3ZwumT7Cvia6sFa5BfIwFWdQTvOqxPQngdkU1npRO3hkwY0KPeOL1WibqrGjdfVzoibEH5iahXXI6M8cfrb/0","sex":0},{"id":"7","nickname":"咖啡","headimgurl":"http://7xjo4p.com2.z0.glb.qiniucdn.com/FmQc5PqnhqzmO8x5O7NDlJrOOJyE","sex":0}]
     */

    private TeamEntity team;
    private ArrayList<TeamMember> inviteUsers;

    public void setTeam(TeamEntity team) {
        this.team = team;
    }

    public void setInviteUsers(ArrayList<TeamMember> inviteUsers) {
        this.inviteUsers = inviteUsers;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public ArrayList<TeamMember> getInviteUsers() {
        return inviteUsers;
    }

    public static class TeamEntity {
        private int id;
        private String name;
        private String ownerId;
        private List<TeamMember> users;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public void setUsers(List<TeamMember> users) {
            this.users = users;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public List<TeamMember> getUsers() {
            return users;
        }

        @Override
        public String toString() {
            return "TeamEntity{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", ownerId='" + ownerId + '\'' +
                    ", users=" + users +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TeamInfo{" +
                "team=" + team +
                ", inviteUsers=" + inviteUsers +
                '}';
    }
}
