/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impl;

import collections.BinaryTreeNode;

/**
 * Classe que modela um nó de uma árvore AVL
 * 
 * @author Davide Carneiro
 */
public class AVLNode<T> extends BinaryTreeNode<T>{
    
    protected int balance;
    
    public AVLNode(T obj) 
    {
        super(obj);
        this.balance = 0;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return super.element + "("+this.balance+")";
    }
    
    
    
}
