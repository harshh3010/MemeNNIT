package com.codebee.v2.memennit.Model;

public class Notification {
    private String notificationMessage ;
    private String senderUserEmail ;
    private String recieverUserEmail;

    public Notification() {
    }

    public Notification(String notificationMessage, String senderUserEmail, String recieverUserEmail) {
        this.notificationMessage = notificationMessage;
        this.senderUserEmail = senderUserEmail;
        this.recieverUserEmail = recieverUserEmail;
    }

    public String getRecieverUserEmail() {
        return recieverUserEmail;
    }

    public void setRecieverUserEmail(String recieverUserEmail) {
        this.recieverUserEmail = recieverUserEmail;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getSenderUserEmail() {
        return senderUserEmail;
    }

    public void setSenderUserEmail(String senderUserEmail) {
        this.senderUserEmail = senderUserEmail;
    }
}
