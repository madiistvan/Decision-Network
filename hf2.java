import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Scanner;
import javax.print.FlavorException;

class Main {
    public static int NodesNum;
    public static Scanner scanner;
    public static ArrayList<Node> nodes = new ArrayList<>();
    public static ArrayList<Node> evidencia = new ArrayList<>();
    public static Usefulness u = new Usefulness();
    public static ArrayList<Row> ekvivalencia = new ArrayList<>();
    public static int evidenciaNum;
    public static int varIndex;
    public static int choiceNum;

    public static void main(String[] args) {
       scanner = new Scanner(System.in).useLocale(Locale.ENGLISH);
        NodesNum = scanner.nextInt();
        
        readNodes();
        
        evidenciaNum=scanner.nextInt();
        
        readEvi();
        
        varIndex= scanner.nextInt();
        choiceNum= scanner.nextInt();
        
        int usefullnessNum = nodes.get(varIndex).k*choiceNum;
       
        u.endPoint= nodes.get(varIndex);
        for(int i = 0; i<usefullnessNum;i++){
            scanner.nextInt();
            u.D.add(scanner.nextInt());
            u.U.add(scanner.nextFloat());
        }
        enumerationAsk(nodes.get(varIndex), evidencia, nodes);  
    }
    public static void enumerationAsk(Node X,ArrayList<Node> e,ArrayList<Node> bn ){
        Row Q = new  Row(new ArrayList<Integer>(),new ArrayList<Float>());
            for(int i = 0; i<X.k;i++){
                ArrayList<Node> vars =  deepCopy(bn);

                Q.values.add(i);
                vars.get(X.ID).evi = true;
                vars.get(X.ID).eviVal = i; 
                ArrayList<Node> e1 = deepCopy(e);
                for (Node node : e1) {
                    if(node.ID == X.ID) e1.remove(node);
                }

                
                e1.add(vars.get(X.ID));

                Q.probability.add((float) enumerationAll(vars, e1));

            }
            normalize(Q);
            calculateFinalResult();

    }
    public static calculateFinalResult(){
        for (Float float1 : Q.probability) {
            print(float1+"");
        }
        ArrayList<Double> results = new ArrayList();

        for (int i = 0; i < choiceNum; i++) {
            results.add(0.0);
        }
            
    
        int idx=0;
        for (Integer d : u.D) {
            for (Float p : Q.probability) {
                results.set(d, results.get(d)+p* u.U.get(idx));
            }
            idx++;
        }
        int max=0;
        Double maxVal =results.get(0);
        for (int i = 0; i < results.size(); i++) {
            if(results.get(i)>maxVal){
                maxVal = results.get(i);
                max = i;
            }
        }
        print(max+"");
    }
    public static double enumerationAll(ArrayList<Node> vars,ArrayList<Node> e){
        if(vars.isEmpty())return  1.0000;

        Node Y= vars.get(0);

        ArrayList<Node> e1 = deepCopy(e);
        
        vars.remove(0);

        if(Y.evi){
            if(!Y.parents.isEmpty()){
                ArrayList<Integer> parentsValues = getParrentValueFromId(e1, Y);
                Y.eviProb = calculateProbability(Y,parentsValues); 
                
            }
            else{
                Y.eviProb= Y.table.rows.get(0).probability.get(Y.eviVal);
            }
        
            
            double res = Y.eviProb * enumerationAll(deepCopy(vars),deepCopy(e1));
            return res;
        }
        else{
            double res = 0;
            for (int i = 0; i < Y.k; i++) {
                e1 = deepCopy(e);
                if(!Y.parents.isEmpty()){
                    ArrayList<Integer> parentsValues = getParrentValueFromId(e1, Y);
                    Y.eviVal=i;
                    Y.eviProb = calculateProbability(Y,parentsValues);
                }
                else{
                    Y.eviVal=i;
                    Y.eviProb= Y.table.rows.get(0).probability.get(Y.eviVal);
                }
                Y.evi= true;
                e1.add(Y);
                 
                res += Y.eviProb * enumerationAll(deepCopy(vars),deepCopy(e1));
            }
            return res;
        }
    }
public static boolean benne(ArrayList<Node> list,Node n){
    for (Node node : list) {
        if(node.ID==n.ID && node.eviVal==n.eviVal){
            return true;

        }
    }
return false;

}
    private static float calculateProbability(Node Y, ArrayList<Integer> parentsValues) {
        for (Row row : Y.table.rows) {
                for (int i = 0; i < parentsValues.size(); i++) {
                       // print("ittt "+parentsValues.get(i)+"   "+row.values.get(i));
                    
                }
                if(row.values.equals(parentsValues)){
                    Y.eviProb = row.probability.get(Y.eviVal);
                    return Y.eviProb;
                }
        }
        return (Float) null;
    }

public static Row normalize(Row row){
    float div= 0;
    for (float float1 : row.probability) {
        div+=float1;
    }
    for (int i = 0; i < row.probability.size(); i++) {
        row.probability.set(i, row.probability.get(i)/div);
    }
    return row;

}

public static ArrayList<Row> creatVar(ArrayList<Node> nList){
    ArrayList<Row> res = new ArrayList<>();
    for (Node node : nList) {
        for (Row row : node.table.rows) {
            res.add(row);
        }
    }
    return res;
}

public static ArrayList<Node> deepCopy( ArrayList<Node> list){
    ArrayList<Node> result = new ArrayList();
    for (Node node : list) {
        result.add(new Node(node));
    }

    return result;
}
public static ArrayList<Integer> getParrentValueFromId(ArrayList<Node> nodes, Node node){
    ArrayList<Integer> parentsValues = new ArrayList<>();
        for (Integer parent : node.parents) {
            for (Node parentNode : nodes) {
                if(parentNode.ID==parent){
                    parentsValues.add(parentNode.eviVal);
                }
            }
        }
    return parentsValues;
}

