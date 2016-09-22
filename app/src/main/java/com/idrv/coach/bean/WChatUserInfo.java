package com.idrv.coach.bean;

/**
 * time:2016/3/8
 * description:微信用户信息
 *
 * @author sunjianfei
 */
public class WChatUserInfo {
    String openid;
    String nickname;
    int sex;
    String province;
    String city;
    String country;
    String headimgurl;
    String unionid;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    /**
     * 微信默认编码是"iso8859-1",中文需要转一下
     */
    public void encodingFormat() {
        try {
            nickname = new String(nickname.getBytes("iso8859-1"), "UTF-8");
            city = new String(city.getBytes("iso8859-1"), "UTF-8");
            province = new String(province.getBytes("iso8859-1"), "UTF-8");
            country = new String(country.getBytes("iso8859-1"), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
