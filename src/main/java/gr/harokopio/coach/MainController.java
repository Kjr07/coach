package gr.harokopio.coach.ui;

import gr.harokopio.coach.io.TcxActivityReader;
import gr.harokopio.coach.model.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.File;
import java.net.URL;
import java.util.List;

public class MainController {

    @FXML
    private Label statusLabel;

    @FXML
    private void onLoadFiles() {
        statusLabel.setText("Clicked: Load TCX Files (next step: FileChooser)");
    }

    @FXML
    private void onLoadSample() {
        try {
            // Βρίσκουμε το sample.tcx από τα resources
            URL url = getClass().getResource("/sample.tcx");
            if (url == null) {
                statusLabel.setText("ERROR: Δεν βρέθηκε /sample.tcx στα resources");
                return;
            }

            File tcxFile = new File(url.toURI());

            // Καλούμε τον reader σου
            TcxActivityReader reader = new TcxActivityReader();
            List<Activity> activities = reader.readAll(tcxFile);

            if (activities.isEmpty()) {
                statusLabel.setText("No activities found in sample.tcx");
                return;
            }

            Activity activity = activities.get(0);

            // Δείχνουμε ένα μικρό αποτέλεσμα
            statusLabel.setText(
                    "Loaded: " + activity.getSport()
                            + " | start: " + activity.getStartTime()
                            + " | laps: " + activity.getLaps().size()
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("ERROR loading sample.tcx: " + ex.getMessage());
        }
    }
}

