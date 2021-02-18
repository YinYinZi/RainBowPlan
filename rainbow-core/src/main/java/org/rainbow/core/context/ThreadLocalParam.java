package org.rainbow.core.context;

import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * @author K
 * @date 2021/2/10  10:19
 */
public class ThreadLocalParam implements Serializable {
    private Boolean boot;
    private String tenant;
    private Long userid;
    private String name;
    private String account;

    public ThreadLocalParam() {
    }

    public Boolean getBoot() {
        return boot;
    }

    public void setBoot(Boolean boot) {
        this.boot = boot;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ThreadLocalParam that = (ThreadLocalParam) o;
        return Objects.equal(boot, that.boot) &&
                Objects.equal(tenant, that.tenant) &&
                Objects.equal(userid, that.userid) &&
                Objects.equal(name, that.name) &&
                Objects.equal(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(boot, tenant, userid, name, account);
    }

    @Override
    public String toString() {
        return "ThreadLocalParam{" +
                "boot=" + boot +
                ", tenant='" + tenant + '\'' +
                ", userid=" + userid +
                ", name='" + name + '\'' +
                ", account='" + account + '\'' +
                '}';
    }
}
