package com.boxfox.dsm_boxfox.Server;

/**
 * Created by 김성래 on 2016-01-12.
 */
public class Logined {
    public static final int PERMISSION_STUDNET=1;
    public static final int PERMISSION_ADMIN=2;
    public static final int PERMISSION_PARENT=3;
    private boolean logged=false;
    private String name,phone,cname,cnum,permission,id,password;
    public void setID(String id){this.id = id;}
    public String getID(){return id;}
    public void setLogged(boolean b){logged=b;}
    public void setName(String name){this.name=name;}
    public void setPhone(String phone){this.phone=phone;}
    public void setCname(String cname){this.cname=cname;}
    public void setCnum(String cnum){this.cnum=cnum;}
    public String getPhone(){return phone;}
    public String getCname(){return cname;}
    public String getCnum(){return cnum;}
    public boolean islogin(){return logged;}
    public String getName(){return name;}
    public void setPermission(String p){this.permission = p;}
    public boolean isAdmin(){if(permission.equals("admin"))return true; else return false;}
    public int getPermission(){
        if(permission.equals("admin")){
            return PERMISSION_ADMIN;
        }else if(permission.equals("student")){
            return PERMISSION_STUDNET;
        }else if(permission.equals("parent")){
            return PERMISSION_PARENT;
        }
        return 0;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public String getPermissionString() {
        if(permission.equals("admin")){
            return "A";
        }else if(permission.equals("student")){
            return "S";
        }else if(permission.equals("parent")){
            return "P";
        }
        return "G";
    }
}
