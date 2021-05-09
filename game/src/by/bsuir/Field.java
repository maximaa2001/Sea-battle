package by.bsuir;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Field extends GridPane {
    private String[][] field;
    private volatile MyButton[] buttons;

    Field(){
        field = new String[10][10];
        buttons = new MyButton[100];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new MyButton(String.valueOf(i));
            int finalI = i;
            buttons[i].setOnMouseClicked(event -> {
                System.out.println(this.buttons[finalI].getLayoutX());
            });
        }

        this.setGridLinesVisible(true);

        for (int i = 0; i < 10 ; i++) {
            this.getColumnConstraints().add(new ColumnConstraints(40));
            this.getRowConstraints().add(new RowConstraints(40));
        }

        int count = 0;
        while (count < buttons.length) {
            for (int i = 0; i < field.length; i++) {
                for (int j = 0; j < field[i].length; j++) {
                    Button button = buttons[count];
                    button.setMaxWidth(Double.MAX_VALUE);
                    button.setMaxHeight(Double.MAX_VALUE);
                    this.add(button, j, i);
                    count++;
                }
            }
        }

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = "0";
            }
        }
    }

    public void getButtonByCoordinate(int row, int column){
        String id = "";
        if(row == 0){
            id += String.valueOf(column);
        }else {
            id += String.valueOf(row);
            id += String.valueOf(column);
        }
        for (int i = 0; i < buttons.length ; i++) {
            if(buttons[i].getMyId().equals(id)){
                buttons[i].getStyleClass().add("aliveButton");
                break;
            }
        }
    }

    public String getIdByCoordinate(int row, int column){
        String id = "";
        if(row == 0){
            id += String.valueOf(column);
        }else {
            id += String.valueOf(row);
            id += String.valueOf(column);
        }
        return id;
    }

    public MyButton getButtonById(String id){
        for (int i = 0; i < buttons.length ; i++) {
            if(buttons[i].getMyId().equals(id)){
                return buttons[i];
            }
        }
        return null;
    }

    public Proxy checkedField(String id){
        int row;
        int column;
        Proxy proxy = null;
        if(id.length() == 1){
            row = 0;
            column = Integer.parseInt(id);
        }else {
            row = Integer.parseInt(String.valueOf(id.charAt(0)));
            column = Integer.parseInt(String.valueOf(id.charAt(1)));
        }
        if(field[row][column].equals("0")){
            proxy = new Proxy(false);
            proxy.setState(State.PAST);
            proxy.setId(id);
        }
        else if(field[row][column].equals("-")){
            proxy = new Proxy(false);
            proxy.setState(State.PAST);
            proxy.setId(id);
        }
        else if(field[row][column].equals("*")){
            field[row][column] = "r";
            proxy = new Proxy(true);
            proxy.setState(State.KILL);
            proxy.setId(id);
            if(column - 1 >= 0){
                if(field[row][column-1].equals("*")){
                    proxy.setState(State.WOUND);
                    return proxy;
                }else if(field[row][column-1].equals("r")){
                    if(isExistLeft(column-1,row)){
                        proxy.setState(State.WOUND);
                    }
                }
            }
            if(column + 1 <= 9){
                if(field[row][column+1].equals("*")){
                    proxy.setState(State.WOUND);
                    return proxy;
                }else if(field[row][column+1].equals("r")){
                    if(isExistRight(column+1,row)){
                        proxy.setState(State.WOUND);
                    }
                }
            }
            if(row - 1 >= 0){
                if(field[row-1][column].equals("*")){
                    proxy.setState(State.WOUND);
                    return proxy;
                }else if(field[row-1][column].equals("r")){
                    if(isExistTop(column,row-1)){
                        proxy.setState(State.WOUND);
                    }
                }
            }
            if(row + 1 <= 9){
                if(field[row+1][column].equals("*")){
                    proxy.setState(State.WOUND);
                    return proxy;
                }else if(field[row+1][column].equals("r")){
                    if(isExistDown(column,row+1)){
                        proxy.setState(State.WOUND);
                    }
                }
            }
        }else {
            proxy = new Proxy(false);
            proxy.setState(State.PAST);
            proxy.setId(id);
        }
        return proxy;
    }

    public String[][] getField() {
        return field;
    }

    public MyButton[] getButtons() {
        return buttons;
    }

    public void setField(String[][] field) {
        this.field = field;
    }

    public void setButtons(MyButton[] buttons) {
        this.buttons = buttons;
    }

    private boolean isExistLeft(int column, int row){
        if(field[row][column].equals("0") || field[row][column].equals("-")){
            return false;
        }else if(field[row][column].equals("r")){
            if((column - 1) >= 0) {
                return isExistLeft(column - 1, row);
            }else {
                return false;
            }
        }else {
            return true;
        }
    }
    private boolean isExistRight(int column, int row){
        if(field[row][column].equals("0") || field[row][column].equals("-")){
            return false;
        }else if(field[row][column].equals("r")){
            if((column+1) <= 9) {
                return isExistLeft(column + 1, row);
            }else {
                return false;
            }
        }else {
            return true;
        }
    }

    private boolean isExistTop(int column, int row){
        if(field[row][column].equals("0") || field[row][column].equals("-")){
            return false;
        }else if(field[row][column].equals("r")){
            if((row - 1) >= 0) {
                return isExistLeft(column, row - 1);
            }else {
                return false;
            }
        }else {
            return true;
        }
    }

    private boolean isExistDown(int column, int row){
        if(field[row][column].equals("0") || field[row][column].equals("-")){
            return false;
        }else if(field[row][column].equals("r")){
            if((row + 1) <= 9) {
                return isExistLeft(column, row + 1);
            }else {
                return false;
            }
        }else {
            return true;
        }
    }
}
