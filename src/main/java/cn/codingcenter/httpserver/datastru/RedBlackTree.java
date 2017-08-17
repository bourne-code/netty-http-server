package cn.codingcenter.httpserver.datastru;

import java.util.ArrayList;
import java.util.List;

public class RedBlackTree<T extends Comparable> {

    private static final boolean COLOR_BLACK = false;
    private static final boolean COLOR_RED = true;
    private TreeNode root = null;
    private List<TreeNode> nodelist = new ArrayList<>();

    private static class TreeNode<T> {


        T key;
        TreeNode leftChild;
        TreeNode rightChild;
        TreeNode parent;
        boolean color;

        public TreeNode(T key) {
            this.key = key;
            this.color = COLOR_BLACK;
        }

        TreeNode(T key, TreeNode leftChild, TreeNode rightChild,
                 TreeNode parent) {
            this.key = key;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.parent = parent;
            this.color = COLOR_BLACK;
        }

        public T getKey() {
            return key;
        }


        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("( ")
                    .append((leftChild == null ? "" : String.valueOf(leftChild.key)))
                    .append(" , ")
                    .append(key)
                    .append(" , ")
                    .append((rightChild == null ? "" : String.valueOf(rightChild.key)))
                    .append(" , ")
                    .append(color)
                    .append(" )");

