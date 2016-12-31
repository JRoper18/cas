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
}
