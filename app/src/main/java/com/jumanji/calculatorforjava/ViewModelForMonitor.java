package com.jumanji.calculatorforjava;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Objects;

public class ViewModelForMonitor extends ViewModel {

    private final MutableLiveData<String> monitor;

    private LinkedList<StringBuilder> numStack;
    private LinkedList<Character> optStack;

    private final DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
    private boolean newFlag = false;//判定是否开启新运算

    public ViewModelForMonitor(){
        monitor = new MutableLiveData<>();
        numStack = new LinkedList<>();
        optStack = new LinkedList<>();
    }

    /**
     * 显示器显示，与UI绑定
     */
    public MutableLiveData<String> monitorDisplay() {
        if(monitor.getValue()==null){
            monitor.setValue("0");
        }
        Log.d("a","执行monitorDisplay,显示结果为"+monitor.getValue());
        return monitor;
    }

    /**
     * 刷新显示值，触发变动
     */
    public void fresh() {
        StringBuilder ans = numStack.peekLast();
        if(ans==null){
            monitor.setValue("0");
        } else{
            monitor.setValue(adaptDecimal(ans.toString()));
        }
        Log.d("a","执行fresh,刷新后结果为"+monitor.getValue());
    }

    /*
    处理下小数点
    如果小数点是没用的，删掉，后续也能处理用户选择的小数点位数，后面考虑加一下
     */
    private String adaptDecimal(String str) {
        if(str.equals("")) return "0";
        return decimalFormat.format(Double.valueOf(str));
    }

    /**
     * 键入数字
     */
    public void inputNum(int n) {
        if(newFlag){//运算完成时会展示结果，但再键入数字是想开启新运算，如果在键入数字前键入了运算符
                    //则是继续旧运算，开启新运算要清掉数字栈
            numStack.pollLast();
            newFlag = false;
        }
        if(numStack.peek()==null){
            numStack.addLast(new StringBuilder());
        }
        Objects.requireNonNull(numStack.peekLast()).append(n);
        Log.d("a","执行inputNum,添加数字"+n);
        fresh();
    }

    /**
     * 键入操作符
     */
    public void inputOpt(char c) {
        if(numStack.isEmpty()||Objects.requireNonNull(numStack.peekLast()).toString().equals("")){
            //无效的字符，比如一开始没键入数字就先键入字符，或连续键入字符
            return;
        }
        if(!optStack.isEmpty()){
            while(!optStack.isEmpty()&&priority(c, Objects.requireNonNull(optStack.peekLast()))){
                computeOne();
            }
            fresh();
        }
        //键入字符后需要新数字
        newFlag = false;
        numStack.addLast(new StringBuilder());
        optStack.addLast(c);
        Log.d("a","执行inputOpt,添加运算符"+c);
    }

    /*
    比较符号优先级,新加入符号优先级如果小于等于栈顶元素，就true，比如**，+*
    当前简单，只有两级，复杂以后有别的运算级时可以改这里
     */
    private boolean priority(char c, char peek){
        if(c=='+'||c=='-'){
            return true;
        } else if(c=='*'||c=='/'){
            return peek != '+' && peek != '-';
        } else return true;
    }

    /**
     * 清空全部 A/C
     */
    public void clear() {
        numStack = new LinkedList<>();
        optStack = new LinkedList<>();
        Log.d("a","执行clear");
        fresh();
    }

    /*
     * 执行一次计算，更新双栈
     */
    private void computeOne() {
        char opt = Objects.requireNonNull(optStack.pollLast());
        String top = Objects.requireNonNull(numStack.pollLast()).toString();
        double b = top.equals("")? Double.parseDouble(Objects.requireNonNull(numStack.pollLast()).toString()): Double.parseDouble(top);
        double a = numStack.isEmpty()? b: Double.parseDouble(Objects.requireNonNull(numStack.pollLast()).toString());
        double ans = 0.0;
        switch (opt){
            case '+': ans = a+b; break;
            case '-': ans = a-b; break;
            case '*': ans = a*b; break;
            case '/': ans = a/b; break;
        }
        numStack.addLast(new StringBuilder(String.valueOf(ans)));
        Log.d("a","完成一次计算"+a+opt+b+"="+ans);
    }

    /**
     * 完成全部计算，显示结果
     */
    public void computeAll() {
        while(numStack.size()>1){
            computeOne();
        }
        Log.d("a","全部计算已完成");
        fresh();
        newFlag = true;
//        numStack.pollLast();
//        numStack.addLast(new StringBuilder());
    }

    /**
     * 正负操作
     */
    public void negative() {
        if(!numStack.isEmpty()){
            StringBuilder after = new StringBuilder(
                    String.valueOf(
                            Double.parseDouble(
                                    Objects.requireNonNull(numStack.pollLast()).toString())*(-1.0)));
            numStack.addLast(after);
            Log.d("a","符号变动");
            fresh();
        }
    }

}
