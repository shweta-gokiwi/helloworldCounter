package com.hello.helloworld.entity;
import jakarta.persistence.*;


@Entity
@Table(name = "helloworld")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int counter;

    public User() {
    }

    public User(String name, int counter){
        this.name = name;
        this.counter = counter;
    }

    public long getId(){
        return id;
    }

    public void setId(Long id){
        this.id= id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getCounter(){
        return counter;
    }

    public void setCounter(int counter){
        this.counter = counter;
    }


}
