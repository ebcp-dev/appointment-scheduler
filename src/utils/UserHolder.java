package utils;

import models.User;

/** Singleton class to hold current user's data. */
public final class UserHolder{
    /** Class fields. */
    private User user;
    /** Instance shouldn't be changed. */
    private final static UserHolder INSTANCE = new UserHolder();
    /** Constructor. */
    private UserHolder() {}
    /** Get current user's instance.
     * @return returns UserHolder object*/
    public static UserHolder getInstance() {
        return INSTANCE;
    }
    /** Set User to logged in user.
     * @param u User object*/
    public void setUser(User u) {
        this.user = u;
    }
    /** Get current User object.
     * @return returns User object*/
    public User getUser() {
        return this.user;
    }
}
