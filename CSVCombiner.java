import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 userInformation object, has id, pay group, and pay frequency as the name for the Strings and within the constructor.
 */


        /* THESE ARE DEFAULTS:
        sourceFolder=C:\Users\Jordan\IdeaProjects\ADP_CSV_ZIPPER
        sourceType=csv
        outputZipFolderName=adp_vantage_ZIP.zip
        outputUniqueCSVName=adp_vantage_UNIQUE.csv
        fieldsOfInterest=3
        deleteSourceFiles=false
        outputUniqueList=false
        */

public class CSVCombiner {
    private static String globalSettings[] = new String[7];
    private static ArrayList<userInformation> userIDStringList = new ArrayList<userInformation>();

    public static void main(String[] args) throws IOException {
        // Name of the config file - VERY IMPORTANT
        String ConfigFileName = "config.txt";
        String line5;
        String[] userID;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(ConfigFileName));
            int count = 0;
            System.out.println("Reading Config");
            while ((line5 = br.readLine()) != null) {

                String splitConfig = "=";
                userID = line5.split(splitConfig);
                if (userID.length > 1) {
                    globalSettings[count] = userID[1];
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Read config");
        File curDir = new File(globalSettings[0] + "\\.");
        System.out.println("Source set:  "+ curDir.getPath());
        try {
            getAllFiles(curDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void getAllFiles(File curDir) throws IOException {

        // Name of the list to save.
        String outputUniqueCSVName = globalSettings[3]; //"adp_vantage_UNIQUE.csv";//line4

        // Check if it exists, if it does, delete it.
        boolean alreadyExists = new File(outputUniqueCSVName).exists();
        if (alreadyExists) {
            File file = new File(outputUniqueCSVName);
            if (file.delete()) {
                System.out.println("INFO: previous csv "+ outputUniqueCSVName + " was deleted");
            }
            else {
                System.out.println("INFO: " + outputUniqueCSVName + " did not exist before and will be created");
            }
        }

        // Create a structure to hold the Files to be zipped
        File[] filesList = curDir.listFiles();
        ArrayList<File> filesToZip = new ArrayList<>();
        // Grab what file type it is:
        // TODO : Make the program support multiple comma delimited saved text formats for fun
        String fileType = globalSettings[1];
        for (File csv : filesList) {
            if (csv.isFile() && Objects.equals(fileType, csv.toPath().toString().substring(csv.toPath().toString().length() - fileType.length()))) {
                System.out.println(csv.getName() + "\t File #" + (filesToZip.size() + 1));
                filesToZip.add(csv);
                userIDStringList = readFile(csv.getAbsolutePath(), userIDStringList);
            }
        }
        // # of unique users
        System.out.println("INFO: UNIQUE SIZE: " + userIDStringList.size());
        try {
            FileWriter fw = new FileWriter(outputUniqueCSVName);
            // Write headers of the csv.
            fw.write("");
            for (userInformation unique : userIDStringList)
                fw.append(unique.to_String(Integer.parseInt(globalSettings[4])));
            fw.close();
            System.out.println("INFO: new csv " + outputUniqueCSVName + " done being written");
        } catch (Exception e) {
            System.out.println("ERROR: Failed to create " + outputUniqueCSVName);

            e.printStackTrace();
        }

        File oldZip = new File(globalSettings[2]);
        if (oldZip.delete()) {
            System.out.println("INFO: previous zip " + oldZip.getPath() + " was deleted.");
        } else {
            System.out.println("INFO: previous zip " + oldZip.getPath() + " didn't exist, it is being made.");
        }
        FileOutputStream fos = new FileOutputStream(oldZip.getName());
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        filesToZip.add(new File(outputUniqueCSVName));
        for (File srcFile : filesToZip) {
            File fileToZip = new File(srcFile.getAbsolutePath());
            FileInputStream fis = new FileInputStream(fileToZip.getAbsoluteFile());
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        System.out.println("INFO: Zip Created " + oldZip.getPath());
        fos.close();
        // IF we we want to delete .csv files because they are now inside of a .zip folder?
        boolean deleteFiles = Boolean.parseBoolean(globalSettings[5]);
        if( deleteFiles){
            for (File me : filesToZip){
                if(me.delete()){
                    System.out.println("INFO: Deleted: " + me.getPath());
                }
            }
        }

    }


    static ArrayList<userInformation> readFile(String fileName, ArrayList<userInformation> userIDStringList) {
        ArrayList<userInformation> clone = userIDStringList;
        BufferedReader br = null;
        String line = "";
        try {

            br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) {
                String cvsSplitBy = ",";
                String[] userID = line.split(cvsSplitBy);
                userInformation placeHolder;
                //int fieldsOfInterest = Integer.parseInt(globalSettings[4]);
                //if (userID.length >= fieldsOfInterest) {
                    placeHolder = new userInformation(userID); //
                    // placeHolder = new userInformation(userID[0].trim(), userID[1].trim(), userID[2].trim());
                // } else {

                //}

                boolean wasInside = false;
                for (userInformation UI : clone) {
                    if (Objects.equals(UI.fields.get(0), placeHolder.fields.get(0))) {
                        wasInside = true;
                        if (placeHolder.fields.get(0) == null) {
                            wasInside = false;
                        }
                    }
                }
                if (!wasInside) {
                    Boolean printLogs = Boolean.parseBoolean(globalSettings[6]);
                    int fieldsOfInterest = Integer.parseInt(globalSettings[4]);
                    if(printLogs)
                        System.out.print(placeHolder.to_String(fieldsOfInterest));
                    if (placeHolder.fields.get(0) == null) {
                    } else {
                        clone.add(placeHolder);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return clone;
    }
}
