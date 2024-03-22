package com.example.project_v2.board;

import com.example.project_v2.love.Love;
import com.example.project_v2.reply.Reply;
import com.example.project_v2.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Table(name = "board_tb")
@Data
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreationTimestamp
    private Timestamp created_at;

    @OrderBy("id desc")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Love> loves = new ArrayList<>();

    @Builder
    public Board(Integer id, String title, String content, User user, Timestamp created_at, List<Reply> replies, List<Love> loves) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.created_at = created_at;
        this.replies = replies;
        this.loves = loves;
    }
}