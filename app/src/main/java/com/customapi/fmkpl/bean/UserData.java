/**
 * Copyright 2023 bejson.com
 */
package com.customapi.fmkpl.bean;

public class UserData {

    private int id;
    private String account;
    private String nickname;
    private String avatar;
    private int point;
    private long addtime;
    private int viptime;
    private String qqunionid;
    private String qqopenid;
    private String background;
    private int is_forever_vip;
    private int self_service_unbinding_time;
    private int growth;
    private String capping_growth_value;
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public String getAccount() {
        return account;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getNickname() {
        return nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getAvatar() {
        return avatar;
    }

    public void setPoint(int point) {
        this.point = point;
    }
    public int getPoint() {
        return point;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }
    public long getAddtime() {
        return addtime;
    }

    public void setViptime(int viptime) {
        this.viptime = viptime;
    }
    public int getViptime() {
        return viptime;
    }

    public void setQqunionid(String qqunionid) {
        this.qqunionid = qqunionid;
    }
    public String getQqunionid() {
        return qqunionid;
    }

    public void setQqopenid(String qqopenid) {
        this.qqopenid = qqopenid;
    }
    public String getQqopenid() {
        return qqopenid;
    }

    public void setBackground(String background) {
        this.background = background;
    }
    public String getBackground() {
        return background;
    }

    public void setIs_forever_vip(int is_forever_vip) {
        this.is_forever_vip = is_forever_vip;
    }
    public int getIs_forever_vip() {
        return is_forever_vip;
    }

    public void setSelf_service_unbinding_time(int self_service_unbinding_time) {
        this.self_service_unbinding_time = self_service_unbinding_time;
    }
    public int getSelf_service_unbinding_time() {
        return self_service_unbinding_time;
    }

    public void setGrowth(int growth) {
        this.growth = growth;
    }
    public int getGrowth() {
        return growth;
    }

    public void setCapping_growth_value(String capping_growth_value) {
        this.capping_growth_value = capping_growth_value;
    }
    public String getCapping_growth_value() {
        return capping_growth_value;
    }

}