            return stringBuilder.toString();
        }
    }


    public TreeNode search(T key) {
        TreeNode pNode = root;
        while (pNode != null && pNode.key != key) {
            if (key.compareTo(pNode.key) == -1) {
                pNode = pNode.leftChild;
            } else {
                pNode = pNode.rightChild;
            }
        }
        return pNode;
    }

    /**
     * 在节点nodeX上进行左旋操作
     * <p>
     * 节点nodeX的右节点为nodeY且不为空，以nodeX到nodeY的链为轴进行旋转
     * 使得nodeY成为nodeX子树新的根节点，nodeX成为nodeY的左孩子，nodeY的左孩子成为nodeY的右孩子
     *
     * @param nodeX
     */
    private void leftRotate(TreeNode nodeX) {
        TreeNode nodeY = nodeX.rightChild;
        if(nodeY == null) return;
        nodeX.rightChild = nodeY.leftChild;
        if (nodeY.leftChild != null)
            nodeY.leftChild.parent = nodeX;
        nodeY.parent = nodeX.parent;
        if (nodeX.parent == null)
            root = nodeY;
        else if (nodeX == nodeX.parent.leftChild)
            nodeX.parent.leftChild = nodeY;
        else
            nodeX.parent.rightChild = nodeY;
        nodeY.leftChild = nodeX;
        nodeX.parent = nodeY;
    }

    private void rightRotate(TreeNode nodeX) {
        TreeNode nodeY = nodeX.leftChild;
        if(nodeY == null) return;
        nodeX.leftChild = nodeY.rightChild;
        if (nodeY.rightChild != null)
            nodeY.rightChild.parent = nodeX;
        nodeY.parent = nodeX.parent;
        if (nodeX.parent == null)
            root = nodeY;
        else if (nodeX == nodeX.parent.leftChild)
            nodeX.parent.leftChild = nodeY;
        else
            nodeX.parent.rightChild = nodeY;
        nodeY.rightChild = nodeX;
        nodeX.parent = nodeY;
    }


    public void insert(T key) {
        TreeNode treeNode = new TreeNode(key);
        insertNode(treeNode);
    }

    private void insertNode(TreeNode nodeZ) {
        TreeNode nodeY = null;
        TreeNode nodeX = root;
        while (nodeX != null) {
            nodeY = nodeX;
            if (((T) nodeZ.key).compareTo(nodeX.key) == -1)
                nodeX = nodeX.leftChild;
            else
                nodeX = nodeX.rightChild;
        }
        nodeZ.parent = nodeY;
        if (nodeY == null)
            root = nodeZ;
        else if (((T) nodeZ.key).compareTo(nodeY.key) == -1)
            nodeY.leftChild = nodeZ;
        else
            nodeY.rightChild = nodeZ;
        nodeZ.leftChild = nodeZ.rightChild = null;
        nodeZ.color = COLOR_RED;
        insertFixup(nodeZ);
    }

    private void insertFixup(TreeNode nodeZ) {
        //zp = nodeZ.parent
        //zpp = nodeZ.parent.parent
        //zppl = zpp.leftChild
        //zppr = zpp.rightChild
        TreeNode zp, zpp, zppr, zppl;
        zp = nodeZ.parent;
        //zp == null说明nodeZ是根结点，将颜色置为黑色，直接返回
        if (zp == null) {
            nodeZ.color = COLOR_BLACK;
            return;
        }
        //如果当前插入的结点的parent是红结点，则执行调整
        while (zp != null && zp.color) {
            zpp = zp.parent;
            if (zpp.leftChild == zp) {
                //情况1：当前结点的叔结点是红节点时
                if ((zppr = zpp.rightChild) != null && zppr.color) {
                    zp.color = COLOR_BLACK;
                    zppr.color = COLOR_BLACK;
                    zpp.color = COLOR_RED;
                    nodeZ = zpp;
                } else if (nodeZ == zp.rightChild) {
                    //情况2：当前结点的叔结点为黑色，且当前结点是其双亲的右孩子
                    //情况2：当前结点的叔结点为黑色，且当前结点是其双亲的左孩子
                    //都是通过左旋来处理
                    nodeZ = zp;
                    leftRotate(nodeZ);
                }
                zpp = (zp = nodeZ.parent) == null ? null : zp.parent;
                if (zp != null) {
                    zp.color = COLOR_BLACK;
                    if (zpp != null) {
                        zpp.color = COLOR_RED;
                        rightRotate(zpp);
                    }
                }
            } else {
                //类似于上面的情况，只不过左右交换
                if ((zppl = zpp.leftChild) != null && zppl.color) {
                    zp.color = COLOR_BLACK;
                    zppl.color = COLOR_BLACK;
                    zpp.color = COLOR_RED;
                    nodeZ = zpp;
                } else if (nodeZ == zp.leftChild) {
                    nodeZ = zp;
                    rightRotate(nodeZ);
                }
                zpp = (zp = nodeZ.parent) == null ? null : zp.parent;
                if (zp != null) {
                    zp.color = COLOR_BLACK;
                    if (zpp != null) {
                        zpp.color = COLOR_RED;
                        leftRotate(zpp);
                    }
                }
            }
        }
        root.color = COLOR_BLACK;
    }

    public void delete(T key) {
        TreeNode pNode = search(key);
        if(pNode == null)
            return;
        delete(pNode);
    }

    private void delete(TreeNode nodeZ) {
        TreeNode nodeY = nodeZ;
        TreeNode nodeX;
        boolean nodeYColor = nodeY.color;
        if (nodeZ.leftChild == null) {
            nodeX = nodeZ.rightChild;
            transplant(nodeZ, nodeZ.rightChild);
        } else if (nodeZ.rightChild == null) {
            nodeX = nodeZ.leftChild;
            transplant(nodeZ, nodeZ.leftChild);
        } else {
            nodeY = minElemNode(nodeZ.rightChild);
            nodeYColor = nodeY.color;
            nodeX = nodeY.rightChild;
            if (nodeY.parent == nodeZ) {
                if (nodeX != null)
                    nodeX.parent = nodeY;
            } else {
                transplant(nodeY, nodeY.rightChild);
                nodeY.rightChild = nodeZ.rightChild;
                if(nodeY.rightChild != null)
                    nodeY.rightChild.parent = nodeY;
            }
            transplant(nodeZ, nodeY);
            nodeY.leftChild = nodeZ.leftChild;
            nodeY.leftChild.parent = nodeY;
            nodeY.color = nodeZ.color;
        }
        if (!nodeYColor)
            deleteFixup(nodeX);
    }

    private void deleteFixup(TreeNode nodeX) {
        if (nodeX == null)
            return;
        while (nodeX != root && nodeX != null && !nodeX.color) {
            if (nodeX == nodeX.parent.leftChild) {
                TreeNode nodeW = nodeX.parent.rightChild;
                if (nodeW.color) {
                    nodeW.color = COLOR_BLACK;
                    nodeX.parent.color = COLOR_RED;
                    leftRotate(nodeX.parent);
                    nodeW = nodeX.parent.rightChild;
                }
                if (!nodeW.leftChild.color && !nodeW.rightChild.color) {
                    nodeW.color = COLOR_RED;
                } else if (!nodeW.rightChild.color) {
                    nodeW.leftChild.color = COLOR_BLACK;
                    nodeW.color = COLOR_RED;
                    rightRotate(nodeW);
                    nodeW = nodeX.parent.rightChild;
                }
                nodeW.color = nodeX.parent.color;
                nodeX.parent.color = COLOR_BLACK;
                nodeW.rightChild.color = COLOR_BLACK;
                nodeX = root;
            } else {
                TreeNode nodeW = nodeX.parent.rightChild;
                if (nodeW.color) {
                    nodeW.color = COLOR_BLACK;
                    nodeX.parent.color = COLOR_RED;
                    leftRotate(nodeX.parent);
                    nodeW = nodeX.parent.leftChild;
                }
                if (!nodeW.rightChild.color && !nodeW.leftChild.color) {
                    nodeW.color = COLOR_RED;
                } else if (!nodeW.leftChild.color) {
                    nodeW.rightChild.color = COLOR_BLACK;
                    nodeW.color = COLOR_RED;
                    rightRotate(nodeW);
                    nodeW = nodeX.parent.leftChild;
                }
                nodeW.color = nodeX.parent.color;
                nodeX.parent.color = COLOR_BLACK;
                nodeW.leftChild.color = COLOR_BLACK;
                nodeX = root;
            }
        }
        nodeX.color = COLOR_BLACK;
    }

    private void transplant(TreeNode srcNode, TreeNode targetNode) {
        if (srcNode.parent == null)
            root = targetNode;
        else if (srcNode == srcNode.parent.leftChild)
            srcNode.parent.leftChild = targetNode;
        else
            srcNode.parent.rightChild = targetNode;
        if (targetNode != null)
            targetNode.parent = srcNode.parent;
    }

    public List<TreeNode> inOrderTraverseList() {
        if (nodelist != null) {
            nodelist.clear();
        }
        inOrderTraverse(root);
        return nodelist;
    }

    public TreeNode minElemNode(TreeNode node) {
        if (node == null) {
            return null;
        }
        TreeNode pNode = node;
        while (pNode.leftChild != null) {
            pNode = pNode.leftChild;
        }
        return pNode;
    }

    public T getMinElem() {
        return (T) minElemNode(root).key;
    }

    /**
     * inOrderTraverse: 对给定二叉查找树进行中序遍历
     *
     * @param root 给定二叉查找树的根结点
     */
    private void inOrderTraverse(TreeNode root) {
        if (root != null) {
            inOrderTraverse(root.leftChild);
            nodelist.add(root);
            inOrderTraverse(root.rightChild);
        }
    }

    public String toStringOfOrderList() {
        StringBuilder sbBuilder = new StringBuilder(" [ ");
        for (TreeNode p : inOrderTraverseList()) {
            sbBuilder.append(p);
        }
        sbBuilder.deleteCharAt(sbBuilder.length() - 2);
        sbBuilder.append("]");

        return sbBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder sbBuilder = new StringBuilder("[ ");
        for (TreeNode p : inOrderTraverseList()) {
            sbBuilder.append(p);
            sbBuilder.append(", ");
        }
        if(sbBuilder.length() > 3)
            sbBuilder.deleteCharAt(sbBuilder.length() - 2);
        sbBuilder.append("]");
        return sbBuilder.toString();
    }

//    public static void main(String[] args) {
//
//        RedBlackTree rbt = new RedBlackTree();
//        Random random = new Random();
//        for (int i = 0; i < 100; i++) {
//            rbt.insert(new FileCounter(random.nextInt(100)));
//        }
//        System.out.println(rbt.getMinElem());
//    }
}
