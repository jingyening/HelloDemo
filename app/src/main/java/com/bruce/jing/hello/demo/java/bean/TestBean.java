package com.bruce.jing.hello.demo.java.bean;

import java.lang.reflect.Field;

public class TestBean implements Cloneable{

    private int id;
    private int age;
    private String addr;
    private String name;

    private boolean isEnable;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @Override
    public TestBean clone() throws CloneNotSupportedException {
        TestBean tb = (TestBean)super.clone();
        return tb;
    }

    public String toString() {
        Field[] fields = getClass().getDeclaredFields();
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName() + ":[\n");
        for (Field field : fields) {
            field.setAccessible(true);
            sb.append("    ");
            sb.append(field.getName() + " = ");
            try {
                sb.append(field.get(this));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
