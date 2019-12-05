/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impl;

import collections.*;
import exceptions.ElementNotFoundException;
import java.util.Iterator;

/**
 * Classe que modela uma AVLTree com nós sem referência ao pai.
 * 
 * Implementa as operações de inserção, remoção, balanceamento e ainda
 * a visualização da árvore na consola.
 * 
 * @author Davide Carneiro
 * @param <T> O tipo dos elementos a guardar nos nós da árvore
 */
public class AVLTree<T extends Comparable<T>> extends LinkedBinaryTree<T>
{
    private static final String GREEN = "\u001B[32m";
    private final boolean explain;
    
    /**
     * Cria uma nova instância de uma árvore binária (vazia)
     * @param explain um booleano que determina se a árvore deve ou não
     * explicar as suas operações na consola (e.g. rotações, etc.)
     */
    public AVLTree(boolean explain) 
    {
        this.explain = explain;
    }

    /**
     * Versão condicional (com base no valor de explain) de um System.out.println 
     * @param s 
     */
    private void println(String s){if (explain) System.out.println(s);}
    
    /**
    * Procura, iterativamente, a posição para inserir o novo elemento
    * À medida que procura, vai construindo numa Stack o caminho que vai
    * percorrendo (para depois
    * percorrer em sentido inverso atualizando balanceamentos e fazendo 
    * rotações quando necessário)
     * @param element o elemento a adicionar à AVL
     */
    public void add(T element) 
    {
        println("########################");
        println("    A adicionar "+element);
        println("########################");
        AVLNode<T> temp = new AVLNode<>(element);
        StackADT<AVLNode<T>> caminho = new LinkedStack<>(); //caminho seguido até inserir o novo nó (para depois atualizar balanceamento)

        if (isEmpty()) {
            root = temp;
        } else {
            BinaryTreeNode<T> current = root;
            caminho.push((AVLNode<T>)root);
            
            boolean added = false;
            while (!added) //ir descendo na árvore à procura da posição correta para inserir o novo nó. Guarda o caminho percorrido na stack
            {
                if (element.compareTo(current.element) < 0) 
                {
                    if (current.left == null) {
                        current.left = temp;
                        added = true;
                    } else {
                        current = current.left;
                        caminho.push((AVLNode<T>)current);
                    }
                } else if (current.right == null) {
                    current.right = temp;
                    added = true;
                } else {
                    current = current.right;
                    caminho.push((AVLNode<T>)current);
                }
            }
        }
        count++;
              
        //propagar alterações no balance pela árvore acima e fazer rotações se necessário
        
        if (explain)
            imprimeMesmoBonito();
        
        atualizaBalanceamento(caminho);
    }
    
    /**
     * Remove um determinado elemento da árvore, se existir. 
     * À medida que o procura, vai construindo numa Stack o caminho que vai
     * percorrendo (para depois
     * percorrer em sentido inverso atualizando balanceamentos e fazendo 
     * rotações quando necessário)
     * @param targetElement o elemento a remover
     * @return referência ao elemento removido
     * @throws ElementNotFoundException quando o elemento a remover não existe na árvore
     */
    public T remove(T targetElement) throws ElementNotFoundException 
    {
        println("########################");
        println("    A remover "+targetElement);
        println("########################");
        
        StackADT<AVLNode<T>> caminho = new LinkedStack<>();
        
        T result = null;
        if (!isEmpty()) 
        {
            if (((Comparable) targetElement).equals(root.element)) //o elemento a remover encontra-se na raiz
            {
                result = root.element;
                root = replacement(root, caminho);
                count--;
            } 
            else 
            {
                BinaryTreeNode<T> current, parent = root;
                boolean found = false;
                if (((Comparable) targetElement).compareTo(root.element) < 0)
                    current = root.left;
                else
                    current = root.right;
                
                caminho.push((AVLNode<T>) parent);
                caminho.push((AVLNode<T>) current);
                
                while (current != null && !found) //procurar o elemento a remover. À medida que desce na árvore, guarda o caminho na stack.
                {
                    if (targetElement.equals(current.element)) 
                    {
                        found = true;
                        count--;
                        result = current.element;
                        if (current == parent.left)
                            parent.left = replacement(current, caminho);
                        else
                            parent.right = replacement(current, caminho);
                    } 
                    else 
                    {
                        parent = current;
                        if (((Comparable) targetElement).compareTo(current.element) < 0) 
                        {
                            current = current.left;
                            caminho.push((AVLNode<T>) current);
                        } 
                        else 
                        {
                            current = current.right;
                            caminho.push((AVLNode<T>) current);
                        }
                    }
                } //while
                if (!found) {
                    throw new ElementNotFoundException("binary search tree");
                }
            }
        } //outer if
        
        if (explain)
            imprimeMesmoBonito();
        
        atualizaBalanceamento(caminho);
       
        return result;
    }

