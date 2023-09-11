package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {//функционал главной страницы

    private EditText user_field;// поле для ввода текста
    private Button main_btn;// кнопка
    private TextView result_info;// текстовая надпись 

    @Override
    protected void onCreate(Bundle savedInstanceState) {// задаем начальную установку параметров при инициализации активности
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);// обращение к полю для ввода текста
        main_btn = findViewById(R.id.main_btn);// обращение к кнопке
        result_info = findViewById(R.id.result_info);// обращение к текстовой надписи

        main_btn.setOnClickListener(new View.OnClickListener() {// обработчик события для кнопки
            @Override
            public void onClick(View view) {// метод при нажатии на кнопку
                if(user_field.getText().toString().trim().equals(""))// условие для текстового поля
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();//всплывающие окно, если пользователь ничего не ввел 
                else {// если пользователь ввел данные, то идет обращение к сервису с данными
                    String city = user_field.getText().toString();// переменная для обращения к тексту, который ввел пользователь 
                    String key = "79419d25c44013b337c557f4db6dee99";// ключ для получения информации с сервиса
                    String url = "https://api.openweathermap.org/data/2.5/weather?q="+ city +"&appid="+ key +"&units=metric&lang=ru";// ссылка для получения информации с сервиса

                    new GetUrlData().execute(url);// создание объекта GetUrlData
                }
            }
        });
    }

    private class GetUrlData extends AsyncTask<String, String, String> {// класс для обработки Url адреса

        @Override
        protected void onPreExecute() {// метод, когда мы только начинаем отправлять данные по определеннному url адресу
            super.onPreExecute();// обращение к методу 
            result_info.setText("Ожидайте...");// надпись "Ожидайте..."
        }

        @Override
        protected String doInBackground(String... strings) {// метод для получения всей информации по Url адресу
            HttpURLConnection connection = null;// объект для получения данных по Url адресу
            BufferedReader reader = null;// для эффективного чтения объекта

            try {// код, который может сработать не правильно и создать ошибку
                URL url = new URL(strings[0]);// объект на основе которого можно обращаться по определенному Url адресу
                connection = (HttpURLConnection) url.openConnection();// http соединение 
                connection.connect();

                InputStream stream = connection.getInputStream();// объект для считки данных
                reader = new BufferedReader(new InputStreamReader(stream));// поток в формате строки, который был считан 

                StringBuffer buffer = new StringBuffer();
                String line ="";// пустая строка

                while((line = reader.readLine()) != null)// цикл для считки данных по одной линии с информации, которую получили по url адресу
                    buffer.append(line).append("\n");// метод добавления к строке одной прочитанной линии с добавлением перевода на новую строку

                return buffer.toString();

            } catch (MalformedURLException e) {// блок для исключения, если адрес указан неверно или заданный в нем ресурс отсутствует
                e.printStackTrace();
            } catch (IOException e) {// блок для исключений, возникающих при доступе к данным с помощью потоков, файлов и каталогов
                e.printStackTrace();
            } finally {// блок для закрытия соединений 
                if(connection != null)// закрытие соединения connection
                    connection.disconnect();

                try {
                if(reader != null)// закрытие соединения для считывания данных
                        reader.close();
                    } catch (IOException e) {// блок для исключений, возникающих при доступе к данным с помощью потоков, файлов и каталогов
                    e.printStackTrace();
                }
            }

            return null;// возвращаем ничего 
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {// метод для приема строки с результатом 
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);// обработка JSON
                result_info.setText("Температура: " + jsonObject.getJSONObject("main").getDouble("temp"));// вывод результата внутри текстовой надписи
            } catch (JSONException e) {// блок для исключения, который возникает при обнаружении недопустимого текста JSON, передаче заданной максимальной глубины или при несовместимом тексте JSON с типом свойства объекта
                e.printStackTrace();
            }
        }
    }
}