    public static void print(String s){
        System.out.println(s);
    }
    public static void readNodes(){
        
        Node node = new Node();    

        for(int i = 0; i<NodesNum;i++){
            node.ID = i;
            node.k = scanner.nextInt();
            node.n = scanner.nextInt();

            if(node.n == 0){
                String line = scanner.nextLine();
                line = line.replace(",", " ");
                Scanner scanner1 = new Scanner(line).useLocale(Locale.ENGLISH);
                ArrayList<Integer> values = new ArrayList<>();
                ArrayList<Float> prob = new ArrayList<>();
                for(int j = 0; j<node.k;j++){
                    values.add(j);
                    
                    
                    prob.add(scanner1.nextFloat());
                }
                node.table.rows.add(new Row(values,prob));
                scanner1.close();
            } 
            else{
                String line = scanner.nextLine();
                line = line.replace(",", " ");
                line = line.replace(":", " ");

                Scanner scanner1 = new Scanner(line).useLocale(Locale.ENGLISH);
                    
                for(int j = 0; j<node.n;j++){
                    node.parents.add(scanner1.nextInt());
                }
                int tmp = iterNum(node.parents);
                for(int j = 0; j < (tmp); j++){
                    ArrayList<Integer> values = new ArrayList<>();
                    ArrayList<Float> prob = new ArrayList<>();
                    for(int z = 0; z < node.n; z++){
                        values.add(scanner1.nextInt());
                        
                    }
                    for(int z = 0; z < node.k; z++){
                        prob.add(scanner1.nextFloat());
                    }
                    node.table.rows.add(new Row(values,prob));
                }
                scanner1.close();   
            }
            nodes.add(node);
            node = new Node(); 
        }
    }
    public static int iterNum(ArrayList<Integer> parents){
        int val=1;
        for (Integer i : parents) {
            val *= nodes.get(i).k;
        }
        return val;

    }
    public static void readEvi(){
        int index;
        int value;
        for(int i = 0; i<evidenciaNum;i++){
            index = scanner.nextInt();
            value = scanner.nextInt();
            nodes.get(index).evi=true;
            nodes.get(index).eviVal=value;
            evidencia.add(nodes.get(index));
            //print("evidencia "+ value +"index "+ index);
        }
    }
}
class Node  {

    public int ID;
    public boolean evi = false;

    public float eviProb;

    public int eviVal;

    public int k;

    public int n;

    public ArrayList<Integer> parents;

    public ArrayList<Node> parentsArrayList;
    public Node(Node node){
        ID = node.ID;
        evi = node.evi;
        eviProb = node.eviProb;
        eviVal = node.eviVal;
        k = node.k;
        n = node.n;
        parents = node.parents;
        table=node.table;
    }
    public Table table ;
    Node(int k, int n,ArrayList<Integer> parrents, Table table){
        this.k = k;
        this.n = n;
        this.parents=parrents;
        this.table=table;
    }
    Node(){
        this.parents=new ArrayList<>();
        this.table=new Table();
    }
}
class Usefulness{
    Node endPoint;
    ArrayList<Integer> D = new ArrayList<>();
    ArrayList<Float> U = new ArrayList<>();
}
class Row{
    int index;
    ArrayList<Integer> values;
    ArrayList<Float> probability;
    Row(ArrayList<Integer> values,ArrayList<Float> probability){
        this.values=values;
        this.probability=probability;
    }
}
class Table{
    ArrayList<Row> rows= new ArrayList<>();
}
