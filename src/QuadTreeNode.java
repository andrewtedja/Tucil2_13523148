class QuadTreeNode {
    private int x, y;
    private int width;
    private int height;
    private boolean isLeaf;

    private QuadTreeNode topLeft;
    private QuadTreeNode topRight;
    private QuadTreeNode bottomLeft;
    private QuadTreeNode bottomRight;

    private int avgRed;
    private int avgGreen;
    private int avgBlue;

    // ctor
    public QuadTreeNode(int x, int y, int width, int height){ 
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isLeaf = true;

        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }
    
    public void split() {
        int newWidth = width / 2;
        int newHeight = height / 2;

        topLeft = new QuadTreeNode(x, y, newWidth, newHeight);
        topRight = new QuadTreeNode(x + newWidth, y, newWidth, newHeight);
        bottomLeft = new QuadTreeNode(x, y + newHeight, newWidth, newHeight);
        bottomRight = new QuadTreeNode(x + newWidth,y + newHeight, newWidth, newHeight);
        isLeaf = false;
    }

    // getter setter
    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public boolean getIsLeaf() { return this.isLeaf; }
    public QuadTreeNode getTopLeft() { return this.topLeft; }
    public QuadTreeNode getTopRight() { return this.topRight; }
    public QuadTreeNode getBottomLeft() { return this.bottomLeft; }
    public QuadTreeNode getBottomRight() { return this.bottomRight; }
}