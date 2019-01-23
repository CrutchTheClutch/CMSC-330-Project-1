import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, SyntaxError {
        String filepath = new FileChooser().getFilePath();
        Lexer lexer = new Lexer(filepath);
        Parser parser = new Parser(lexer);
        parser.parse();
  }
}
