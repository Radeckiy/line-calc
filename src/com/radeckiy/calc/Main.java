package com.radeckiy.calc;

import java.util.ArrayDeque;

import static java.lang.Character.isDigit;

public class Main {

    public static void main(String[] args) {
        String str = "2.2^2+(47 - 17)-10+(15*3+9*5)/9*4";
        String exp = createExpression(parseStringMath(str));
        System.out.println(exp);
        System.out.println(str + " = " + calc(exp));
    }

    private static String parseStringMath(String str) {
        //TODO: Добавить регулярку для отброса всего, кроме чисел и мат. знаков
        return str.replaceAll(" ", "");
    }

    private static boolean isDelimiter(char c) {
        return (" =".indexOf(c) != -1);
    }

    private static boolean isOperator(char c) {
        return ("+-/*^()".indexOf(c) != -1);
    }

    private static int getPriority(char c) {
        switch (c) {
            case '(':
                return 0;
            case ')':
                return 1;
            case '+':
                return 2;
            case '-':
                return 3;
            case '*':
            case '/':
                return 4;
            case '^':
                return 5;
            default:
                return 6;
        }
    }

    private static String createExpression(String input) {
        StringBuilder output = new StringBuilder(); //Строка для хранения выражения
        ArrayDeque<Character> operations = new ArrayDeque<>(); //Стек для хранения операторов

        for (int i = 0; i < input.length(); i++) {
            if (isDigit(input.charAt(i))) { // Если цифра
                while (!isDelimiter(input.charAt(i)) && !isOperator(input.charAt(i))) { // Читаем число целиком до разделителя или оператора
                    output.append(input.charAt(i++)); // Добавляем каждую цифру числа к нашей строке и переходим к новому символу

                    if (i == input.length()) // Если символ - последний, то выходим из цикла
                        break;
                }

                output.append(" "); //Дописываем после числа пробел в строку с выражением
                i--; //Возвращаемся на один символ назад, к символу перед разделителем
            }

            if (isOperator(input.charAt(i))) { // Если оператор
                if (input.charAt(i) == '(')
                    operations.push(input.charAt(i)); // Записываем скобку

                else if (input.charAt(i) == ')') { // Если символ - закрывающая скобка, то ыыписываем все операторы до открывающей скобки в строку
                    char s = operations.pop();

                    while (s != '(') {
                        output.append(s).append(' ');
                        s = operations.pop();
                    }
                }

                else {
                    if (operations.peek() != null && (getPriority(input.charAt(i)) <= getPriority(operations.peek()))) //Если очередь не пуста и приоритет нашего оператора меньше или равен приоритету оператора на вершине стека
                            output.append(operations.pop().toString()).append(" "); //То добавляем последний оператор из стека в строку с выражением

                    operations.push(input.charAt(i)); //Если стек пуст, или же приоритет оператора выше - добавляем операторов на вершину стека

                }
            }
        }

        //Когда прошли по всем символам, выкидываем из стека все оставшиеся там операторы в строку
        while (!operations.isEmpty()) {
            output.append(operations.pop()).append(" ");
        }

        return output.toString(); //Возвращаем выражение в постфиксной записи
    }

    private static Double calc(String expression) throws RuntimeException {
        double result;
        ArrayDeque<Double> temp = new ArrayDeque<>(); //Двунаправленная очередь для решения

        for (int i = 0; i < expression.length(); i++) {
            if (isDigit(expression.charAt(i))) { //Если символ - цифра, то читаем все число и записываем наверх очереди
                StringBuilder a = new StringBuilder();

                while (!isDelimiter(expression.charAt(i)) && !isOperator(expression.charAt(i))) { //Пока цифра
                    a.append(expression.charAt(i)); //Добавляем
                    i++;
                    if (i == expression.length()) break;
                }
                temp.push(Double.parseDouble(a.toString())); //Записываем в стек
                i--;
            } else if (isOperator(expression.charAt(i))) { //Если символ - оператор
                Double a = temp.pop();
                Double b = temp.pop(); //Берем два последних значения из стека

                switch (expression.charAt(i)) { // И производим над ними действие, согласно оператору
                    case '+': {
                        result = b + a;
                        break;
                    }
                    case '-': {
                        result = b - a;
                        break;
                    }
                    case '*': {
                        result = b * a;
                        break;
                    }
                    case '/': {
                        result = b / a;
                        break;
                    }
                    case '^': {
                        result = Math.pow(b, a);
                        break;
                    }
                    default:
                        throw new RuntimeException("Неизвестный арифметический знак!");
                }
                temp.push(result); //Результат вычисления записываем обратно в стек
            }
        }
        return temp.peek(); //Забираем результат всех вычислений из стека и возвращаем его
    }
}