    /**
     * Encontra o nó certo para substituir um determinado nó a remover. 
     * 
     * À medida que desce pela árvore para encontrar o substituto, vai também
     * guardando o caminho percorrido uma vez que também nesse caminho pode
     * ser necessário atualizar o balanceamento e/ou fazer rotações.
     * 
     * @param node o nó a remover
     * @param caminho o caminho já percorrido até encontrar o nó a remover, e ao
     * qual se vão acrescentar os nós percorridos até encontrar o substituto
     * @return o nó que será utilizado para substituir o nó a remover
     */
    protected BinaryTreeNode<T> replacement(BinaryTreeNode<T> node, StackADT<AVLNode<T>> caminho)
    {
        if (!caminho.isEmpty()) //neste ponto, o nó no topo do caminho é sempre o nó a remover e por isso pode ser retirado da stack (porque não vamos verificar o seu balanceamento)
            caminho.pop();
   
        UnorderedListADT<AVLNode<T>> listaCaminho = new ArrayUnorderedList<>();
        
        BinaryTreeNode<T> result;
        if ((node.left == null) && (node.right == null)) {
            result = null;
        } else if ((node.left != null) && (node.right == null)) {
            result = node.left;
        } else if ((node.left == null) && (node.right != null)) {
            result = node.right;
        } 
        else 
        {
            BinaryTreeNode<T> current = node.right;
            BinaryTreeNode<T> parent = node;
                 
            while (current.left != null) 
            {
                parent = current;
                current = current.left;
                listaCaminho.addToRear((AVLNode<T>) parent);
            }
            
            if (node.right == current) 
                current.left = node.left;
            else 
            {
                parent.left = current.right;
                current.right = node.right;
                current.left = node.left;
            }
            result = current;
        }
        
        //é necessário colocar o nó que será o substituto já na posição correta na stack do caminho (i.e. no topo do caminho percorrido até encontrar o elemento a remover)
        if(result != null)
            caminho.push((AVLNode<T>) result);
        
        //de seguida colocar os restantes elementos no caminho (i.e. os elementos entre o nó a remover e o nó para substituir)
        while(!listaCaminho.isEmpty())
            caminho.push(listaCaminho.removeFirst());
        
        return result;
    }
    
    /**
     * Percorre a stack que representa o caminho que foi seguido para encontrar 
     * o ponto de inserção do novo elemento em sentido inverso. Vai "subindo
     * na árvore" (obtendo cada nó na Stack). Para cada nó, atualiza o seu balanceamento
     * e chama o método {@link AVLTree#fazRotacoes(exercicio1.AVLNode, exercicio1.AVLNode) fazRotacoes}
     * que faz a sua rotação, se necessário.
     * 
     * @param caminho {@link StackADT} com o caminho percorrido até ao ponto de inserção do novo nó
     */
    public void atualizaBalanceamento(StackADT<AVLNode<T>> caminho)
    {
        while(!caminho.isEmpty())
        {
            AVLNode<T> next = caminho.pop();
            
            println("\nA analisar balanceamento do nó "+next+"");
            println("Novo balanceamento do nó após inserção: "+(altura(next.right) - altura(next.left)));
            next.setBalance(altura(next.right) - altura(next.left));

            if(caminho.isEmpty())
                fazRotacoes(next, null); //já estamos na root
            else
                fazRotacoes(next, caminho.peek()); //passa o sobre o qual vamos fazer a rotação e o seu pai (próximo nó no caminho)
        }
    }
    
