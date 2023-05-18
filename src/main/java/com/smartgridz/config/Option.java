package com.smartgridz.config;

/**
 * This class represents a possible option in the application.  It has a
 * string option with a potential default value.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    private String option;
    private String defaultValue;

    public Option(String option) {
        this.option = option;
    }

    @Override
    public String toString() {
        if (defaultValue != null) {
           return "Option: " + option + " Default: " + defaultValue;
        }
        return "Option: " + option + " Default: null";
    }

    public boolean hasDefault() {
        return defaultValue != null ? true : false;
    }
}
