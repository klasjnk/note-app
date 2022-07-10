package com.example.notes_app.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "trashs")
public class Trash implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="title")
    private String title;

    @ColumnInfo(name="date_time")
    private String dateTime;

    @ColumnInfo(name="subtitle")
    private String subtitle;

    @ColumnInfo(name="note_text")
    private String noteText;

    @ColumnInfo(name="image_path")
    private String imagePath;

    @ColumnInfo(name="color")
    private String color;

    @ColumnInfo(name="web_link")
    private String webLink;

    @ColumnInfo(name="prioritize")
    private boolean prioritize;

    @ColumnInfo(name="tag")
    private String tag;

    @ColumnInfo(name="date_delete")
    private String dateDelete;

    @ColumnInfo(name="day_delete")
    private String dayDelete;

    public String getDayDelete() {
        return dayDelete;
    }

    public void setDayDelete(String dayDelete) {
        this.dayDelete = dayDelete;
    }

    public String getDateDelete() {
        return dateDelete;
    }

    public void setDateDelete(String dateDelete) {
        this.dateDelete = dateDelete;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean getPrioritize() {
        return prioritize;
    }

    public void setPrioritize(boolean prioritize) {
        this.prioritize = prioritize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @Override
    public String toString() {
        return
                title.toUpperCase() +"\n"  +
                        dateTime + "\n" +
                        subtitle + "\n" +
                        noteText + "\n";
    }
}
