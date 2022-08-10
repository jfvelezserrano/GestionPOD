package com.urjc.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditableDataRequest {

    private Integer correctedHours;
    private String observation;
}
