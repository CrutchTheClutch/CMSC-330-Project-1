import javax.swing.*;
import java.awt.*;
import java.io.IOException;

class Parser {
    private Lexer lexer;
    private Token expectedToken;
    private Token token;
    private JFrame window;
    private String string;
    private ButtonGroup group;

    Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    void parse() throws IOException, SyntaxError {
        token = lexer.getNextToken();
        if (parseGUI()) {
            window.setVisible(true);
        } else {
            throw new SyntaxError(lexer.lineNo()," Expecting Token " + expectedToken + " not " + token);
        }
    }

    private boolean evalToken(Token evalToken) throws IOException, SyntaxError {
        if (token == evalToken) {
            switch (token) {
                case BUTTON:
                case END:
                case FLOW:
                case GRID:
                case LABEL:
                case LAYOUT:
                case PANEL:
                case RADIO:
                case TEXTFIELD:
                case WINDOW:
                case COMMA:
                case COLON:
                case LEFT_PAREN:
                case RIGHT_PAREN:
                    token = lexer.getNextToken();
                    break;
                case GROUP:
                    group = new ButtonGroup();
                    token = lexer.getNextToken();
                    break;
                case STRING:
                    string = lexer.getLexeme();
                    token = lexer.getNextToken();
                    break;
                case SEMICOLON:
                    token = lexer.getNextToken();
            }
            return true;
        } else {
            expectedToken = evalToken;
            return false;
        }
    }

    private boolean parseGUI() throws IOException, SyntaxError {
        int width;
        int height;
        if (evalToken(Token.WINDOW)) {
            if (evalToken(Token.STRING)) {
                window = new JFrame(string);
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JPanel main = new JPanel();
                if (evalToken(Token.LEFT_PAREN)) {
                    if (evalToken(Token.NUMBER)) {
                        width = (int) lexer.getValue();
                        token = lexer.getNextToken();
                        if (evalToken(Token.COMMA)) {
                            if (evalToken(Token.NUMBER)) {
                                height = (int) lexer.getValue();
                                token = lexer.getNextToken();
                                if (evalToken(Token.RIGHT_PAREN)) {
                                    window.setSize(width,height);
                                    main.setSize(width,height);
                                    window.add(main);
                                    if (parseLayout(main)) {
                                        if (parseWidgets(main)) {
                                            if (evalToken(Token.END)) {
                                                return evalToken(Token.PERIOD);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean parseLayout(Container container) throws IOException, SyntaxError {
        if (evalToken(Token.LAYOUT)) {
            if (parseLayoutType(container)) {
                return evalToken(Token.COLON);
            }
        }
        return false;
    }

    private boolean parseLayoutType(Container container) throws IOException, SyntaxError {
        int rows;
        int cols;
        int hgap;
        int vgap;
        if (evalToken(Token.FLOW)) {
            container.setLayout(new FlowLayout());
            return true;
        }
        else if (evalToken(Token.GRID)) {
            if (evalToken(Token.LEFT_PAREN)) {
                if (evalToken(Token.NUMBER)) {
                    rows = (int) lexer.getValue();
                    token = lexer.getNextToken();
                    if (evalToken(Token.COMMA)) {
                        if (evalToken(Token.NUMBER)) {
                            cols = (int) lexer.getValue();
                            token = lexer.getNextToken();
                            if (evalToken(Token.RIGHT_PAREN)) {
                                container.setLayout(new GridLayout(rows,cols));
                                return true;
                            }
                            else if (evalToken(Token.COMMA)) {
                                if (evalToken(Token.NUMBER)) {
                                    hgap = (int) lexer.getValue();
                                    token = lexer.getNextToken();
                                    if (evalToken(Token.COMMA)) {
                                        if (evalToken(Token.NUMBER)) {
                                            vgap = (int) lexer.getValue();
                                            token = lexer.getNextToken();
                                            if (evalToken(Token.RIGHT_PAREN)) {
                                                container.setLayout(new GridLayout(rows,cols,hgap,vgap));
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean parseWidgets(Container container) throws IOException, SyntaxError {
        if (parseWidget(container)) {
            if (parseWidgets(container)) {
                return true;
            }
            return true;
        }
        return false;
    }

    private boolean parseWidget(Container container) throws IOException, SyntaxError {
        int cols;
        if (evalToken(Token.BUTTON)) {
            if (evalToken(Token.STRING)) {
                if (evalToken(Token.SEMICOLON)) {
                    container.add(new JButton(string));
                    return true;
                }
            }
        }
        else if (evalToken(Token.GROUP)) {
            if (parseRadioButtons(container)) {
                return endWidget();
            }
        }
        else if (evalToken(Token.LABEL)) {
            if (evalToken(Token.STRING)) {
                if (evalToken(Token.SEMICOLON)) {
                    container.add(new JLabel(string));
                    return true;
                }
            }
        }
        else if (evalToken(Token.PANEL)) {
            container.add(container = new JPanel());
            if (parseLayout(container)) {
                if (parseWidgets(container)) {
                    return endWidget();
                }
            }
        }
        else if (evalToken(Token.TEXTFIELD)) {
            if (evalToken(Token.NUMBER)) {
                cols = (int) lexer.getValue();
                token = lexer.getNextToken();
                if (evalToken(Token.SEMICOLON)) {
                    container.add(new JTextField(cols));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean endWidget() throws IOException, SyntaxError {
        if (evalToken(Token.END)) {
            return evalToken(Token.SEMICOLON);
        }
        return false;
    }

    private boolean parseRadioButtons(Container container) throws IOException, SyntaxError {
        if (parseRadioButton(container)) {
            if (parseRadioButtons(container)) {
                return true;
            }
            return true;
        }
        return false;
    }

    private boolean parseRadioButton(Container container) throws IOException, SyntaxError {
        if (evalToken(Token.RADIO)) {
            if (evalToken(Token.STRING)) {
                if (evalToken(Token.SEMICOLON)) {
                    JRadioButton rButton = new JRadioButton(string);
                    container.add(rButton);
                    group.add(rButton);
                    return true;
                }
            }
        }
        return false;
    }
}
