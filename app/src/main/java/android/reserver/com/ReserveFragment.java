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
    private List<TimeSlot> timeSlots; // List to hold time slot data
    private DatabaseHelper dbHelper; // Database helper instance
    private String selectedDay; // Selected day for the reservation
    private String selectedTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserve, container, false); // Inflate the layout

        // Initialize the database helper
        dbHelper = new DatabaseHelper(getContext());

        // Initialize UI components
        etvSeatingCountInput = view.findViewById(R.id.etv_seating_count_input);
        tvFeedbackMsg = view.findViewById(R.id.tv_feedback_msg);

        // Set up buttons for day selection
        Button btnFriday = view.findViewById(R.id.btn_fri);
        Button btnSaturday = view.findViewById(R.id.btn_sat);
        Button btnSunday = view.findViewById(R.id.btn_sun);
        btnFriday.setOnClickListener(v -> selectDay("Friday"));
        btnSaturday.setOnClickListener(v -> selectDay("Saturday"));
        btnSunday.setOnClickListener(v -> selectDay("Sunday"));

        // Set up the submit button's click listener
        Button btnSubmit = view.findViewById(R.id.btn_submit);
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

        // Set up the RecyclerView for displaying time slots
        setupRecyclerView(view);

        // Handle keyboard visibility
        etvSeatingCountInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                // Clear focus from the EditText
                v.clearFocus();
                return true;
            }
            return false;
        });

        return view;
    }

    // Method to handle day selection
    private void selectDay(String day) {
        selectedDay = day;
        Toast.makeText(getActivity(), selectedDay + " selected", Toast.LENGTH_SHORT).show();
    }

    private void handleTimeSlotSelection(String selectedTime) {
        this.selectedTime = selectedTime; // Save the selected time
        String seatingCountText = etvSeatingCountInput.getText().toString().trim();

        if (!seatingCountText.isEmpty()) {
            try {
                int seatingCountInt = Integer.parseInt(seatingCountText);
                if (seatingCountInt > 6 || seatingCountInt < 1) {
                    Toast.makeText(getActivity(), R.string.seat_count_error_msg, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedDay == null) {
                    Toast.makeText(getActivity(), "Please select a day", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Query available seats based on selected day and time
                List<Seat> availableSeats = dbHelper.getAvailableSeats(selectedDay, selectedTime, seatingCountInt);
                showAvailableSeatsDialog(availableSeats);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), R.string.seat_count_error_msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.seat_count_empty_error_msg, Toast.LENGTH_SHORT).show();
        }
    }

    // Method to display a dialog with available seats
    private void showAvailableSeatsDialog(List<Seat> availableSeats) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Available Seats");

        StringBuilder message = new StringBuilder();
        if (availableSeats.isEmpty()) {
            message.append("No available seats.");
        } else {
            for (Seat seat : availableSeats) {
                message.append(seat.getName()).append(" (").append(seat.getType()).append(")\n");
            }
        }

        builder.setMessage(message.toString());
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss()); // Dismiss dialog on "OK" selection
        AlertDialog dialog = builder.create();
        dialog.show(); // Display the dialog
    }

    // Method to handle submission of the reservation
    private void handleSubmit() {
        String seatingCountText = etvSeatingCountInput.getText().toString().trim();

        if (!seatingCountText.isEmpty()) {
            try {
                int seatingCountInt = Integer.parseInt(seatingCountText);
                if (seatingCountInt > 6 || seatingCountInt < 1) {
                    Toast.makeText(getActivity(), R.string.seat_count_error_msg, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show a confirmation dialog before proceeding
                showConfirmationDialog(seatingCountInt);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), R.string.seat_count_error_msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.seat_count_empty_error_msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmationDialog(int seatingCountInt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Action");
        builder.setMessage("Are you sure you want to proceed with " + seatingCountInt + " seats on " + selectedDay + " at " + selectedTime + "?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Start FloorPlanActivity with the seat count
            Intent intent = new Intent(getActivity(), FloorPlanActivity.class);
            intent.putExtra(SEAT_COUNT_KEY, seatingCountInt);
            intent.putStringArrayListExtra("UNAVAIL_SEAT_NAMES", dbHelper.getUnavailableSeatsNames(selectedDay, selectedTime));
            intent.putExtra("SELECTED_TIME", selectedTime); // Pass selected time to the next activity
            intent.putExtra("SELECTED_DAY", selectedDay); // Pass selected day to the next activity
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show(); // Display the confirmation dialog
    }

    // Method to update the feedback message based on the input
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

    // Method to determine the appropriate feedback message resource ID based on seating count
    private int getFeedbackMessageResId(int seatingCount) {
        if (seatingCount >= 1 && seatingCount <= 6) {
            return R.string.empty_string; // No feedback message for valid input
        } else if (seatingCount > 6 && seatingCount < 1000) {
            return R.string.seat_count_larger_than_6_msg; // Feedback for exceeding maximum seats
        } else if (seatingCount >= 1000) {
            return R.string.seat_count_gt_1000; // Feedback for excessively large count
        } else {
            return R.string.seat_count_error_msg; // General error message
        }
    }

    // Method to set up the RecyclerView and its layout manager
    private void setupRecyclerView(View view) {
        // Find the RecyclerView in the layout
        RecyclerView recyclerView = view.findViewById(R.id.rv_time_slots);
        if (recyclerView != null) {
            int orientation = getResources().getConfiguration().orientation;

            // Set the layout manager based on the orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Grid layout for landscape
            }

            // Initialize the list of time slots
            initializeTimeSlots();

            // Set up the adapter with a click listener for each time slot
            TimeSlotAdapter adapter = new TimeSlotAdapter(timeSlots, position -> {
                TimeSlot selectedTimeSlot = timeSlots.get(position);
                handleTimeSlotSelection(selectedTimeSlot.getTime());
            });

            recyclerView.setAdapter(adapter); // Attach the adapter to the RecyclerView
        }
    }

    // Method to initialize the time slots available for selection
    private void initializeTimeSlots() {
        timeSlots = new ArrayList<>();
        // Add predefined time slots
        timeSlots.add(new TimeSlot("4:00"));
        timeSlots.add(new TimeSlot("4:30"));
        timeSlots.add(new TimeSlot("5:00"));
        timeSlots.add(new TimeSlot("5:30"));
        timeSlots.add(new TimeSlot("6:00"));
        timeSlots.add(new TimeSlot("6:30"));
        timeSlots.add(new TimeSlot("7:00"));
        timeSlots.add(new TimeSlot("7:30"));
    }
}
