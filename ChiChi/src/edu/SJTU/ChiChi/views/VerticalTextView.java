package edu.SJTU.ChiChi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView for vertical character<br>.
 * <p/>
 * Attribute "layout_height" is not supported in layout.xml and you have to set height in java by method setHeight(int pixels)
 *
 * @author Pumpkin Leaf
 * @version 1.0.0
 */
public class VerticalTextView extends TextView {

    char[][] matrix;
    String columnSpacing = " ";
    int height;

    public VerticalTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public VerticalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        // TODO Auto-generated method stub
        super.setText(text, type);
    }

    public void setColumnSpacing(int columnSpacing) {
        for (int i = 1; i < columnSpacing; i++) {
            this.columnSpacing += " ";
        }
    }

    public void setVerticalText(CharSequence text, boolean rightToLeft) {
        int row;
        int column;
        int[] rowAndColumn = new int[2];
        String verticalText;

        rowAndColumn = measureMatrix(text.toString());
        row = rowAndColumn[0];
        column = rowAndColumn[1];

        setMatrix(text.toString(), row, column, rightToLeft);

        verticalText = verticalTextString(row, column);

        this.setText(verticalText);

    }

    private int[] measureMatrix(String text) {
        // int height = this.getHeight();
        int textLength = text.length();

        int height = this.getMinHeight();
        int paddingTop = this.getPaddingTop();
        int paddingBottom = this.getPaddingBottom();
        double lineSpacingMultiplier = this.getLineSpacingMultiplier();
        double textSize = this.getTextSize();

        int row;
        int column;

        row = (int) ((height - paddingTop - paddingBottom) / (lineSpacingMultiplier + textSize));
        column = (int) (textLength / row) + 1;

        for (int i = 0; i < textLength; i++) {
            if (text.charAt(i) == '\n') {
                column++;
            }
        }
        int result[] = new int[2];

        result[0] = row;
        result[1] = column;
        return result;
    }

    private void setMatrix(String text, int row, int column, boolean rightToLeft) {
        matrix = new char[row][column];

        int x, y;
        x = 0;
        y = 0;

        if (!rightToLeft) {
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) != '\n') {
                    matrix[x][y] = text.charAt(i);
                    x++;
                    if (x == row - 1) {
                        y++;
                        x = 0;
                    }
                } else {
                    y++;
                    x = 0;
                }
            }

        } else {
            x = 0;
            y = column - 1;
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) != '\n') {
                    matrix[x][y] = text.charAt(i);
                    x++;
                    if (x == row) {
                        y--;
                        x = 0;
                    }
                } else {
                    y--;
                    x = 0;
                }
            }
        }

    }

    private String verticalTextString(int row, int column) {
        String str = "";
        int x, y;
        int columnStartPoint;
        int columnEndPoint;

        x = 0;
        y = 0;
        columnStartPoint = 0;
        columnEndPoint = column;
        //
        while (y < column) {
            if (matrix[x][y] == '\0') {
                x++;
                if (x == row) {
                    columnStartPoint++;
                    x = 0;
                    y++;
                }
            } else
                break;
        }

        x = 0;
        y = column - 1;
        //
        while (y >= columnStartPoint) {
            if (matrix[x][y] == '\0') {
                x++;
                if (x == row) {
                    columnEndPoint--;
                    x = 0;
                    y--;
                }
            } else
                break;
        }

        x = 0;
        y = columnStartPoint;
        while (x < row) {
            if (matrix[x][y] != '\0') {
                str += matrix[x][y] + columnSpacing;
                y++;
                if (y == columnEndPoint) {
                    y = columnStartPoint;
                    x++;
                    str += "\n";
                }
            } else {
                y++;
                if (y == columnEndPoint) {
                    y = columnStartPoint;
                    x++;
                    str += "\n";
                } else
                    str += "    " + columnSpacing;
            }
        }

        return str;
    }

}
