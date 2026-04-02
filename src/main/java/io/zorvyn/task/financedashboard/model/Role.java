package io.zorvyn.task.financedashboard.model;

public enum Role {
    VIEWER,    // Can only view dashboard data
    ANALYST,   // Can view records and access insights
    ADMIN      // Can create, update, and manage records and users
}
