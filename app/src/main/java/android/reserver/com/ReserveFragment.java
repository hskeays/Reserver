package android.reserver.com;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReserveFragment extends Fragment {

    private static final String SEAT_COUNT_KEY = "SEAT_COUNT"; // Key for passing seat count to the next activity
    private EditText etvSeatingCountInput; // EditText for seat count input
    private TextView tvFeedbackMsg; // TextView for displaying feedback messages
    private RecyclerView recyclerView; // RecyclerView for displaying time slots
    private TimeSlotAdapter adapter; // Adapter for managing the RecyclerView's data
    private List<TimeSlot> timeSlots; // List to hold time slot data

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserve, container, false); // Inflate the layout

        // Initialize UI components
        etvSeatingCountInput = view.findViewById(R.id.etv_seating_count_input);
        tvFeedbackMsg = view.findViewById(R.id.tv_feedback_msg);
        Button btnSubmit = view.findViewById(R.id.btn_submit);

        // Set up the submit button's click listener
        btnSubmit.setOnClickListener(v -> handleSubmit());

        // Set up a text watcher for the seating count input field
        etvSeatingCountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Update feedback message when the text changes
                updateFeedbackMessage(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed after text changes
            }
        });

        // Set up the RecyclerView if it's available (landscape mode)
        recyclerView = view.findViewById(R.id.rv_time_slots);
        if (recyclerView != null) {
            int orientation = getResources().getConfiguration().orientation;

            // Set the layout manager based on orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Grid layout for landscape
            }

            // Initialize the list of time slots
            initializeTimeSlots();

            // Set up the adapter with a click listener for each time slot
            adapter = new TimeSlotAdapter(timeSlots, position -> {
                TimeSlot selectedTimeSlot = timeSlots.get(position);
                Toast.makeText(getContext(), "Selected: " + selectedTimeSlot.getTime(), Toast.LENGTH_SHORT).show();
            });

            recyclerView.setAdapter(adapter); // Attach the adapter to the RecyclerView
        }

        // Handle keyboard visibility
        etvSeatingCountInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                // Clear focus
                v.clearFocus();
                return true;
            }
            return false;
        });

        return view;
    }

    /**
     * Handles the submit button click event.
     * Validates the seat count input and, if valid, shows a confirmation dialog.
     * If the user confirms, starts the FloorPlanActivity with the seat count.
     */
    private void handleSubmit() {
        String seatingCountText = etvSeatingCountInput.getText().toString().trim();

        if (!seatingCountText.isEmpty()) {
            try {
                int seatingCountInt = Integer.parseInt(seatingCountText);
                if (seatingCountInt > 6 || seatingCountInt < 1) {
                    Toast.makeText(getActivity(), R.string.seat_count_error_msg, Toast.LENGTH_SHORT).show(); // Handle invalid seating count
                    return;
                }

                // Show a confirmation dialog
                showConfirmationDialog(seatingCountInt);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), R.string.seat_count_error_msg, Toast.LENGTH_SHORT).show(); // Handle invalid number format
            }
        } else {
            Toast.makeText(getActivity(), R.string.seat_count_empty_error_msg, Toast.LENGTH_SHORT).show(); // Handle empty input
        }
    }

    /**
     * Shows a confirmation dialog with "Yes" and "No" options.
     * If "Yes" is selected, the FloorPlanActivity is started with the seat count.
     *
     * @param seatingCountInt The seat count to pass to the next activity.
     */
    private void showConfirmationDialog(int seatingCountInt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Action");
        builder.setMessage("Are you sure you want to proceed?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Start FloorPlanActivity with the seat count
            Intent intent = new Intent(getActivity(), FloorPlanActivity.class);
            intent.putExtra(SEAT_COUNT_KEY, seatingCountInt);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()); // Dismiss dialog on "No" selection

        AlertDialog dialog = builder.create();
        dialog.show(); // Display the dialog
    }

    /**
     * Updates the feedback message based on the input seating count.
     *
     * @param input The input text from the seating count EditText.
     */
    private void updateFeedbackMessage(CharSequence input) {
        String seatingCountText = input.toString().trim();
        if (!seatingCountText.isEmpty()) {
            try {
                int seatingCountInt = Integer.parseInt(seatingCountText);
                int feedbackMessageResId = getFeedbackMessageResId(seatingCountInt);
                tvFeedbackMsg.setVisibility(View.VISIBLE);
                tvFeedbackMsg.setText(feedbackMessageResId);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), R.string.seat_count_error_msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            tvFeedbackMsg.setVisibility(View.INVISIBLE); // Hide feedback message if input is empty
        }
    }

    /**
     * Returns the appropriate feedback message resource ID based on the seating count.
     *
     * @param seatingCount The seating count to evaluate.
     * @return The resource ID of the feedback message.
     */
    private int getFeedbackMessageResId(int seatingCount) {
        if (seatingCount >= 1 && seatingCount <= 6) {
            return R.string.empty_string;
        } else if (seatingCount > 6 && seatingCount < 1000) {
            return R.string.seat_count_larger_than_6_msg;
        } else if (seatingCount >= 1000) {
            return R.string.seat_count_gt_1000;
        } else {
            return R.string.seat_count_error_msg;
        }
    }

    /**
     * Initializes the list of time slots.
     */
    private void initializeTimeSlots() {
        timeSlots = new ArrayList<>();
        timeSlots.add(new TimeSlot("4:00 PM"));
        timeSlots.add(new TimeSlot("4:30 PM"));
        timeSlots.add(new TimeSlot("5:00 PM"));
        timeSlots.add(new TimeSlot("5:30 PM"));
        timeSlots.add(new TimeSlot("6:00 PM"));
        timeSlots.add(new TimeSlot("6:30 PM"));
        timeSlots.add(new TimeSlot("7:00 PM"));
        timeSlots.add(new TimeSlot("7:30 PM"));
    }
}
