import java.util.ArrayList;

/**
 * Created by Jordan on 7/7/2017.
 */
public class userInformation {
    ArrayList<String> fields;
    public userInformation(String[] x){
        fields = new ArrayList<>();
        int count = 0;// = // x.length;
        while(count < x.length)
        {
            fields.add(count,x[count]);
            count++;
        }
        //   if (count < length;
    }

    public String to_String(int length) {
        String stringBuilder = "";
        int c = 0;

        for (String place : fields)
        {
            stringBuilder += place + ",";
            c ++;
            if (c == length)
                break;
        }
        stringBuilder=stringBuilder.substring(0,stringBuilder.length()-1) + "\n";
        return stringBuilder;
    }
}
