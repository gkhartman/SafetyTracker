SafetyTracker
=============
Fixed beginActivity enablegps bug by restructuring the onCreate, onStart(), onResume(), onPause(), onStop(), onDestroy().
The application engine now waits for the user to decide if he wants to enable gps or not before starting.
Deleted CalibrateScreen class and layout (note: not the calibrate we use in the engine).
Updated UI colors, all of the text is now blue.
Fine Tuned some things.
