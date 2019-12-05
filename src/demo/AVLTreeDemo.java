/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import impl.AVLNode;
import impl.AVLTree;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Davide Carneiro
 */
public class AVLTreeDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        int SIZE = 50;        
        final java.util.ArrayList<Integer> valores = new java.util.ArrayList<>(SIZE);
        
        /*
        
        Exemplifica a introdução interativa de números pelo utilizador. De seguida
        remove os números de forma aleatória, parando em cada remoção para se ver o estado da árvore.
        */
        AVLTree<Integer> arv = new AVLTree<>(true);

        System.out.println("Introduza os números a inserir. Introduza um caracter não numérico para terminar.");
        try
        {
            while(SIZE == 50)
            {
                System.out.print(">> ");
                int n = new Scanner(System.in).nextInt();
                arv.add(n);
                valores.add(n);
                
                arv.imprimeMesmoBonito();
            }
        }
        catch(InputMismatchException ex){}
        
        
        System.out.println("A remover todos os elementos da árvore aleatoriamente.");
        
        Collections.shuffle(valores);
        
        valores.forEach(x -> {arv.remove(x); arv.imprimeMesmoBonito(); if (!arv.isEmpty()){System.out.println("Prima enter para continuar..."); new Scanner(System.in).nextLine();}});
    }
    
}
