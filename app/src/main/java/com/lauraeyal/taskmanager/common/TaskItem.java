package com.lauraeyal.taskmanager.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Eyal on 11/7/2015.
 */
public class TaskItem {
    private String _description;
    private String _category;
    private String _location;
    private Date _dueTime;
    private String _teamMemebr;
    private String _taskPriority;
    private int _id;
    private int _taskStatus;
    private int _isApproved;

    public TaskItem(String description,String category , String location, Date dueTime, String teamMember,String taskPriority,int status,int isApproved) {
        super();
        _description = description;
        _category = category;
        _location = location;
        _dueTime = dueTime;
        _teamMemebr = teamMember;
        _taskPriority = taskPriority;
        _taskStatus = status;
        _isApproved=isApproved;
    }

    public TaskItem() {
    }

    public String getCategory() {
        return _category;
    }
    public void setCategory(String category) {
        _category = category;
    }

    public String GetLocation() {
        return _location;
    }

    public void SetLocation(String _location) {
        this._location = _location;
    }

    public String GetDueTime() {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.format(_dueTime);

    }

    public void SetDueTime(Date _dueTime) {
        this._dueTime = _dueTime;
    }

    public void setDueTime(String Time) throws ParseException {
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = format.parse(Time);
        _dueTime = date;
    }

    public String get_teamMemebr() {
        return _teamMemebr;
    }

    public void SetTeamMemebr(String _teamMemebr) {
        this._teamMemebr = _teamMemebr;
    }

    public String GetPriority() {
        return _taskPriority;
    }

    public void SetPriority(String _taskPriority) {
        this._taskPriority = _taskPriority;
    }

    public int GetTaskStatus() {return _taskStatus;}

    public void SetTaskStatus(int status) {_taskStatus = status;}

    public void setId(int id) {
        this._id = id;
    }

    public long getId() {
        return _id;
    }

    public void SetTaskApprovle(int isApproved) {_isApproved=isApproved;}

    public int GetTaskApprovle() {return _isApproved;}

    public void SetDescription(String description) { _description = description;}

    public String GetDescription() {return _description;}
}
