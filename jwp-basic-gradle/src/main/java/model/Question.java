package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Question {
    private long questionId;
    private String writer;
    private String title;
    private String contents;
    private Date timestamp;
    private int countOfAnswer;

    public Question(long questionId, String writer, String title, String contents, Date timestamp, int countOfAnswer) {
        this.questionId = questionId;
        this.writer = writer;
        this.title = title;
        this.contents = contents;
        this.timestamp = timestamp;
        this.countOfAnswer = countOfAnswer;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getWriter() {
        return writer;
    }

    public String getContents() {
        return contents;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getCountOfAnswer() {
        return countOfAnswer;
    }

    public String getTitle() {
        return title;
    }
}
