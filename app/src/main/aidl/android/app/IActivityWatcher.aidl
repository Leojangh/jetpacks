package android.app;
/**
 * Callback interface to watch the user's traversal through activities.
 * {@hide}
 */
oneway interface IActivityWatcher {
    void activityResuming(int activityId);
    void closingSystemDialogs(String reason);
}