    /**
     * Faz a rotação adequada de um determinado nó, se for necessário fazê-la 
     * (de acordo com o seu balanceamento). 
     * 
     * Implementa as rotações descritas nos slides 2017.ED.Aula10.pdf
     * 
     * @param next o {@link AVLNode} a tratar nesta invocação
     * @param parent o pai de next (null se next é a raíz da árvore)
     */
    public void fazRotacoes(AVLNode<T> next, AVLNode<T> parent)
    {
        println("É necessário rodar o nó "+next+"?");
        if (((AVLNode)next).balance == 2)
        {
            if (((AVLNode)next.right).balance < 0)
            {
                if (parent == null) //é uma rotação sobre a raiz
                    root = rotacaoDireitaEsquerda(next);
                else if (parent.right == next)
                    parent.right = rotacaoDireitaEsquerda(next);
                else 
                    parent.left = rotacaoDireitaEsquerda(next);
            }
            else if (((AVLNode) next.right).balance > 0) 
            {
                if (parent == null) //é uma rotação sobre a raiz
                    root = rotacaoEsquerda(next);
                else if (parent.right == next)
                    parent.right = rotacaoEsquerda(next);
                else 
                    parent.left = rotacaoEsquerda(next);
            } 
            else
            {
                if (parent == null) //é uma rotação sobre a raiz
                    root = rotacaoEsquerda(next);
                else if (parent.right == next)
                    parent.right = rotacaoEsquerda(next);
                else
                    parent.left = rotacaoEsquerda(next);
            }
        }
        else if (((AVLNode) next).balance == -2) 
        {
                if (((AVLNode) next.left).balance > 0)
                {
                    if (parent == null)
                        root = rotacaoEsquerdaDireita(next);
                    else if (parent.left == next)
                        parent.left = rotacaoEsquerdaDireita(next);
                    else
                        parent.right = rotacaoEsquerdaDireita(next);
                }
                else if (((AVLNode) next.left).balance < 0)
                {
                    if (parent == null) //é uma rotação sobre a raiz
                        root = rotacaoDireita(next);
                    else if (parent.left == next)
                        parent.left = rotacaoDireita(next);
                    else 
                        parent.right = rotacaoDireita(next);
                }
                else
                {
                    if (parent == null) //é uma rotação sobre a raiz
                        root = rotacaoDireita(next);
                    else if (parent.left == next)
                        parent.left = rotacaoDireita(next);
                    else
                        parent.right = rotacaoDireita(next);
                }
        }
        else println("Não");
    }
    
    /**
     * Faz uma rotação à direita da árvore cuja raíz é dada como parâmetro
     * @param root o nó à volta da qual se fará a rotação à direita
     * @return a referência da nova raiz
     */
    public AVLNode<T> rotacaoDireita(AVLNode<T> root)
    {
        println("Rotação à direita sobre "+root.element);
        AVLNode<T> temp = (AVLNode<T>) root.left.right;
        AVLNode<T> new_root = (AVLNode<T>) root.left;
        new_root.right = root;
        new_root.right.left = temp;
        root = new_root;
        
        println("Resultado da rotação:");
        if (explain)
            imprimeMesmoBonito(root);
        
        updateAllBalances((AVLNode<T>) root);
        
        return root;
    }
    
    /**
     * Faz uma rotação à esquerda da árvore cuja raíz é dada como parâmetro
     * @param root o nó à volta da qual se fará a rotação à esquerda
     * @return a referência da nova raiz
     */
    public AVLNode<T> rotacaoEsquerda(AVLNode<T> root)
    {
        println("Rotação à esquerda sobre "+root.element);
        AVLNode<T> temp = (AVLNode<T>) root.right.left;
        AVLNode<T> new_root = (AVLNode<T>) root.right;
        new_root.left = root;
        new_root.left.right = temp;
        root = new_root;
        
        println("Resultado da rotação:");
        if (explain)
            imprimeMesmoBonito(root);
        
        updateAllBalances((AVLNode<T>) root);
        
        return root;
    }
    
    /**
     * Faz uma rotação à direita da sub-árvore esquerda da árvore cuja 
     * raíz é dada como parâmetro. De seguida, faz uma rotação à esquerda da
     * árvore cuja raíz é recebida como parâmetro
     * @param root o nó à volta da qual se fará a rotação direita-esquerda
     * @return a referência da nova raiz
     */
    public AVLNode<T> rotacaoDireitaEsquerda(AVLNode<T> root)
    {
        println("Rotação direita/esquerda");
        println("Rotação à direita sobre "+root.right.element);
        AVLNode<T> temp = (AVLNode<T>) root.right.left.right;
        AVLNode<T> new_root = (AVLNode<T>) root.right.left;
        new_root.right = root.right;
        new_root.right.left = temp;
        root.right = new_root;
        
        println("Resultado da rotação:");
        if (explain)
            imprimeMesmoBonito(root);
        
        return rotacaoEsquerda(root);
    }
    
