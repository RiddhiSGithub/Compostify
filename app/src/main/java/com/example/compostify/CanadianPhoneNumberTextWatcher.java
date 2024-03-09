package com.example.compostify;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CanadianPhoneNumberTextWatcher implements TextWatcher {

    private EditText editText;
    private boolean isFormatting;

    public CanadianPhoneNumberTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Not needed
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Not needed
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isFormatting) {
            return;
        }

        isFormatting = true;

        String originalString = s.toString();
        String formattedString = formatPhoneNumber(originalString);

        if (!originalString.equals(formattedString)) {
            editText.setText(formattedString);
            editText.setSelection(formattedString.length());
        }

        isFormatting = false;
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Implement your logic here to format the phone number as Canadian phone number
        // For example: (123) 456-7890

        // Example implementation:
        StringBuilder formattedNumber = new StringBuilder();

        // Assuming phone number format is 1234567890
        if (phoneNumber.length() == 10) {
            formattedNumber.append("(")
                    .append(phoneNumber.substring(0, 3))
                    .append(") ")
                    .append(phoneNumber.substring(3, 6))
                    .append("-")
                    .append(phoneNumber.substring(6));
        } else {
            // If the length is not 10, leave it as it is
            formattedNumber.append(phoneNumber);
        }

        return formattedNumber.toString();
    }
}
