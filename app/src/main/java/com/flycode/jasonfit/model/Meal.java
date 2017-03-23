package com.flycode.jasonfit.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by acerkinght on 3/22/17.
 */

@Table(name = "Meal", id = "activeId")
public class Meal extends Model {
    @Column(name = "id")
    int id;

    @Column(name = "name")
    String name;

    @Column(name = "type")
    String type;

    @Column(name = "ingredients")
    String ingredients;

    @Column(name = "instructions")
    String instructions;
}
