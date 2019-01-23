import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

class FileChooser {
    private JFrame frame;
    private FileFilter filter;

    FileChooser() {
        frame = new JFrame();
        filter = new FileNameExtensionFilter("Text File","txt");
        frame.setVisible(true);
    }

    String getFilePath() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(filter);
        if(JFileChooser.APPROVE_OPTION == jFileChooser.showOpenDialog(null)){
            frame.setVisible(false);
            return jFileChooser.getSelectedFile().getAbsolutePath();
        }else {
            System.out.println("Next time select a file.");
            System.exit(1);
        }
        return null;
    }
}
