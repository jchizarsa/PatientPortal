package com.example.patientportal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class createAccount {

    @FXML
    private TextField firstName, lastName, month, date, year, phoneNum, usernameField, passwordField;

    @FXML
    private TextArea verifyArea;

    private String information;

    public void exitPage(ActionEvent actionEvent) throws IOException {

        PatientPortal m = new PatientPortal();
        PatientPortal.changeScene("login-selection.fxml");

    }


    public void submitButton(ActionEvent actionEvent) throws IOException {

        information = "First: " + firstName.getText().toString() + "\nLast: " + lastName.getText().toString() +
                "\nDate of Birth: " + month.getText().toString() + "/" + date.getText().toString() + "/"
                + year.getText().toString() + "\nPhone Number: " + phoneNum.getText().toString() + "\n\nUsername: " + usernameField.getText() +
                "\nPassword: " + passwordField.getText();

        verifyArea.setText(information);



    }

    public void createAccount(ActionEvent actionEvent) throws SQLException, IOException {


        //create SQL database connection
        DatabaseConnect connectNow = new DatabaseConnect();

        //create connection
        Connection connectDb = connectNow.getConnection();

        int id = 0;

        try {


            String query = "INSERT INTO patientlogins (patientID, username, password, firstname, lastname, phonenumber, medh, imm) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connectDb.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);


            int count = 1;
            statement.setString(count++, null);
            statement.setString(count++, usernameField.getText());
            statement.setString(count++, passwordField.getText());
            statement.setString(count++, firstName.getText());
            statement.setString(count++, lastName.getText());
            statement.setString(count++, phoneNum.getText());
            //update history information. This will be empty.
            statement.setString(count++, "This patient has no medical history.");
            statement.setString(count++, "This patient has no immunization history.");

            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();

            String gatherID = "SELECT patientID FROM patientlogins WHERE username ='" + usernameField.getText() + "'";

            PreparedStatement stmt = connectDb.prepareStatement(gatherID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {

                id = rs.getInt("patientID");

            }

            PatientPortal.firstName = firstName.getText();
            PatientPortal.lastName = lastName.getText();
            PatientPortal.phonenumber = phoneNum.getText();
            PatientPortal.medical_history = "N/A";
            PatientPortal.immunization_history = "N/A";
            PatientPortal.patientID = id;


        }
        catch (Exception e)
        {

            e.printStackTrace();
            e.getCause();

        }

        PatientPortal m = new PatientPortal();

        PatientPortal.changeScene("patientMainPage.fxml");
    }
}
