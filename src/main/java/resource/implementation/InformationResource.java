package resource.implementation;

import resource.DBNode;
import resource.DBNodeComposite;

public class InformationResource extends DBNodeComposite {
    public InformationResource(String name) {
        super(name, null);
    }

    @Override
    public void addChild(DBNode child) {
        if (child != null && child instanceof Entity){
            Entity entity = (Entity) child;
            this.getChildren().add(entity);
        }
    }
}
