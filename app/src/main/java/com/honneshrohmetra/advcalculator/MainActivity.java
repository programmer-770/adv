package com.honneshrohmetra.advcalculator;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private int[] numericButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine};

    private int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};

    private EditText txtScreen;

    private boolean lastNumeric;

    private boolean stateError;


    private boolean lastDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find the EditText
        this.txtScreen = (EditText) findViewById(R.id.txtScreen);
        EditText editText = (EditText)findViewById(R.id.txtScreen);
        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });
        setNumericOnClickListener();

        setOperatorOnClickListener();
    }

   private void setNumericOnClickListener() {
       View.OnClickListener listener = new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Button button = (Button) v;
               if (stateError) {

                   txtScreen.setText(button.getText());
                   stateError = false;
               }
               if(txtScreen.getSelectionStart()!=txtScreen.getText().toString().length())
               {
                   txtScreen.getText().insert(txtScreen.getSelectionStart(), button.getText());
               }
               else {

                   txtScreen.append(button.getText());
               }

               lastNumeric = true;
           }
       };
        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(listener);
        }
    }


    private void setOperatorOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNumeric && !stateError) {
                    Button button = (Button) v;
                    txtScreen.append(button.getText());
                    lastNumeric = false;
                    lastDot = false;
                }
            }
        };
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(listener);
        }
        findViewById(R.id.btnDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNumeric && !stateError && !lastDot) {
                    txtScreen.append(".");
                    lastNumeric = false;
                    lastDot = true;
                }
            }
        });
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtScreen.setText("");
                lastNumeric = false;
                stateError = false;
                lastDot = false;
            }
        });
        findViewById(R.id.btnLplus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLplus();
                EditText et = (EditText)findViewById(R.id.txtScreen);
                et.setSelection(et.getText().length());
            }
        });
      /*  findViewById(R.id.btnLminus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLminus();
                EditText et = (EditText)findViewById(R.id.txtScreen);
                et.setSelection(et.getText().length());
            }
        });*/
        findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = txtScreen.getText().toString();
                if(text.length()!=0)
                {
                    int pos=txtScreen.getSelectionStart();
                    txtScreen.setText(text.substring(0,pos-1)+text.substring(pos,text.length()));
                    txtScreen.setSelection(pos-1);
                }
                lastNumeric = false;
                stateError = false;
                lastDot = false;
            }
        });
        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEqual();
                EditText et = (EditText)findViewById(R.id.txtScreen);
                et.setSelection(et.getText().length());
            }
        });
    }


    private void onEqual() {

        if (lastNumeric && !stateError) {
            String txt = txtScreen.getText().toString();

            Expression expression = new ExpressionBuilder(txt).build();
            try {
                double result = expression.evaluate();
                if(Math.ceil(result)==result && Double.toString(result).indexOf('E')==-1 && Double.toString(result).indexOf('I')==-1) {
                    String res=Double.toString(result);
                    int len=res.length();
                    String lite=res.substring(0,len-2);
                    txtScreen.setText(lite);
                }
                else
                { txtScreen.setText(Double.toString(result));}

            } catch (ArithmeticException ex) {
                txtScreen.setText("Error");
                stateError = true;
                lastNumeric = false;
            }
        }
    }

    private void onLplus() {
        String plus = txtScreen.getText().toString();
        int pin=plus.indexOf('+');

        if (lastNumeric && !stateError && pin!=-1) {
            String txt = txtScreen.getText().toString();
            int index=txt.indexOf('+');
            String num1=txt.substring(0,index);
            String num2=txt.substring(index+1);
            try {
                int min = (num1.length() < num2.length() ?  num1.length() : num2.length());
                int max = (num1.length() < num2.length() ?  num2.length() : num1.length());

                int n1[]= new int [max];
                int n2[]= new int [max];

                for(int i=0;i<num1.length();i++)
                {
                    n1[i]=num1.charAt(num1.length()-1-i)-48;
                }
                for(int i=0;i<num2.length();i++)
                {
                    n2[i]=num2.charAt(num2.length()-1-i)-48;
                }
                int carry=0;

                int sum[]=new int[max+1];
                int k=0;
                for(k=0;k<max;k++)
                {
                    sum[k]=(n1[k]+n2[k]+carry)%10;

                    if((n1[k]+n2[k]+carry)>=10)
                        carry=1;
                    else
                        carry=0;
                }
                sum[max]=carry;
                StringBuilder summer = new StringBuilder();
                if(carry==0)
                for(int j=max-1;j>=0;j--)
                    summer.append(sum[j]);
                else
                    for(int j=max;j>=0;j--)
                        summer.append(sum[j]);
                    txtScreen.setText(summer.toString());
            }
            catch (ArithmeticException ex) {
                txtScreen.setText("Error");
                stateError = true;
                lastNumeric = false;
            }
        }
    }




    /*private void onLminus() {
        String plus = txtScreen.getText().toString();
        int pin=plus.indexOf('-');

        if (lastNumeric && !stateError && pin!=-1) {
            String txt = txtScreen.getText().toString();
            int index=txt.indexOf('-');
            String num1=txt.substring(0,index);
            String num2=txt.substring(index+1);
            try {
                int min = (num1.length() < num2.length() ?  num1.length() : num2.length());
                int max = (num1.length() < num2.length() ?  num2.length() : num1.length());
                int number1[]= new int [max];
                int number2[]= new int [max];

                for(int i=0;i<num1.length();i++)
                {
                    number1[i]=num1.charAt(num1.length()-1-i)-48;
                }
                for(int i=0;i<num2.length();i++)
                {
                    number2[i]=num2.charAt(num2.length()-1-i)-48;
                }
                boolean swap = false;
                for(int j = 0; j < number1.length; j++){
                    if(number2[j] > number1[j]){
                        swap = true;
                        int temp[] = number1;
                        number1 = number2;
                        number2 = temp;
                        break;
                    } else if(number1[j] > number2[j]){
                        break;
                    }
                }

                int[] result = new int[number1.length];
                int carry = 0;
                for(int i = number1.length - 1; i >= 0; i--) {
                    int newDigit;
                    if(number1[i] - number2[i] >=0)
                    { newDigit=number1[i]-number2[i];}
                    else {
                        newDigit=number1[i]-number2[i]+10;
                        number1[i+1]=number1[i+1]-1;
                    }
                        //newDigit=number1[i]-number2[i];
                    /*newDigit += carry;
                    if (newDigit >= 10) {
                        carry = 1;
                        newDigit -= 10;
                    } else if (newDigit < 0) {
                        carry = -1;
                        newDigit += 10;
                    } else {
                        carry = 0;
                    }
                    result[i] = newDigit;
                }
                 //String str=Arrays.toString(result);
                StringBuilder str = new StringBuilder();
                for(int j=number1.length;j>=0;j--)
                        str.append(result[j]);
                txtScreen.setText(str.toString());
            }
               /* String resultString = "";
                for(int j = 0; j <result.length; j++){
               resultString += (result[j] + "");
                }

                if(carry == 1) {
                    txtScreen.setText("1" + resultString);
                } else if(carry == -1 || swap) {
                    txtScreen.setText("-" + resultString);
                } else {
                    txtScreen.setText(resultString);
                }
            }
            catch (ArithmeticException ex) {
                txtScreen.setText("Error");
                stateError = true;
                lastNumeric = false;
            }
        }
    }*/

}