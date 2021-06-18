package cacao.components;

import cacao.renderer.Transform;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private String name;
    private List<Component> components;
    public Transform transform;

    public GameObject(String name) {
       init(name, new Transform());
    }

    public GameObject(String name, Transform transform) {
        init(name, transform);
    }

    private void init(String name, Transform transform) {
        this.name = name;
        this.transform = transform;
        this.components = new ArrayList<>();
    }

    public <T extends Component>  T getComponent(Class<T> componentClass) {

       for (Component c : components) {

           if (!componentClass.isAssignableFrom(c.getClass())) {
               continue;
           }

           try {
               return componentClass.cast(c);
           } catch (ClassCastException e) {
               e.printStackTrace();
               assert false : "Error: casting component";
           }

       }

       return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);

            if (!componentClass.isAssignableFrom(c.getClass())) {
                continue;
            }

            components.remove(i);

            // It's removed, no need to continue
            return;
        }
    }

    public void addComponent(Component c) {
        this.components.add(c);
        c.gameObject = this;
    }

    public void update(float dt) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).update(dt);
        }
    }

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }

}
