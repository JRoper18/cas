import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 12/30/2016.
 */
public class Tree<T> {
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
        return this.children.isEmpty();
    }
    public void setChildren(List<Tree<T>> children) {
        for(Tree<T> child : this.children) {
            child.parent = this;
        }
        this.children = children;
    }
    public Tree<T> getChild(int index){
        return children.get(index);
    }
    public boolean containsData(T data){
        if(!this.hasChildren() && this.data == data){
            return true;
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
    public int getNumberOfChildren(){
        return this.children.size();
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
