package com.example.entity;
//实体类
public class Admin extends Account {
    private Integer id;
    private String username;
    private String name;
    private String password;
    private String phone;
    private String email;
    private String role;
    //非数据库字段
    private String ids;
    private String[] idsArr;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIds() {return ids;}

    public void setIds(String ids) {this.ids = ids;}

    public String[] getIdsArr() {return idsArr;}

    public void setIdsArr(String[] idsArr) {this.idsArr = idsArr;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}