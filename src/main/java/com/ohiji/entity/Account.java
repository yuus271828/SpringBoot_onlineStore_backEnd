package com.ohiji.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ACCOUNT")
@Data
@NoArgsConstructor
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, length = 50)
    private int id;
    @Column(name = "email", unique = true, nullable = false, length = 50)
    private String email;
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "authority_id")
    private Authority authority;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "userinfo_id")
    private UserInfo userInfo;
    // CascadeType.ALL 表示：無論儲存、合併、 更新或移除，一併對被參考物件作出對應動作。
    // FetchType.EAGER 在查詢時立刻載入關聯的物件。
    // FetchType.LAZY 只在用到時才載入關聯的物件。

    /*
    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(name = "ACCOUNTS_AUTHORITY",
            joinColumns = @JoinColumn(referencedColumnName = "account_id"),
            inverseJoinColumns = @JoinColumn(referencedColumnName ="authority_id"))
    @MapKeyJoinColumn(name = "userInfo_id")
    private List<Authority> authorities;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(name = "ACCOUNTS_USERINFO", joinColumns = @JoinColumn(referencedColumnName = "id"),inverseJoinColumns = @JoinColumn(referencedColumnName ="id"))
    private List<UserInfo> usersInfo;
    */

}
