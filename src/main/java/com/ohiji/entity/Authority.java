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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "AUTHORITY")
@Data
@NoArgsConstructor
public class Authority implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, length = 50)
    private int id;
    @Column(name = "role_code")
    private String roleCode;
    @Column(name = "role_description")
    private String roleDescription;

    @OneToMany(mappedBy = "authority",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Account> accounts;
}
