package by.nikiter.model.comparator;

import by.nikiter.model.db.entity.UserTrackingEntity;

import java.util.Comparator;

/**
 * Comparator for {@link UserTrackingEntity} that compares by trackingName field
 *
 * @see UserTrackingEntity
 * @author NikiTer
 */
public class UserTrackingNameComparator implements Comparator<UserTrackingEntity> {

    @Override
    public int compare(UserTrackingEntity o1, UserTrackingEntity o2) {
        return o1.getTrackingName().compareTo(o2.getTrackingName());
    }
}
