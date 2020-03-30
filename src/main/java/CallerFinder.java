import org.apache.maven.cli.MavenCli;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class CallerFinder {

    public static  ArrayList<String> methodList;

    public JSONObject methodLister(String projectPath, String projectName) throws IOException, InterruptedException {


        JSONObject myList;
        Path directory = Paths.get(projectPath);
        String otherFolder = directory+"";
        File dirPath=new File(otherFolder);

            System.out.println("exists "+dirPath);
            System.out.println(otherFolder);
            MavenCli cli = new MavenCli();
        cli.doMain(new String[]{"install", "-DprojectName=["+projectPath+"]"},otherFolder,System.out,System.out);
        myList = getCrunchifyClassNamesFromJar(otherFolder+"/target/"+projectName+"-1.0-SNAPSHOT.jar");



        cli.doMain(new String[]{"test","-Dtest="+projectPath},otherFolder,System.out,System.out);
        System.out.println("*******after -Dtest");
        cli.doMain(new String[]{"test","-Dtest="+extractTestClassName(myList)},otherFolder,System.out,System.out);
        cli.doMain(new String[]{"test","-Dtest="+extractTestClassName(myList)+"#"+methodList.get(1)},otherFolder,System.out,System.out);


        cli.doMain(new String[]{"test", "-Dtest=CallerMethodClassTest"}, otherFolder, System.out, System.out);
            myList = getCrunchifyClassNamesFromJar(otherFolder+"/target/"+projectName+"-1.0-SNAPSHOT.jar");
        cli.doMain(new String[]{"test", "-Dcucumber.options=\\src\\test\\features"}, otherFolder, System.out, System.out);
        myList = getCrunchifyClassNamesFromJar(otherFolder+"/target/"+projectName+"-1.0-SNAPSHOT.jar");

        //    cli.doMain(new String[]{"clean", "-DprojectName=["+projectName+"]"},otherFolder,System.out,System.out);

           // cli.doMain(new String[]{"install", "-DprojectName=["+projectName+"]"},otherFolder,System.out,System.out);
           //  myList = getCrunchifyClassNamesFromJar(otherFolder+"/target/"+projectName+"-1.0-SNAPSHOT.jar");
            System.out.println(myList);

        for(int i=0;i<methodList.size();i++)
        System.out.println(methodList.get(i));
        return myList;
    }

    private String extractTestClassName(JSONObject myList) {
        String className="";
        for(int i=0;i<myList.length();i++){
            className=myList.getString("test");
        }
        return className;
    }


    @SuppressWarnings("resource")
    public static JSONObject getCrunchifyClassNamesFromJar(String crunchifyJarName) {
        methodList= new ArrayList<>();

        JSONArray listofClasses = new JSONArray();
        JSONObject crunchifyObject = new JSONObject();
        try {
            JarInputStream crunchifyJarFile = new JarInputStream(new FileInputStream(crunchifyJarName));
            JarEntry crunchifyJar;

            while (true) {
                crunchifyJar = crunchifyJarFile.getNextJarEntry();
                if (crunchifyJar == null) {
                    break;
                }
                if ((crunchifyJar.getName().endsWith(".class"))) {
                    String className = crunchifyJar.getName().replaceAll("/", "\\.");
                    String myClass = className.substring(0, className.lastIndexOf('.'));
                    listofClasses.put(myClass);
                   

                }
            }
         //   crunchifyObject.put("Jar File Name", crunchifyJarName);
            crunchifyObject.put("List of Class", listofClasses);
            Class<?> crunchifyClass = null;
            for(int i=0;i<listofClasses.length();i++){
            crunchifyClass = Class.forName(listofClasses.get(i).toString());
            Method[] main = crunchifyClass.getDeclaredMethods();
            System.out.print("method name--> ");
                for( i=0;i< main.length;i++){
                System.out.println(main[i].toString());
                methodList.add(main[i].toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Oops.. Encounter an issue while parsing jar" + e.toString());
        }
        return crunchifyObject;
    }






}
