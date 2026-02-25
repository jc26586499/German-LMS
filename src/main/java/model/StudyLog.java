package model;

import java.sql.Date;

/**
 * Table: study_logs
 * Columns:
 * - log_id (PK, AI)
 * - user_id (FK -> users.user_id)
 * - study_date (date, NOT NULL)
 *
 * UNIQUE(user_id, study_date)
 */
public class StudyLog {

    private int logId;
    private Integer userId;   // DB 可為 NULL
    private Date studyDate;   // DB: study_date (NOT NULL)

    public StudyLog() {}

    public StudyLog(int logId, Integer userId, Date studyDate) {
        this.logId = logId;
        this.userId = userId;
        this.studyDate = studyDate;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(Date studyDate) {
        this.studyDate = studyDate;
    }

    @Override
    public String toString() {
        return "StudyLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", studyDate=" + studyDate +
                '}';
    }
}