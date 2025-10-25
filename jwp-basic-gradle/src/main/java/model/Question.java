package model;

import java.util.Date;
import java.util.Objects;

public class Question {
    private long questionId;
    private String writer;
    private String title;
    private String contents;
    private Date createdDate;
    private int countOfAnswer;

    public Question(long questionId, String writer, String title, String contents, Date createdDate, int countOfAnswer) {
        this.questionId = questionId;
        this.writer = writer;
        this.title = title;
        this.contents = contents;
        this.createdDate = createdDate;
        this.countOfAnswer = countOfAnswer;
    }

    //아직 DB에 저장되지 않은 Question 객체를 생성하기 위해 임시값으로 0 넣기
    //todo: countOfAnswers 추가 로직 구현하기
    public Question(String writer, String title, String contents){
        this(0, writer, title, contents, new Date(), 0);
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public long getTimeFromCreatedDate() {
        return this.createdDate.getTime();
    }

    public int getCountOfAnswer() {
        return countOfAnswer;
    }

    public String getTitle() {
        return title;
    }


    //테스트 assertEquals로 인해 추가
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(questionId, question.questionId) &&
                Objects.equals(title, question.title) &&
                Objects.equals(contents, question.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, title, contents);
    }
}
