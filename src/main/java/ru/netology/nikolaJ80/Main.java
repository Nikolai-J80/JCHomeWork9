package ru.netology.nikolaJ80;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);
        String jsonFilename = "data.json";
        writeString(json, jsonFilename);

        List<Employee> listXML = parseXML("data.xml");
        String json2 = listToJson(listXML);
        String jsonFilename2 = "data2.json";
        writeString(json2, jsonFilename2);

        String json3 = readString("new_data.json");
        List<Employee> list3 = jsonToList(json3);
        list3.forEach(System.out::println);
    }

    private static List<Employee> jsonToList(String json3) {
        List<Employee> list = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            Object obj = new JSONParser().parse(json3);
            JSONArray array = (JSONArray) obj;
            for (Object a : array) {
                list.add(gson.fromJson(a.toString(), Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String readString(String fileJSON) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileJSON))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s).append("\n");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }

    private static String getTagValue(String tag, Element element) {
        return element.getElementsByTagName(tag).item(0).getTextContent();
    }

    private static List<Employee> parseXML(String fileXML) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileXML));
        NodeList nodeList = doc.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                long id = Long.parseLong(getTagValue("id", element));
                String firstName = getTagValue("firstName", element);
                String lastName = getTagValue("lastName", element);
                String country = getTagValue("country", element);
                int age = Integer.parseInt(getTagValue("age", element));
                list.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return list;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void writeString(String json, String jsonFilename) {
        try (FileWriter file = new FileWriter(jsonFilename)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}