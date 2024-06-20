package com.customapi.fmkpl.okhttp;

public class UserSetting {
    private int is_valid = 0;
    private long expiration_time = 0;
    private long install_time = 0;

    public int getIs_valid() {
        return is_valid;
    }

    public long getInstall_time() {
        return install_time;
    }

    public long getExpiration_time() {
        return expiration_time;
    }

    public void setIs_valid(int is_valid) {
        this.is_valid = is_valid;
    }

    public void setInstall_time(long install_time) {
        this.install_time = install_time;
    }

    public void setExpiration_time(long expiration_time) {
        this.expiration_time = expiration_time;
    }
}
