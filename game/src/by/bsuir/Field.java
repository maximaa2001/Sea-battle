package by.bsuir;

import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class Field extends GridPane {
    private String[][] field;
    private MyButton[] buttons;

    Field(){
        field = new String[10][10];
        buttons = new MyButton[100];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new MyButton(String.valueOf(i));
            int finalI = i;
            buttons[i].setOnMouseClicked(event -> {
                System.out.println(this.buttons[finalI].getMyId());
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
}
