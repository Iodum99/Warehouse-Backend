package com.example.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asset_table")
@Data
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;
    private String author;
    @Column(columnDefinition="TEXT")
    private String name;
    @Column(columnDefinition="TEXT")
    private String description;
    @Column(columnDefinition="TEXT")
    private String filePath;
    @ElementCollection
    private List<String> imagePaths;
    private AssetType assetType;
    private LocalDate uploadDate;
    private LocalDate lastModifiedDate;
    private int downloads;
    @ElementCollection
    private List<Integer> userIdLikes;
    @ElementCollection
    private List<String> tags;
    @ElementCollection
    private List<String> extensions;
    private long size;

    @JoinTable(name = "asset_user_id_likes")
    @Formula(value = "(SELECT COUNT(*) FROM asset_user_id_likes a WHERE a.asset_id=id)")
    private int numberOfLikes;

    public Asset(){
        this.uploadDate = LocalDate.now();
        this.downloads = 0;
        this.userIdLikes = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.extensions = new ArrayList<>();
    }
}
