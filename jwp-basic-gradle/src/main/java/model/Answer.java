package model;

import java.sql.Timestamp;

public class Answer {
    private long answerId;
    private String writer;
    private String contents;
    private Timestamp timestamp;
    private long questionId;

    public Answer(String writer, String contents, long questionId) {
        this.writer = writer;
        this.contents = contents;
        this.questionId = questionId;
    }

    public long getAnswerId() {
        return answerId;
    }

    public String getWriter() {
        return writer;
    }

    public String getContents() {
        return contents;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public long getQuestionId() {
        return questionId;
    }
}
