package com.itcse.sticky_notifications;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itcse.sticky_notifications.utilities.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends Activity {

    @BindView(R.id.spSpinner)
    Spinner spPriority;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.cbSticky)
    CheckBox cbSticky;
    @BindView(R.id.tvHeading)
    TextView tvHeading;

    @BindView(R.id.bCreate)
    Button bCreate;
    @BindView(R.id.bCancel)
    Button bCancel;

    // SharedPreferences are used to store the notification count. This will help in showing new notification everytime.
    private SharedPreferences prefs;

    // Integer denoting current notification id, if exists.
    private int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Getting priority arrays from strings.xml
        final List<String> priorityName = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.priority_name)));

        // Initializing SharedPreferences file
        prefs = getSharedPreferences(Constants.sharedPreferences, MODE_PRIVATE);

        // Centering the screen
        Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        // Make us non-modal, so that others can receive touch events.
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        // Setting layout on Activity
        setContentView(R.layout.activity_home);

        // Using ButterKnife to prevent multiple findViewById() calls
        ButterKnife.bind(this);


        // adding hint to the spinner which won't be there when user clicks on spinner
        priorityName.add(getString(R.string.please_choose_priority));

        // Creating a simple ArrayAdapter with custom view.
        final ArrayAdapter<String> simpleArrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_textview, R.id.tvText, priorityName) {

            /**
             * Function to set hint as the first item of spinner
             * @param position Integer denoting current position
             * @param convertView View containing spinner's dropdown layout
             * @param parent ViewGroup which is parent layout of spinner's dropdown
             * @return View for current position of spinner
             */
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                // Hint should be added to the last position of the spinner
                // If we are at the last position, we will set hint
                if (position == getCount()) {
                    ((TextView) v.findViewById(R.id.tvText)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                // we don't want to display last item since it is used as hint
                return super.getCount() - 1;
            }
        };

        // Adding adapter to the spinner
        spPriority.setAdapter(simpleArrayAdapter);
        // Selecting last item of spinner as default which is hint in our case
        // This will allow us to show hint as default on spinner
        spPriority.setSelection(simpleArrayAdapter.getCount());

        // Checking if we have any intent
        // We will have intent containing notification information only when we click on a current notification
        final Intent intent = getIntent();
        if (intent != null) {
            // Trying to get notification ID from recieved intent. We had passed the notificationID
            // in intent when we created notification. This ID will be used to update or cancel
            // the current notification
            notificationId = intent.getIntExtra(Constants.notificationId, -1);

            // The notificationId will never be -1, if it is, it means that we didn't pass any
            // notification intent to this activity so our app will show create notification screen
            if (notificationId != -1) {
                // Since we received a notificationId, we have clicked on an existing notification
                // Now trying to load data from old notification which was clicked
                final String title = intent.getStringExtra(Constants.title);
                final String description = intent.getStringExtra(Constants.description);
                final int priority = intent.getIntExtra(Constants.priority, 1);
                final boolean makeSticky = intent.getBooleanExtra(Constants.makeSticky, false);

                // Setting old data to the activity and changing dialog title and button names.
                cbSticky.setChecked(makeSticky);
                spPriority.setSelection(priority, true);
                tvHeading.setText(getString(R.string.edit_notification));
                bCreate.setText(getString(R.string.delete));
                bCancel.setText(getString(R.string.update));

                if (!TextUtils.isEmpty(title)) {
                    etTitle.setText(title);
                }
                if (!TextUtils.isEmpty(description)) {
                    etDescription.setText(description);
                }
            }
        }
    }

    /**
     * Function to check if the form is missing some information
     * @return true if data is valid, false if invalid
     */
    private boolean dataValid() {
        boolean dataValid = true;

        if (TextUtils.isEmpty(etTitle.getText())) {
            etTitle.setError(getString(R.string.error_enter_title));
            dataValid = false;
        }
        if (TextUtils.isEmpty(etDescription.getText())) {
            etDescription.setError(getString(R.string.error_enter_description));
            dataValid = false;
        }

        if (spPriority.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.please_choose_priority))) {
            Toast.makeText(HomeActivity.this, getString(R.string.please_choose_priority), Toast.LENGTH_SHORT).show();
            dataValid = false;
        }
        return dataValid;
    }

    /**
     * Function to add new notification or to update a current notification if we know it's notificationId
     */
    private void addNotification() {
        final String title = etTitle.getText().toString().trim();
        final String description = etDescription.getText().toString().trim();
        final int priority = spPriority.getSelectedItemPosition();
        final boolean makeSticky = cbSticky.isChecked();

        // NotificationId will be -1 if it is a new notification, otherwise it is old notification
        // If new notification, creating a new notificationId
        if (notificationId == -1) {
            // Getting new notificationId from SharedPreferences which will start from 1
            notificationId = prefs.getInt(Constants.notificationId, 0) + 1;
            // Updating notificationId in SharedPreferences so that we have new notificationId everytime
            prefs.edit().putInt(Constants.notificationId, notificationId).apply();
        }

        // Creating an intent and adding current form data to it
        // This data will then read in onCreate using getIntent when use clicks on an existing notification.
        // The data saved here will be used to fill form with current values later.
        final Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.title, title);
        intent.putExtra(Constants.description, description);
        intent.putExtra(Constants.priority, priority);
        intent.putExtra(Constants.notificationId, notificationId);
        intent.putExtra(Constants.makeSticky, makeSticky);

        // using this function to retain correct intent when the activity is opened
        // http://stackoverflow.com/a/3168653/1979347
        // Without this function we will get the intent of latest notification
        intent.setAction(Long.toString(System.currentTimeMillis()));

        // Creating a PendingIntent and adding current intent to it.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1000, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Creating a notification using NotificationCompact.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setPriority(getResources().getIntArray(R.array.priority_value)[priority])
                .setOngoing(cbSticky.isChecked())
                .setWhen(0)
                .setContentText(description)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Allows notification to be shown even when user has selected private notification
                .setContentIntent(pendingIntent);

        // Creating a new notification if the notificationId was new or updating an existing notification if id was old
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, builder.build());

    }

    /**
     * Finishing the activity when touched outside. Shamelessly copied from
     * http://stackoverflow.com/questions/4650246/how-to-cancel-an-dialog-themed-like-activity-when-touched-outside-the-window/5831214#5831214
     *
     * @param event Motion event containing information about the touch event
     * @return true or false depending on whether the event was consumed by this activity or not
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    @OnClick({R.id.bCancel, R.id.bCreate})
    void onClick(final View view) {
        switch (view.getId()) {
            case R.id.bCreate:
                // If we are creating new notification, create button's text will be "Create"
                // If we have opened an existing notification, the text will be delete in which case
                // we will remove the notification
                if (bCreate.getText().toString().equalsIgnoreCase(getString(R.string.create))) {
                    // Checking if entered data is valid
                    if (dataValid()) {
                        // If data entered is valid, adding new notification
                        addNotification();
                    } else {
                        // If data is not valid, do nothing.
                        return;
                    }
                } else {
                    // If the text is "Cancel", cancelling the notification using notificationId
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notificationId);
                }
                break;
            case R.id.bCancel:
                // If the notification is new, the cancel button's text will be "Cancel"
                // If the notification is old, the text will be "Update" in which case we will
                // update the notification
                if (bCancel.getText().toString().equalsIgnoreCase(getString(R.string.update))) {
                    addNotification();
                }
        }
        // Whichever button is pressed, finish the activity once the buttons perform their task
        finish();
    }
}
