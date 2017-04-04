package CAS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jack on 12/30/2016.
 */
public class Tree<T> implements Serializable{
    public T data;
    private List<Tree<T>> children = new ArrayList<Tree<T>>();
    private Tree<T> parent;
    public Tree(T rootData){
        data = rootData;
    }
    public Tree(){
    }
    public Tree<T> getParent(){
        return this.parent;
    }
    public List<Tree<T>> getChildren(){
        return this.children;
    }
    public boolean hasChildren(){
        return !this.children.isEmpty();
    }
    public void setChildren(List<Tree<T>> children) {
        for(Tree<T> child : this.children) {
            child.parent = this;
        }
        this.children = children;
    }
    public void setChild(int index, Tree<T> newChild){
        this.children.set(index, newChild);
    }
    public Tree<T> getChild(int index){
        return children.get(index);
    }
    public boolean containsData(T data){
        if(this.data.equals(data)){
            return true;
        }
        if(!this.hasChildren()){
            return false;
        }
        else{
            for(Tree<T> child : children){
                if(child.containsData(data)){
                    return true;
                }
            }
            return false;
        }
    }
    public boolean containsClass(Class find){
        if(!this.hasChildren() && this.data.getClass().equals(find)){
            return true;
        }
        else{
            for(Tree<T> child : children){
                if(child.containsClass(find)){
                    return true;
                }
            }
            return false;
        }
    }
    public List<LinkedList<Integer>> findPaths(T toFind){ //Returns linked list of a route to take to find the said child
        List<LinkedList<Integer>> paths = new ArrayList<LinkedList<Integer>>();
        if (this.hasChildren()) {
            for(int i = 0; i<this.getChildren().size(); i++){
                if(this.getChild(i).data.equals(toFind)){
                    LinkedList<Integer> toReturn = new LinkedList<Integer>();
                    toReturn.add(new Integer(i));
                    paths.add(toReturn);
                }
                List<LinkedList<Integer>> possiblePaths = this.getChild(i).findPaths(toFind);
                if(!possiblePaths.isEmpty()){
                    for(int j = 0; j<possiblePaths.size(); j++){
                        LinkedList<Integer> currentPath = possiblePaths.get(j);
                        currentPath.addFirst(i);
                        paths.add(currentPath);
                    }
                }
            }
        }
        return paths;
    }
    public Tree<T> getChildThroughPath(LinkedList<Integer> path){
        if(path.isEmpty()){
            return this;
        }
        return this.continuePath(path, 0);
    }
    private Tree continuePath(LinkedList<Integer> path, int index){
        if(index == path.size()){
            return this;
        }
        return this.getChild(path.get(index)).continuePath(path, index + 1);
    }
    public void replaceWith(Tree<T> newTree){
        if(this.parent != null){
            newTree.parent = this.parent;
            for(int i = 0; i<this.parent.getNumberOfChildren(); i++){
                Tree<T> sibling = this.parent.getChild(i);
                if(this == sibling){ //Purposely ==, not .equals()
                    this.parent.children.set(i, newTree);
                    break;
                }
            }
        }
        else{
            this.children = newTree.children;
            this.data = newTree.data;
        }
    }
    public boolean isRoot(){
        return this.parent == null;
    }
    public int size(){
        int size = 1;
        if(this.hasChildren()){
            for(Tree<T> child : this.children){
                size += child.size();
            }
        }
        return size;
    }
    public void addChild(Tree<T> child){
        this.children.add(child);
    }
    public void addChildWithData(T data){
        this.children.add(new Tree<T>(data));
    }
    public void addEmptyChild(){
        Tree<T> newChild = new Tree<T>();
        newChild.parent = this;
        this.children.add(newChild);
    }
    public void removeChild(int index){
        this.children.remove(index);
    }
    public int getNumberOfChildren(){
        return this.children.size();
    }
    public Tree<T> clone(){
        Tree<T> newTree = new Tree(this.data);
        for(Tree<T> child: this.children){
            newTree.addChild(child.clone());
        }
        return newTree;
    }
    public void print(){
        this.print(1);
    }
    private void print(int level) {
        for (int i = 1; i < level; i++) {
            System.out.print("\t"); //Newline
        }
        if(this.data == null){
            System.out.println("null");
        }
        else{
            System.out.println(this.data);
        }
        for (Tree child : this.children) {
            child.print(level + 1);
        }
    }
    public void replaceAll(Tree<T> before, Tree<T> after){
        if(this.equals(before)){
            this.replaceWith(after);
            return;
        }
        for(Tree<T> child : children){
            child.replaceAll(before, after);
        }
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Tree){
            //Check the data
            if(!this.data.equals(((Tree) obj).data)){
                return false;
            }
           //Check the number of children
            if(((Tree) obj).getNumberOfChildren() != this.getNumberOfChildren()){
                return false;
            }
            //Actually compare the children
            for(int i = 0; i<this.getNumberOfChildren(); i++){
                if(!this.getChild(i).equals(((Tree) obj).getChild(i))){
                    return false;
                }
            }
            //Made it!
            return true;
        }
        else{
            return false;
        }
    }

}
