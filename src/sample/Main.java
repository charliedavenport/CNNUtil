package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/**
 * Class for running the application
 *
 * @author Stone Daniel, Charles Davenport
 */
public class Main extends Application {

    private static final SecureRandom RAND = new SecureRandom();

    /**
     * Gets the salt for hashing passwords
     * @return salt for hashing passwords
     * @throws NoSuchAlgorithmException
     *
     * @author Stone Daniel
     */
    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        byte[] salt = "$2b$10$X4kv7j5ZcG39WgogSl16au".getBytes();
        return salt;
    }

    /**
     * Hashes a password based on a salt
     * @param pass password to be hashed
     * @param salt salt for hashing password
     * @return hashed password
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     *
     * @author Stone Daniel
     */
    public String hashPWD(String pass, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, 65536, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();
        return enc.encodeToString(hash);
    }

    /**
     * Handles user login and gui construction from fxml and Controller
     * @param primaryStage stage on which to construct gui
     * @throws Exception when something unexpected happens
     *
     * @author Stone Daniel, Charles Davenport
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        byte[] salt = getSalt();
        System.out.println("Type 'A' to make a new account or 'S' to sign in.");
        Scanner input = new Scanner(System.in);
        while(true){
            String cur_choice = input.nextLine();

            if(cur_choice.equals("A")){
                System.out.println("Enter Desired Account Username");
                String username = input.nextLine().trim();
                System.out.println("Enter Desired Password");
                String password = input.nextLine().trim();
                if(DBInsert.insertUser(username,hashPWD(password,salt))){
                    System.out.println("Thanks. You can sign in now.");
                }

            }
            else if(cur_choice.equals("S")){
                System.out.println("Enter Account Username");
                String username = input.nextLine().trim();
                System.out.println("Enter Password");
                String password = input.nextLine().trim();
                String hashed = hashPWD(password, salt);
                if(DBAccess.isValidUser(username,hashed)){
                    break;
                }
                else
                    System.out.println("No User matches those credentials");
            }
            System.out.println("Type 'A' to make a new account or 'S' to sign in.");
            //input = new Scanner(System.in);
            break;
        }
        Parent root = FXMLLoader.load(getClass().getResource("cnnUtil.fxml"));
        primaryStage.setTitle("CNN Util");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    /**
     * Main method that launches program
     * @param args user input arguments from command line
     *
     * @author Charles Davenport
     */
    public static void main(String[] args) {

        launch(args);
    }
}
