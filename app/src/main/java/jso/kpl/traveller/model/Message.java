package jso.kpl.traveller.model;

import java.io.Serializable;

public class Message implements Serializable {

    int m_no;
    String m_sender;
    String m_receiver;
    String m_msg;
    String m_date;
    String m_card_img;
    String m_sender_img;

    boolean m_is_receive;
    boolean m_is_delete;
    boolean m_is_reply;

    public int getM_no() {
        return m_no;
    }

    public void setM_no(int m_no) {
        this.m_no = m_no;
    }

    public String getM_sender() {
        return m_sender;
    }

    public void setM_sender(String m_sender) {
        this.m_sender = m_sender;
    }

    public String getM_receiver() {
        return m_receiver;
    }

    public void setM_receiver(String m_receiver) {
        this.m_receiver = m_receiver;
    }

    public String getM_msg() {
        return m_msg;
    }

    public void setM_msg(String m_msg) {
        this.m_msg = m_msg;
    }

    public String getM_date() {
        return m_date;
    }

    public void setM_date(String m_date) {
        this.m_date = m_date;
    }

    public String getM_card_img() {
        return m_card_img;
    }

    public void setM_card_img(String m_card_img) {
        this.m_card_img = m_card_img;
    }

    public String getM_sender_img() {
        return m_sender_img;
    }

    public void setM_sender_img(String m_sender_img) {
        this.m_sender_img = m_sender_img;
    }

    public boolean isM_is_receive() {
        return m_is_receive;
    }

    public void setM_is_receive(boolean m_is_receive) {
        this.m_is_receive = m_is_receive;
    }

    public boolean isM_is_delete() {
        return m_is_delete;
    }

    public void setM_is_delete(boolean m_is_delete) {
        this.m_is_delete = m_is_delete;
    }

    public boolean isM_is_reply() {
        return m_is_reply;
    }

    public void setM_is_reply(boolean m_is_reply) {
        this.m_is_reply = m_is_reply;
    }

    @Override
    public String toString() {
        return "Message{" +
                "m_no=" + m_no +
                ", m_sender='" + m_sender + '\'' +
                ", m_receiver='" + m_receiver + '\'' +
                ", m_msg='" + m_msg + '\'' +
                ", m_date='" + m_date + '\'' +
                ", m_card_img='" + m_card_img + '\'' +
                ", m_sender_img='" + m_sender_img + '\'' +
                ", m_is_receive=" + m_is_receive +
                ", m_is_delete=" + m_is_delete +
                '}';
    }
}
