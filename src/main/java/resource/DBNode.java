package resource;

public abstract class DBNode {

    private String name;
    private DBNode parent;

    public DBNode() {
    }

    public DBNode(String name, DBNode parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DBNode) {
            DBNode otherObj = (DBNode) obj;
            return this.getName().equals(otherObj.getName());
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DBNode getParent() {
        return parent;
    }

    public void setParent(DBNode parent) {
        this.parent = parent;
    }
}
