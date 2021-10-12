package jmetal.problems.TP2;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SensorDeployment extends Problem {

    ArrayList<Integer> TargetCoord = new ArrayList<Integer>();
    int rayon = 50;

    public SensorDeployment(String solutionType, Integer numberOfVariables) throws ClassNotFoundException {

        try {
            for (String line : Files.readAllLines(Paths.get("C:\\Users\\Mikrail\\IdeaProjects\\TP2_jmetal\\src\\jmetal\\problems\\TP2\\TargetPos.txt"))) {
                String[] parts = line.split(",");
                String part1 = parts[0];
                String part2 = parts[1];
                TargetCoord.add(Integer.valueOf(part1));
                TargetCoord.add(Integer.valueOf(part2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        numberOfVariables_ = numberOfVariables*2;
        numberOfObjectives_ =2;
        numberOfConstraints_ = 0;
        problemName_ = "SensorDeployment";

        if(solutionType.compareTo("Real")==0)
            solutionType_ = new RealSolutionType(this);
        else {
            System.out.println("Error: solution type " + solutionType + " invalid");
            System.exit(-1);
        }

        lowerLimit_ = new double[numberOfVariables_];
        upperLimit_ = new double[numberOfVariables_];
        for(int i = 0; i<numberOfVariables_; i++) {
            lowerLimit_[i] = 0;
            upperLimit_[i] = 250;
        }

    }

    @Override
    public void evaluate(Solution solution) throws JMException {
        Variable[] decisionVariables = solution.getDecisionVariables();

        double [] x = new double[numberOfVariables_];

        for (int i = 0; i< numberOfVariables_; i++) {
            x[i] = decisionVariables[i].getValue();
        }

        double f0 = numberOfVariables_/2;
        double f1 = 0.0;

        int[] NombreCapteur = new int[this.TargetCoord.size()/2];
        for(int i = 0; i<this.TargetCoord.size()/2; i++){
            //NombreCapteur.add(0);
            NombreCapteur[i] = 0;
        }

        Integer[][] TargetCoordMat = new Integer[TargetCoord.size()/2][2];
        int k = 0;
        for(int i = 0; i<TargetCoord.size(); i+=2){
            TargetCoordMat[k][0] = this.TargetCoord.get(i);
            TargetCoordMat[k][1] = this.TargetCoord.get(i+1);
            k++;
        }

        for(int i = 0; i<numberOfVariables_ ; i=i+2) {
            for(int j = 0; j<NombreCapteur.length; j++){
                if(this.CalcDist(x[i],x[i+1],TargetCoordMat[j][0],TargetCoordMat[j][1]) <= this.rayon){
                    NombreCapteur[j] ++;
                }
            }
        }


        String a = "";

        for(int i = 0; i<NombreCapteur.length; i++){
            a+=NombreCapteur[i]+";";
        }
        System.out.println(a);

        //f0 = Collections.min(NombreCapteur);
        for(int i = 0; i<NombreCapteur.length;i++){
            if(f0>NombreCapteur[i]){
                f0 = NombreCapteur[i];
            }
        }

        for(int i = 0 ; i<NombreCapteur.length; i++){
            if(NombreCapteur[i] != 0){
                f1 ++;
            }
        }

        System.out.println(f0 + " : " + f1);
        String txt = f0 +" "+(f1)+"\n";
        try {
            Files.write(Paths.get("C:\\Users\\Mikrail\\IdeaProjects\\TP2_jmetal\\src\\jmetal\\pareto.txt"), txt.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        solution.setObjective(0, (-1) * f0);
        solution.setObjective(1, (-1) * f1);
    }

    public double CalcDist(double Xa, double Ya, double Xb, double Yb){
        double dist = Math.sqrt(Math.pow(Xb-Xa, 2) + Math.pow(Yb-Ya, 2));
        return dist;
    }



}
