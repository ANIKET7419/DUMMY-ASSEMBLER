package Assembler;
import com.sun.source.tree.BreakTree;
import org.jetbrains.annotations.NotNull;
import java.io.*;
import java.util.ArrayList;

public class Assembler {
    static ArrayList<ArrayList<String>> section_table = new ArrayList<>();
    static ArrayList<ArrayList<String>> symbol_table = new ArrayList<>();
    static String file = "";
    static int location_counter = 0;
    static String lines[];
    static  FileOutputStream outputStream;
    static  final String output_file_name="/root/IdeaProjects/src/output.out";

    public static void main(String[] args) throws Exception {
        FileReader reader = new FileReader("/root/IdeaProjects/src/sourcecode.asm");
        outputStream=new FileOutputStream(output_file_name);
        for (; ; ) {
            int c = reader.read();
            if (c != -1)
                file += (char) c;
            else
                break;
        }
        lines = file.split("\n");
        firstPass();
        if (section_table.size() >= 1)
            section_table.get(section_table.size() - 1).add(1, location_counter + "");
        System.out.println("First Pass Successfully Done ");
        System.out.println("-------------SECTION TABLE ------------");
        System.out.println("Name\t Size");
        for (ArrayList<String> list : section_table)
            System.out.println(list.get(0) + "\t\t\t" + list.get(1));
        System.out.println("-------------SYMBOL TABLE ------------");
        System.out.println("Name\tSize\tType\tOffset");
        for (ArrayList<String> list : symbol_table)
            System.out.println(list.get(0) + "\t\t" + list.get(1) + "\t\t" + list.get(2) + "\t\t" + list.get(3));

    }

    static int value(@NotNull String temp) {
        if (temp.equals("DD"))
            return 4;
        else if (temp.equals("DW"))
            return 2;
        else
            return 1;

    }

    static void firstPass() {
        for (int i = 0; i < lines.length; i++) {
            String current = lines[i];
            current = current.trim();
            String words[] = current.split(" ");
            if (words.length == 0)
                continue;
            if (words[0].charAt(0) == '.') {
                if (words[0].equals(".CODE")) {
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(0, "CODE");
                    temp.add(1, "0");
                    if (section_table.size() >= 1)
                        (section_table.get(section_table.size() - 1)).add(1, "" + location_counter);
                    section_table.add(temp);
                    location_counter = 0;
                } else if (words[0].equals(".DATA")) {
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(0, "DATA");
                    temp.add(1, "0");
                    if (section_table.size() >= 1)
                        (section_table.get(section_table.size() - 1)).add(1, "" + location_counter);
                    section_table.add(temp);
                    location_counter = 0;
                } else {
                    System.out.println("There is a syntax error at line : " + (i + 1));
                    System.exit(100);
                }
            } else {
                if (words.length >= 2) {

                    if (words[1].equals("DD") || words[1].equals("DW") || words[1].equals("DB")) {

                        if (section_table.size() == 0) {
                            System.out.println("There is syntax error at line : " + (i + 1));
                            System.exit(100);
                        } else {
                            if (!section_table.get(section_table.size() - 1).get(0).equals("DATA")) {
                                System.out.println("There is syntax error at line : " + (i + 1));
                                System.exit(100);
                            }
                        }
                        if (words.length == 2) {
                            System.out.println("There is syntax error at line : " + (i + 1));
                            System.exit(100);
                        } else {
                            ArrayList<String> temp = new ArrayList<>();
                            temp.add(0, words[0]);
                            temp.add(1, words[1]);
                            temp.add(2, "Variable");
                            temp.add(3, location_counter + "");
                            symbol_table.add(temp);
                            location_counter = location_counter + (value(words[1]) * (words.length - 2));
                        }
                    } else {


                        switch (words[0]) {
                            case "MOV" -> {

                                if (words.length < 4) {
                                    System.out.println("There is some error at line : " + (i + 1));
                                    System.exit(100);
                                } else {


                                    if (!words[2].equals(",")) {
                                        System.out.println("There is some error at line : " + (i + 1));
                                        System.exit(100);
                                    } else {
                                        if ((words[1].equals("A") || words[1].equals("B"))) {
                                            location_counter = location_counter + 5;
                                        } else {
                                            System.out.println("There is some error at line : " + (i + 1));
                                            System.exit(100);
                                        }
                                    }


                                }
                            }
                            case "ADD", "INC", "SUB" -> {

                                if (words.length > 2) {
                                    System.out.println("There is some error at line : " + (i + 1));
                                    System.exit(100);
                                } else {
                                        location_counter  += 5;
                                }

                            }
                            case "END" -> {

                                return;
                            }
                            case "CMP" -> {
                                if (words.length > 3) {
                                    System.out.println("There is some error at line : " + (i + 1));
                                    System.exit(100);
                                } else {
                                        location_counter += 9;
                                }

                            }
                            default -> {
                                System.out.println("There is some error at line :" + (i + 1));
                                System.exit(100);
                            }
                        }


                    }
                }

            }


        }


    }
  static  boolean search(String name)
  {
      for(ArrayList<String> te:symbol_table)
      {
          if (te.get(0).equals(name))
              return true;
      }

      return false;
  }

