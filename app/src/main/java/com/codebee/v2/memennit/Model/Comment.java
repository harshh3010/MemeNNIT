package com.codebee.v2.memennit.Model;

public class Comment {
    private String Username;
    private String CommentContent;
    private boolean isExpandable;
    private String id;

    public Comment(String username, String commentContent, boolean isExpandable, String id) {
        Username = username;
        CommentContent = commentContent;
        this.isExpandable = isExpandable;
        this.id = id;
    }

    public Comment() {
    }

    public Comment(String username, String commentContent, boolean isExpandable) {
        Username = username;
        CommentContent = commentContent;
        this.isExpandable = isExpandable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getCommentContent() {
        return CommentContent;
    }

    public void setCommentContent(String commentContent) {
        CommentContent = commentContent;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }
}