    /**
     * Faz uma rotação à esquerda da sub-árvore direita da árvore cuja 
     * raíz é dada como parâmetro. De seguida, faz uma rotação à esquerda da
     * árvore cuja raíz é recebida como parâmetro
     * @param root o nó à volta da qual se fará a rotação direita-esquerda
     * @return a referência da nova raiz
     */
    public AVLNode<T> rotacaoEsquerdaDireita(AVLNode<T> root)
    {
        println("Rotação esquerda/direita");
        println("Rotação à esquerda sobre "+root.left);
        AVLNode<T> temp = (AVLNode<T>) root.left.right.left;
        AVLNode<T> new_root = (AVLNode<T>) root.left.right;
        new_root.left = root.left;
        new_root.left.right = temp;
        root.left = new_root;
        
        println("Resultado da rotação:");
        if (explain)
            imprimeMesmoBonito(root);
        
        return rotacaoDireita(root);
    }
    
    /**
     * Atualiza o balanceamento de todos os nós incluindo e abaixo do nó recebido
     * como parâmetro
     * @param node o nó da árvore na qual se vai atualizar os balanceamentos
     */
    public void updateAllBalances(AVLNode<T> node)
    {
        if (node == null) return;
        
        node.balance = altura(node.right) - altura(node.left);
        updateAllBalances((AVLNode<T>) node.left);
        updateAllBalances((AVLNode<T>) node.right);
    }
   
    /**
     * Imprime cada elemento da árvore e o seu balanceamento (level order)
     */
    public void imprime() 
    {
        Iterator<BinaryTreeNode<T>> it = AVLLevelOrderToString();
        while(it.hasNext())
        {
            AVLNode<T> next = (AVLNode<T>)it.next();
            System.out.println(next.element+ "("+next.balance+")");
        }
    }
    
    /**
     * Imprime a árvore na consola como uma árvore mas sem imprimir os ramos.
     * 
     * Especialmente útil para a visualização de árvores muito grandes, que podem
     * ocupar muito espaço verticalmente quando imprimidas com ramos.
     * 
     * @param treeToPrint a árvore a imprimir
     */
    public static void imprimeBonito(AVLNode treeToPrint)
    {
        AVLNode[] array = toArray(treeToPrint);//árvore representada como array, porque me pareceu a forma mais fácil de pensar no problema...
        int esp_i = array.length; //o número inicial de espaços antes do primeiro nó de cada nível
        int esp_meio = array.length*2;//o número inicial de espaços entre cada nó em cada nível
        int pagina = 1;//número de elementos no nível atual
        int i=0, j, k;
        
        while(i<array.length) //para cada elemento da árvore
        {
            j=0;
            while(j++<esp_i) System.out.print(" "); //imprime os espaços iniciais
            j=0;
            while(j<pagina)//para cada elemento neste nível
            {
                k = 0;
                if (array[j+i] == null)//se o elemento é nulo imprime um X
                    System.out.print("X");
                else
                    System.out.print(array[j+i].element);//senão imprime o elemento
                
                int dif = 0;
                if (array[j+i] != null)
                    dif = array[j+i].element.toString().length()-1; //calcula o tamanho da string do elemento atual, para o retirar do número de strings (para que a árvore não seja deslocada para a direita quando há elementos grandes)
                
                while(k++ < esp_meio-dif) System.out.print(" "); //imprime os espaços intermédios
                j++;
            }
            System.out.println();
            esp_meio /= 2; //em cada nível o espaço entre nós passa a metade
            esp_i /= 2;//em cada nível o nº de espaços iniciais passa a metade
            i+=pagina;//passar ao nível seguinte da árvore porque o ciclo interno já tratou de todos os elementos deste nível
            pagina *=2;//em cada nível, o número de elementos duplica
        }
    }
    
    /**
     * versão que imprime a instância atual
     */
    public void imprimeMesmoBonito()
    {
        imprimeMesmoBonito((AVLNode) root);
    }
    
