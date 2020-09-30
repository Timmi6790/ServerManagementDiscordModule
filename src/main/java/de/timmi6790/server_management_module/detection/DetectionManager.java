package de.timmi6790.server_management_module.detection;

import de.timmi6790.discord_framework.modules.event.EventModule;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DetectionManager {
    private final List<AbstractDetection> detections = new ArrayList<>();

    private final EventModule eventModule;

    public void addDetections(final AbstractDetection... detections) {
        for (final AbstractDetection detection : detections) {
            this.addDetection(detection);
        }
    }

    public void addDetection(final AbstractDetection detection) {
        this.detections.add(detection);
        this.eventModule.addEventListener(this.eventModule);
    }
}
