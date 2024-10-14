package example.medCashFlow.util;

import org.springframework.stereotype.Component;

@Component
public class UnmaskInput {

    public String unmaskNumeric(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[^0-9]", "");
    }
}