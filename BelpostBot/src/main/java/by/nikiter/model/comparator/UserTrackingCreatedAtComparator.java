package by.nikiter.model.comparator;

import by.nikiter.model.db.entity.UserTrackingEntity;

import java.util.Comparator;

public class UserTrackingCreatedAtComparator implements Comparator<UserTrackingEntity> {

    @Override
    public int compare(UserTrackingEntity o1, UserTrackingEntity o2) {
        return o1.getCreatedAt().compareTo(o2.getCreatedAt());
    }
}
