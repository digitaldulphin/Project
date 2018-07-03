package ca.mohawk.normanpotts.project;

/**
 * Created by Norman on 2018-06-28.
 */

class Course {

    private int _id;
    private int program;
    private int semesterNum;
    private String courseCode;
    private String courseTitle;
    private String courseDescription;
    private String courseOwner;
    private int optional;
    private int hours;

    public Course(int _id, int program, int semesterNum, String courseCode, String courseTitle,
                  String courseDescription, String courseOwner, int optional, int hours)
    {
        this._id = _id;
        this.program = program;
        this.semesterNum = semesterNum;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.courseDescription =courseDescription;
        this.courseOwner = courseOwner;
        this.optional = optional;
        this.hours = hours;
    }


    public int _id() {
        return this._id;
    }
    public void setID(int _id) {
        this._id = _id;
    }


    public int program() {
        return this.program;
    }
    public void setprogram(int program) {
        this.program = program;
    }


    public int semesterNum() {
        return this.semesterNum;
    }
    public void setsemesterNum(int semesterNum) {
        this.semesterNum = semesterNum;
    }


    public String courseCode() {
        return this.courseCode;
    }
    public void setcourseCode(String courseCode) {
        this.courseCode = courseCode;
    }


    public String courseTitle() {
        return this.courseTitle;
    }
    public void setcourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }


    public String courseDescription() {
        return this.courseDescription;
    }
    public void setcourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }


    public String courseOwner() {
        return this.courseOwner;
    }
    public void setcourseOwner(String courseOwner) {
        this.courseOwner = courseOwner;
    }


    public int optional() {
        return this.optional;
    }
    public void setoptional(int optional) {
        this.optional = optional;
    }


    public int hours() {
        return this.hours;
    }
    public void sethours(int hours) {
        this.hours = hours;
    }
}
