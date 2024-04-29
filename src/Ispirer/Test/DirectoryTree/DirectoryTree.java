package Ispirer.Test.DirectoryTree;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import java.io.File;

public class DirectoryTree extends JFrame {

    private JTree directoryTree;

    public DirectoryTree() {
        setTitle("Directory Tree Viewer");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создаем корневой узел для дерева каталогов
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

        // Получаем стартовый каталог (текущий, если не указан) и добавляем его к корневому узлу
        String startDirectoryPath = System.getProperty("user.dir");  // Используем текущий каталог по умолчанию
        File startDirectory = new File(startDirectoryPath);
        DefaultMutableTreeNode startNode = addNodes(null, startDirectory);
        root.add(startNode);

        // Создаем дерево каталогов
        directoryTree = new JTree(root);
        JScrollPane treeScrollPane = new JScrollPane(directoryTree);
        add(treeScrollPane);

        // Добавляем слушатель для отображения подкаталогов при развертывании узла
        directoryTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                if (node.getChildCount() == 1) {
                    DefaultMutableTreeNode subNode = (DefaultMutableTreeNode) node.getFirstChild();
                    if (subNode.getUserObject() instanceof String) {
                        subNode.removeAllChildren();
                        addNodes(subNode, new File(subNode.toString()));
                    }
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                // Не используем
            }
        });

        setVisible(true);
    }

    private DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File directory) {
        String curPath = directory.getPath();
        DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
        if (curTop != null) {
            curTop.add(curDir);
        }

        File[] files = directory.listFiles();
        if (files == null) return curDir;

        for (File file : files) {
            if (file.isDirectory()) {
                addNodes(curDir, file);
            } else {
                if (!file.getName().equals(".DS_Store")) { // Исключаем специфичный файл для MacOS
                    curDir.add(new DefaultMutableTreeNode(file.getName()));
                }
            }
        }
        return curDir;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DirectoryTree();
            }
        });
    }
}
