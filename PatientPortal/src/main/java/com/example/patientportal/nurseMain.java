package com.example.patientportal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;

public class nurseMain {

    //add labels
    @FXML
    private Label ageLabel, bpLabel, bpUnitsLabel;

    //add TextFields
    @FXML
    private TextField weightInput, heightFeetInput, heightInchesInput, tempInput, ageInput, bpInput, firstNameInput, lastNameInput;
    
    // add Button Properties
    @FXML
    private MenuButton ageCheck;

    @FXML
    private Button submitButton;

    @FXML
    private RadioButton lbsRadio, kgRadio, newPatientRadio, returnPatientRadio;

    @FXML
    private TextArea infoCheck;

    @FXML
    private ComboBox patientList;

    boolean older = false;

    String thisFirstName, thisLastName;

    public void logout(ActionEvent actionEvent) throws IOException {

        PatientPortal m = new PatientPortal();
        PatientPortal.changeScene("login-selection.fxml");

    }


    public void checkLbs(ActionEvent actionEvent) {

        if(kgRadio.isSelected())
        {

            lbsRadio.setSelected(false);

        }

    }

    public void checkKG(ActionEvent actionEvent) {

        if(lbsRadio.isSelected())
        {

            kgRadio.setSelected(false);

        }

    }

    public void showSubmit(ActionEvent actionEvent)
    {

        submitButton.setVisible(true);

    }

    // *** Handling if Blood Pressure is a necessary field ***
    // ** If the patient is > 12 yrs, get their blood pressure **
    public void openBP(ActionEvent actionEvent) {

        older = true;
        ageLabel.setVisible(true);
        bpLabel.setVisible(true);
        bpUnitsLabel.setVisible(true);
        bpInput.setVisible(true);
        ageInput.setVisible(true);


    }
    
    // ** Deactivate blood pressure fields if patient is < 12 yrs **
    public void noBP(ActionEvent actionEvent) {

        bpLabel.setVisible(false);
        bpUnitsLabel.setVisible(false);
        bpInput.setVisible(false);

        ageLabel.setVisible(true);
        ageInput.setVisible(true);

    }
    // *******************************************************

    // *** Submit information input by Nurse ***
    public void submit(ActionEvent actionEvent) {

        float tempInt;
        // ** Exception handling should program find empty fields **
        if (firstNameInput.getText().isEmpty()) {

            infoCheck.setText("Missing First Name");

        } else if (lastNameInput.getText().isEmpty()) {
            infoCheck.setText("Missing Last Name");
        } else if (weightInput.getText().isEmpty() || (!lbsRadio.isSelected() && !kgRadio.isSelected())) {

            infoCheck.setText("Missing weight Input");

        } else if (heightFeetInput.getText().isEmpty() || heightInchesInput.getText().isEmpty()) {

            infoCheck.setText("Missing height info");

        } else if (tempInput.getText().isEmpty() ) {


            infoCheck.setText("Missing Temperature");

        }

        else if (ageInput.getText().isEmpty()) {

            infoCheck.setText("Missing Age");

        } else if (bpInput.getText().isEmpty() && older) {

            infoCheck.setText("Missing Blood Pressure");

        }
        else if (lbsRadio.isSelected() && kgRadio.isSelected())
        {

            infoCheck.setText("Both KG and Lbs has been Chosen");

        }
        // ****************************************************
        else {

            String tempS = tempInput.getText().toString();
            tempInt = Float.parseFloat(tempS);


            //gather string of all information
            String name = "Name: " + firstNameInput.getText().toString() + " " + lastNameInput.getText().toString() + "\n";
            String weight = "Weight: ";
            
            // Convert weight metric depending on selection
            if (lbsRadio.isSelected()) {

                weight += weightInput.getText().toString() + "lbs.\n";

            } else {

                weight += weightInput.getText().toString() + "kg.\n";

            }

            // Display height information
            String height = "Height: " + heightFeetInput.getText().toString() + "ft. " + heightInchesInput.getText().toString() + "in.\n";

            String temp = "";

            if (tempInt > 1000) {


                temp += "Damn you're hot (☞ﾟヮﾟ)☞  ☜(ﾟヮﾟ☜)\n";


            } else {

                temp = "Temperature: " + tempInput.getText().toString() + "⁰F\n";

            }

            String bp, age = "";

            if (older) {

                bp = "Blood Pressure: " + bpInput.getText().toString() + "\n";
                age = "Age: " + ageInput.getText().toString() + "\n";

            } else {

                age = "Blood Pressure: " + ageInput.getText().toString() + "\n";
                bp = "Age: " + "Patient is not old enough for Blood Pressure Readings\n";

            }

            infoCheck.setText(name + weight + height + temp + bp + age);

            //add all information to the database
            //create SQL database connection
            DatabaseConnect connectNow = new DatabaseConnect();

            //create connection
            Connection connectDb = connectNow.getConnection();




            try {

                String addInfo = "INSERT INTO patientvitals (patientID, firstname, lastname, weight, heightf, heighti, temp," +
                        "age, bloodp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";



                //chosenID now equals the patientID
                PreparedStatement statement = connectDb.prepareStatement(addInfo, Statement.RETURN_GENERATED_KEYS);

                int count = 1;
                statement.setNull(count++, Types.NULL);
                statement.setString(count++, firstNameInput.getText());
                statement.setString(count++, lastNameInput.getText());
                statement.setInt(count++, Integer.parseInt(weightInput.getText()));
                statement.setInt(count++, Integer.parseInt(heightFeetInput.getText()));
                statement.setInt(count++, Integer.parseInt(heightInchesInput.getText()));
                statement.setInt(count++, Integer.parseInt(tempInput.getText()));
                statement.setInt(count++, Integer.parseInt(tempInput.getText()));
                statement.setInt(count++, Integer.parseInt(bpInput.getText()));

                statement.executeUpdate();

                infoCheck.setText("");
                infoCheck.setText("Information has been updated.");


            } catch (Exception e) {

                e.printStackTrace();
                e.getCause();

            }
        }


    }

