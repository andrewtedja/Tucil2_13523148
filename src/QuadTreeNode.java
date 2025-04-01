class QuadTreeNode {
    private int x, y;
    private int width;
    private int height;
    private boolean isLeaf;

    private QuadTreeNode topLeft;
    private QuadTreeNode topRight;
    private QuadTreeNode bottomLeft;
    private QuadTreeNode bottomRight;


    private double meanR, meanG, meanB;
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
        int leftWidth = width / 2;
        int rightWidth = width - leftWidth;
        int topHeight = height / 2;
        int bottomHeight = height - topHeight;

        topLeft = new QuadTreeNode(x, y, leftWidth, topHeight);
        topRight = new QuadTreeNode(x + leftWidth, y, rightWidth, topHeight);
        bottomLeft = new QuadTreeNode(x, y + topHeight, leftWidth, bottomHeight);
        bottomRight = new QuadTreeNode(x + leftWidth,y + topHeight, rightWidth, bottomHeight);
        isLeaf = false;
    }

    // getter setter
    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }

    public double getMeanR() { return this.meanR; }
    public double getMeanG() { return this.meanG; }
    public double getMeanB() { return this.meanB; }
    public void setMeanValues(double meanR, double meanG, double meanB) {
        this.meanR = meanR;
        this.meanG = meanG;
        this.meanB = meanB;
    }

    public boolean getIsLeaf() { return this.isLeaf; }
    public QuadTreeNode getTopLeft() { return this.topLeft; }
    public QuadTreeNode getTopRight() { return this.topRight; }
    public QuadTreeNode getBottomLeft() { return this.bottomLeft; }
    public QuadTreeNode getBottomRight() { return this.bottomRight; }
}
