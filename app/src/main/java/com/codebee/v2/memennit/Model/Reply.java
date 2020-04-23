package com.codebee.v2.memennit.Model;

public class Reply {
    private String Username;
    private String ReplyContent;
    private String id;
    private String commentId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Reply() {
    }

    public Reply(String username, String replyContent) {
        Username = username;
        ReplyContent = replyContent;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getReplyContent() {
        return ReplyContent;
    }

    public void setReplyContent(String replyContent) {
        ReplyContent = replyContent;
    }
}