    public void update(ActionEvent actionEvent) throws IOException
    {
        //add all information to the database
        //create SQL database connection
        DatabaseConnect connectNow = new DatabaseConnect();

        //create connection
        Connection connectDb = connectNow.getConnection();

        int id = 0;

        int chosenID = 0;
        String firstCheck = "";
        String lastCheck = "";

        try
        {

            String getID = "SELECT firstname, lastname, patientID FROM patientlogins";

            Statement stmt = connectDb.createStatement();

                // for a returning patient
                ResultSet checkName = stmt.executeQuery(getID);



                while (checkName.next()) {

                    firstCheck = checkName.getString("firstname");
                    lastCheck = checkName.getString("lastname");


                    if (patientList.getValue().equals(firstCheck + " " + lastCheck)) {

                        //found the correct id of the patient we want
                        chosenID = checkName.getInt("patientID");
                        //stack trace
                        System.out.println("true");

                    }


                }

                System.out.println(chosenID);

                String updateQuery = "UPDATE patientvitals " +
                        "SET firstname='" + firstNameInput.getText() +
                        "', lastname='" + lastNameInput.getText() + "', weight=" +
                        Integer.parseInt(weightInput.getText()) + ", heightf=" +
                        Integer.parseInt(heightFeetInput.getText()) + ", heighti=" +
                        Double.parseDouble(heightInchesInput.getText()) + ", temp=" +
                        Integer.parseInt(tempInput.getText()) + ", age=" + Integer.parseInt(ageInput.getText()) +
                        ", bloodp=" + Integer.parseInt(bpInput.getText()) + " WHERE patientID=" + chosenID;


                PreparedStatement statement = connectDb.prepareStatement(updateQuery);
                statement.executeUpdate();



        }
        catch (Exception e)
        {

            e.printStackTrace();
            e.getCause();

        }

    }


    public void patientSearch(ActionEvent actionEvent) throws IOException{

        //use database to search for patient
        //create SQL database connection
        DatabaseConnect connectNow = new DatabaseConnect();

        //create connection
        Connection connectDb = connectNow.getConnection();

        //use patient accounts rather than vitals for all patients
        String getPatients = "SELECT firstname, lastname FROM patientlogins";

        try
        {

            Statement stmt = connectDb.createStatement();
            ResultSet populate = stmt.executeQuery(getPatients);

            while (populate.next())
            {

                patientList.getItems().addAll(

                        populate.getString("firstname") + " "
                                + populate.getString("lastname")

                );

            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

    }
}
