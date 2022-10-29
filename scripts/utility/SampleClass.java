/*
    compile with:
      javac SampleClass.java
    run with:
      java SampleClass
*/

public class SampleClass {

    public static void main(String[] args) throws Exception {
        List<List<String>> list1 = Arrays.asList(
                Arrays.asList("Apple", "Banana"),
                Arrays.asList("Carrot", "Beet")
                );
        System.out.println(list1);

        Map<List<String>, String> test1 = new HashMap<>();
        test1.put(Arrays.asList("1", "123a", "Hello"), "en");
        System.out.println(test1);
        // Export model annots as JSON
        // Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        // FileWriter json_file = new FileWriter(json.toString());
        //
        // JSONObject jsonObj = new JSONObject();
        // jsonObj.put("name", "student");
        //
        // JSONArray array = new JSONArray();
        // JSONObject item = new JSONObject();
        // item.put("information", "test");
        // item.put("id", 3);
        // item.put("name", "course1");
        // array.put(item);
        // jsonObj.put("course", array);
        //
        // gsonBuilder.toJson(jsonObj, json_file);
        // json_file.flush();
        // json_file.close();
    }
}
