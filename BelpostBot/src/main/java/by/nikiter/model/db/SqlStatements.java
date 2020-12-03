package by.nikiter.model.db;

public class SqlStatements {

    public final static String GET_TRACKING_BY_USER = "SELECT t FROM TrackingEntity t " +
            "JOIN t.users ute " +
            "JOIN ute.user u " +
            "WHERE u.username=:username AND t.number=:number";

    public final static String GET_ALL_TRACKINGS_BY_USER = "SELECT t FROM TrackingEntity t " +
            "JOIN t.users ute " +
            "JOIN ute.user u " +
            "WHERE u.username=:username";

    public final static String GET_TRACKING_NAME_BY_USER = "SELECT ute.trackingName FROM TrackingEntity t " +
            "JOIN t.users ute " +
            "JOIN ute.user u " +
            "WHERE u.username=:username AND t.number=:number";

    public final static String GET_USER_STATE = "SELECT u.state FROM UserEntity u " +
            "WHERE u.username=:username";
}