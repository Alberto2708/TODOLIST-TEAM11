package com.springboot.MyTodoList.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Objects;

@Embeddable
public class SubToDoItemId implements Serializable{
    
    //Attributes
    private static final long serialVersionUID = 1L;

    @Column(name = "TODOITEM_ID")
    private Integer toDoItemId;

    @Column(name = "SUBTODOITEM_ID")
    private Integer subToDoItemId;

    //Constructors

    public SubToDoItemId() {}

    public SubToDoItemId(Integer toDoItemId, Integer subToDoItemId) {
        this.toDoItemId = toDoItemId;
        this.subToDoItemId = subToDoItemId;
    }

    // Getters and Setters

    public Integer getToDoItemId() {
        return toDoItemId;
    }

    public void setToDoItemId(Integer toDoItemId) {
        this.toDoItemId = toDoItemId;
    }

    public Integer getSubToDoItemId() {
        return subToDoItemId;
    }

    public void setSubToDoItemId(Integer subToDoItemId) {
        this.subToDoItemId = subToDoItemId;
    }

    //equals() function override
    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubToDoItemId that = (SubToDoItemId) o;
        return Objects.equals(toDoItemId, that.toDoItemId) && Objects.equals(subToDoItemId, that.subToDoItemId);
    }

    //hashCode() function override

    @Override
    public int hashCode() {
        return Objects.hash(toDoItemId, subToDoItemId);
    }

}