  static  void writeNumber(String number,String size,OutputStream outputStream,int line_number) throws  Exception
  {

      int value = Integer.parseInt(number);
      String hexvalue = Integer.toHexString(value);
      if (hexvalue.length()>value(size))
      {
          System.out.println("ERROR : NUMBER IS BIGGER , LINE # "+(line_number+1));
          deleteFile(output_file_name,outputStream);
          System.exit(100);
      }
      for (int i2 = hexvalue.length() * 4; i2 < value(size) *2; i2++)
          hexvalue = "0" + hexvalue;
      outputStream.write(hexvalue.getBytes());



  }
  static  void deleteFile(String name, @NotNull OutputStream outputStream) throws Exception
  {
      outputStream.close();
      File file=new File(name);
      boolean isdeleted=file.delete();
      System.out.println(isdeleted?"File is deleted Successfully ":"ERROR : FILE IS NOT DELETED ...");
  }



  static  String getLocationCounter(String name)
  {
      String lc="NF";  //NF FOR NOT FOUND
   for(int i=0;i<symbol_table.size();i++)
   {
       if (symbol_table.get(i).get(0).equals(name))
       {
           lc= symbol_table.get(i).get(3);
       }
   }
   if (!lc.equals("NF"))
   {
       String hex=Integer.toHexString(Integer.parseInt(lc));
       for (int i=hex.length()*4;i<8;i++)
           hex="0"+hex;
       return hex;
   }
   return lc;
  }


   static String getOpcode(String ins)
   {

       switch (ins)
       {

           case "ADD" -> {
               return "78";
           }
           case "SUB" -> {
               return "86";
           }
                   case "MOV" ->{
               return "89";
                   }
                case "CMP" ->
                        {
                            return "56";
                        }
                        case "INC" ->
                                {
                                    return "99";
                                }


           default ->
                   {
                       return "NF"; //NF FOR NOT FOUND
                   }
       }


   }