    /**
     * Imprime na consola a árvore recebida como parâmetro, com ramos.
     * 
     * @param treeToPrint a árvore a imprimir
     */
    public static void imprimeMesmoBonito(AVLNode treeToPrint)
    {
        AVLNode[] array = toArray((AVLNode) treeToPrint);//árvore representada como array, porque me pareceu a forma mais fácil de pensar no problema...
        int esp_i = array.length; //o número inicial de espaços antes do primeiro nó de cada nível
        int esp_meio = array.length*2;//o número inicial de espaços entre cada nó em cada nível
        int pagina = 1;//número de elementos no nível atual
        int i=0, j, k;
        int v_space = array.length/2;//a altura de cada conjunto de ramos de um nível
        int n_reps_h = 1; //quantas vezes se repete o desenho dos ramos (duplica em cada nível)
        
        System.out.println("*******************************************************");
        while(i<array.length)
        {
            j=0;
            while(j++<esp_i) System.out.print(" "); //imprime os espaços iniciais
            j=0;
            
            while(j<pagina) 
            {
                k = 0;
                if (array[j+i] == null)//se o elemento é nulo imprime um espaço
                    System.out.print(" ");
                else
                    System.out.print(array[j+i].element);//senão imprime o elemento
                
                int dif = 0;
                if (array[j+i] != null)
                    dif = array[j+i].element.toString().length()-1; //calcula o tamanho da string do elemento atual, para o retirar do número de strings (para que a árvore não seja deslocada para a direita quando há elementos grandes)
                while(k++ < esp_meio-dif) System.out.print(" ");
                j++;
            }

            System.out.println();
            int espaco_mid = 1;//o número inicial de espaços entre cada par de ramos
            int espaco_esq = esp_meio/2-1;//o número inicial de espaços à esquerda do ramo da esquerda e à direita do ramo da direita
            
            if (i + pagina < array.length) //para não desenhar ramos nas folhas
            {
                for (j=0;j<v_space;j++)//para cada linha da altura dos ramos atuais
                {
                    for(int rep=0;rep<n_reps_h;rep++)//para cada repetição dos ramos neste nível
                    {
                        k=0;
                        while(k++ < espaco_esq) System.out.print(" ");//imprime espaços antes do ramo esquerdo
                        System.out.print(GREEN+"/");//ramo esquerdo
                        k=0;
                        while(k++ < espaco_mid) System.out.print(" ");//espaços entre os 2 ramos
                        System.out.print("\\");//ramo direito
                        k=0;
                        while(k++ < espaco_esq+1) System.out.print(" ");//espaços à direita do ramo direito
                    }
                    System.out.println();

                    espaco_esq--;//em cada linha, o número de espaços à esquerda diminui
                    espaco_mid+=2;//em cada linha, o número de espaços entre cada par de ramos aumenta em 2
                }
            }
            v_space /=2;//em cada nível a altura dos ramos passa a metade
            n_reps_h*=2;//em cada nível, o número de pares de ramos duplica
            esp_meio /= 2;//em cada nível, o número de espaços entre cada dois nós passa a metade
            esp_i /= 2;//em cada nível o nº de espaços iniciais passa a metade
            i+=pagina;//passar ao nível seguinte da árvore porque o ciclo interno já tratou de todos os elementos deste nível
            pagina*=2;//em cada nível, o número de elementos duplica
        }
    }
    
    /**
     * Converte uma AVL implementada com listas ligadas numa AVL implementada em array
     * @param treeToConvert a raiz da AVL a converter
     * @return um array com os nós da AVL
     */
    public static AVLNode[] toArray(AVLNode treeToConvert)
    {
        AVLNode[] array = new AVLNode[(int)Math.pow(2, altura(treeToConvert)) -1];
        toArrayAux((AVLNode) treeToConvert, array, 0);
        return array;
    }
    
    private static void toArrayAux(AVLNode node, AVLNode[] array, int indice)
    {
        if (node == null) return;
        
        array[indice] = node;
        toArrayAux((AVLNode) node.left, array, 2*indice + 1);
        toArrayAux((AVLNode) node.right, array, 2*(indice + 1));
    }
    
    /**
     * Devolve um Iterator level order sobre a árvore
     * @return um Iterator level order
     */
    public Iterator<BinaryTreeNode<T>> AVLLevelOrderToString() 
   {
      
      ArrayUnorderedList<BinaryTreeNode<T>> nodes = new ArrayUnorderedList<>();
      ArrayUnorderedList<BinaryTreeNode<T>> tempList = new ArrayUnorderedList<>();
      
      if (root != null)
        nodes.addToRear(root);
      return aux(nodes, tempList).iterator();
   }
   
   private ArrayUnorderedList<BinaryTreeNode<T>> aux(ArrayUnorderedList<BinaryTreeNode<T>> queue, ArrayUnorderedList<BinaryTreeNode<T>> result)
   {
       if (queue.isEmpty()) return result;
       
       BinaryTreeNode<T> current = queue.removeFirst();
       if (current.left!=null)
           queue.addToRear (current.left);
       if (current.right!=null)
           queue.addToRear (current.right);
       result.addToRear(current);
       
       return aux(queue, result);
   }    
}
