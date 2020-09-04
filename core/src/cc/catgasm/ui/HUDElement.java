package cc.catgasm.ui;

public interface HUDElement {
    void resize(int w, int h);
    void update();
    void dispose();
}