    static void secondPass() throws  Exception {


        for(int i=0;i<lines.length;i++)
        {
            String current=lines[i];
            current=current.trim();
            String words[]=current.split(" ");
            if (words.length==0)
                continue;
            if (words[0].charAt(0)=='.')
            {
                if (words[0].equals(".CODE") || words[0].equals(".DATA"))
                {
                    location_counter=0;
                }
            }
            else
            {
                if (words.length>=2)
                {

                    if (words[1].equals("DD")||words[1].equals("DW")||words[1].equals("DB"))
                    {

                           outputStream.write((""+location_counter).getBytes());
                            for(int i1=2;i1<words.length;i1++)
                            {
                                try {
                                    writeNumber(words[i1],words[1],outputStream,i1);
                                }
                                catch (Exception exp)
                                {
                                    System.out.println("Error : DATA MUST BE LITERAL , LINE NUMBER "+(i+1));
                                    System.exit(100);
                                }
                            }
                            location_counter=location_counter+(value(words[1])*(words.length-2));
                    }
                    else {
                        switch (words[0]) {
                            case "MOV" -> {
                                outputStream.write((location_counter+"").getBytes());
                                outputStream.write(getOpcode("MOV").getBytes());
                                       if (!search(words[1]))
                                       {
                                           try{
                                               Integer.parseInt(words[1]);
                                               System.out.println("ERROR : Destination can never be literal , Line # "+(i+1));
                                               deleteFile(output_file_name,outputStream);
                                               System.exit(100);
                                           }
                                           catch (Exception ep)
                                           {
                                               outputStream.write(getLocationCounter(words[1]).getBytes());
                                           }
                                       }
                                       if (!search(words[2]))
                                       {

                                           try
                                           {
                                               writeNumber(words[2],"DW",outputStream,i);
                                           }
                                           catch (Exception ep)
                                           {
                                               System.out.println("ERROR : SYMBOL IS NOT FOUND , LINE : "+(i+1));
                                               deleteFile(output_file_name,outputStream);
                                               System.exit(100);
                                           }
                                       }
                                            location_counter = location_counter + 5;

                            }
                            case "ADD", "INC", "SUB" -> {


                                  outputStream.write((location_counter+"").getBytes());
                                  outputStream.write(getOpcode(words[1]).getBytes());
                                   try{
                                       writeNumber(words[1],"DW",outputStream,i);
                                       if (words[0].equals("INC"))
                                       {
                                           System.out.println("In INC instruction operand can never le literal at line : "+(i+1));
                                           deleteFile(output_file_name,outputStream);
                                           System.exit(100);
                                       }
                                   }
                                   catch (Exception ep)
                                   {
                                       if (!search(words[1]))
                                       {
                                           System.out.println("ERROR : Symbol is not found , Line # "+(i+1));
                                           deleteFile(output_file_name,outputStream);
                                           System.exit(100);
                                       }
                                       else
                                       {
                                           outputStream.write(getLocationCounter(words[1]).getBytes());
                                       }

                                   }
                                        location_counter += 5;

                                }
                            case "END" -> {
                                return;
                            }
                            case "CMP" -> {
                                outputStream.write((location_counter+"").getBytes());
                                outputStream.write(getOpcode("CMP").getBytes());
                                boolean first_second=true;
                                  try
                                  {
                                     int val1= Integer.parseInt(words[1]);
                                     first_second=false;
                                    int val2= Integer.parseInt(words[2]);
                                    String hex=Integer.toHexString(val1);
                                    String hex2=Integer.toHexString(val2);
                                    for(int i1=hex.length();i1<8;i1++)
                                        hex="0"+hex;
                                    for(int i1=hex2.length();i1<8;i1++)
                                        hex2="0"+hex2;
                                    outputStream.write(hex.getBytes());
                                    outputStream.write(hex2.getBytes());
                                  }
                                  catch (Exception e)
                                  {

                                      if (first_second) {
                                          if (!search(words[1])) {
                                              System.out.println("ERROR: SYMBOL IS NOT FOUND , LINE #" + (i + 1));
                                              deleteFile(output_file_name, outputStream);
                                              System.exit(100);
                                          }
                                          else
                                          {
                                              outputStream.write(getLocationCounter(words[1]).getBytes());
                                          }
                                          try
                                          {
                                              String hex2=Integer.toHexString(Integer.parseInt(words[2]));
                                              for(int i1=hex2.length();i1<8;i1++)
                                                  hex2="0"+hex2;
                                              outputStream.write(hex2.getBytes());
                                          }
                                          catch (Exception ep)
                                          {
                                              if (!search(words[2]))
                                              {
                                                  System.out.println("ERROR: SYMBOL IS NOT FOUND , LINE #" + (i + 1));
                                                  deleteFile(output_file_name, outputStream);
                                                  System.exit(100);
                                              }
                                              else
                                              {
                                                outputStream.write(getLocationCounter(words[2]).getBytes());
                                              }
                                          }


                                      }
                                      else
                                      {
                                          if (!search(words[2]))
                                          {
                                              System.out.println("ERROR: SYMBOL IS NOT FOUND , LINE #" + (i + 1));
                                              deleteFile(output_file_name, outputStream);
                                              System.exit(100);
                                          }
                                          else{
                                              outputStream.write(getLocationCounter(words[2]).getBytes());
                                          }
                                      }

                                  }
                                  location_counter += 9;
                            }
                        }

                    }
                }

            }


        }

    }
    }

