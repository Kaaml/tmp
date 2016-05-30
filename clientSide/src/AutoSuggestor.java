/**
 * Created by kaaml on 30.05.16.
 */

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

class AutoSuggestor {

    private final JTextComponent textComp;
    private final Window container;
    private JPanel suggestionsPanel;
    private JWindow autoSuggestionPopUpWindow;
    private String typedWord;
    private final ArrayList<String> dictionary = new ArrayList<>();
    private int currentIndexOfSpace, tW, tH;
    private DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }
    };
    private final Color suggestionsTextColor;
    private final Color suggestionFocusedColor;

    public AutoSuggestor(JTextComponent textComp, Window mainWindow, ArrayList<String> words, Color popUpBackground, Color textColor, Color suggestionFocusedColor, float opacity) {
        this.textComp = textComp;
        this.suggestionsTextColor = textColor;
        this.container = mainWindow;
        this.suggestionFocusedColor = suggestionFocusedColor;
        this.textComp.getDocument().addDocumentListener(documentListener);

        setDictionary(words);

        typedWord = "";
        currentIndexOfSpace = 0;
        tW = 0;
        tH = 0;

        autoSuggestionPopUpWindow = new JWindow(mainWindow);
        //autoSuggestionPopUpWindow.setOpacity(opacity);

        suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new GridLayout(0, 1));
        suggestionsPanel.setBackground(popUpBackground);

        addKeyBindingToRequestFocusInPopUpWindow();
    }

    private void addKeyBindingToRequestFocusInPopUpWindow() {
        textComp.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
        textComp.getActionMap().put("Down released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {//focuses the first label on popwindow
                for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
                    if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
                        ((SuggestionLabel) suggestionsPanel.getComponent(i)).setFocused(true);
                        autoSuggestionPopUpWindow.toFront();
                        autoSuggestionPopUpWindow.requestFocusInWindow();
                        suggestionsPanel.requestFocusInWindow();
                        suggestionsPanel.getComponent(i).requestFocusInWindow();
                        break;
                    }
                }
            }
        });
        suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
        suggestionsPanel.getActionMap().put("Down released", new AbstractAction() {
            int lastFocusableIndex = 0;

            @Override
            public void actionPerformed(ActionEvent ae) {//allows scrolling of labels

                ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
                int max = sls.size();

                if (max > 1) {//more than 1 suggestion
                    for (int i = 0; i < max; i++) {
                        SuggestionLabel sl = sls.get(i);
                        if (sl.isFocused()) {
                            if (lastFocusableIndex == max - 1) {
                                lastFocusableIndex = 0;
                                sl.setFocused(false);
                                autoSuggestionPopUpWindow.setVisible(false);
                                setFocusToTextField();
                                checkForAndShowSuggestions();

                            } else {
                                sl.setFocused(false);
                                lastFocusableIndex = i;
                            }
                        } else if (lastFocusableIndex <= i) {
                            if (i < max) {
                                sl.setFocused(true);
                                autoSuggestionPopUpWindow.toFront();
                                autoSuggestionPopUpWindow.requestFocusInWindow();
                                suggestionsPanel.requestFocusInWindow();
                                suggestionsPanel.getComponent(i).requestFocusInWindow();
                                lastFocusableIndex = i;
                                break;
                            }
                        }
                    }
                } else {//only a single suggestion was given
                    autoSuggestionPopUpWindow.setVisible(false);
                    setFocusToTextField();
                    checkForAndShowSuggestions();   //fire method as if document listener change occured and fired it
                }
            }
        });
    }

    private void setFocusToTextField() {
        container.toFront();
        container.requestFocusInWindow();
        textComp.requestFocusInWindow();
    }

    public ArrayList<SuggestionLabel> getAddedSuggestionLabels() {
        ArrayList<SuggestionLabel> sls = new ArrayList<>();
        for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
            if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
                SuggestionLabel sl = (SuggestionLabel) suggestionsPanel.getComponent(i);
                sls.add(sl);
            }
        }
        return sls;
    }

    private void checkForAndShowSuggestions() {
        typedWord = getCurrentlyTypedWord();

        suggestionsPanel.removeAll();   //remove older suggestions

        tW = 0;
        tH = 0;

        boolean added = wordTyped(typedWord);

        if (!added) {
            if (autoSuggestionPopUpWindow.isVisible()) {
                autoSuggestionPopUpWindow.setVisible(false);
            }
        } else {
            showPopUpWindow();
            setFocusToTextField();
        }
    }

    protected void addWordToSuggestions(String word) {
        SuggestionLabel suggestionLabel = new SuggestionLabel(word, suggestionFocusedColor, suggestionsTextColor, this);

        calculatePopUpWindowSize(suggestionLabel);

        suggestionsPanel.add(suggestionLabel);
    }

    public String getCurrentlyTypedWord() {//get newest word after last white spaceif any or the first word if no white spaces
        String text = textComp.getText();
        String wordBeingTyped = "";
        text = text.replaceAll("(\\r|\\n)", " ");
        if (text.contains(" ")) {
            int tmp = text.lastIndexOf(" ");
            if (tmp >= currentIndexOfSpace) {
                currentIndexOfSpace = tmp;
                wordBeingTyped = text.substring(text.lastIndexOf(" "));
            }
        } else {
            wordBeingTyped = text;
        }
        return wordBeingTyped.trim();
    }

    private void calculatePopUpWindowSize(JLabel label) {
        if (tW < label.getPreferredSize().width) {
            tW = label.getPreferredSize().width;
        }
        tH += label.getPreferredSize().height;
    }

    private void showPopUpWindow() {
        autoSuggestionPopUpWindow.getContentPane().add(suggestionsPanel);
        autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textComp.getWidth(), 30));
        autoSuggestionPopUpWindow.setSize(tW, tH);
        autoSuggestionPopUpWindow.setVisible(true);

        int windowX = 0;
        int windowY = 0;

        if (textComp instanceof JTextField) {
            windowX = container.getX() + textComp.getX() + 5;
            if (suggestionsPanel.getHeight() > autoSuggestionPopUpWindow.getMinimumSize().height) {
                windowY = container.getY() + textComp.getY() + textComp.getHeight() + autoSuggestionPopUpWindow.getMinimumSize().height;
            } else {
                windowY = container.getY() + textComp.getY() + textComp.getHeight() + autoSuggestionPopUpWindow.getHeight();
            }
        } else {
            Rectangle rect = null;
            try {
                rect = textComp.getUI().modelToView(textComp, textComp.getCaret().getDot());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            //String arr[] = { "adafd", "asdfa", "fadsf"};

            windowX = (int) (rect.getX() + 15);
            windowY = (int) (rect.getY() + (rect.getHeight() * 3));
        }

        //show the pop up
        autoSuggestionPopUpWindow.setLocation(windowX, windowY);
        autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textComp.getWidth(), 30));
        autoSuggestionPopUpWindow.revalidate();
        autoSuggestionPopUpWindow.repaint();

    }

    public void setDictionary(ArrayList<String> words) {
        dictionary.clear();
        if (words == null) {
            return;//so we can call constructor with null value for dictionary without exception thrown
        }
        for (String word : words) {
            dictionary.add(word);
        }
    }

    public JWindow getAutoSuggestionPopUpWindow() {
        return autoSuggestionPopUpWindow;
    }

    public Window getContainer() {
        return container;
    }

    public JTextComponent getTextField() {
        return textComp;
    }

    public void addToDictionary(String word) {
        dictionary.add(word);
    }

    boolean wordTyped(String typedWord) {

        if (typedWord.isEmpty()) {
            return false;
        }
        //System.out.println("Typed word: " + typedWord);

        boolean suggestionAdded = false;
    try {
        for (String word : dictionary) {//get words in the dictionary which we added
            boolean fullymatches = true;
            for (int i = 0; i < typedWord.length(); i++) {//each string in the word
                if (word.length() < i)
                    if (!typedWord.toLowerCase().startsWith(String.valueOf(word.toLowerCase().charAt(i)), i)) {//check for match
                        fullymatches = false;
                        break;
                    }
            }
            if (fullymatches) {
                addWordToSuggestions(word);
                suggestionAdded = true;
            }
        }
    }catch ( Exception e ){

    }
        return suggestionAdded;
    }
}

class SuggestionLabel extends JLabel {

    private boolean focused = false;
    private final JWindow autoSuggestionsPopUpWindow;
    private final JTextComponent textComponent;
    private final AutoSuggestor autoSuggestor;
    private Color suggestionsTextColor, suggestionBorderColor;

    public SuggestionLabel(String string, final Color borderColor, Color suggestionsTextColor, AutoSuggestor autoSuggestor) {
        super(string);

        this.suggestionsTextColor = suggestionsTextColor;
        this.autoSuggestor = autoSuggestor;
        this.textComponent = autoSuggestor.getTextField();
        this.suggestionBorderColor = borderColor;
        this.autoSuggestionsPopUpWindow = autoSuggestor.getAutoSuggestionPopUpWindow();

        initComponent();
    }

    private void initComponent() {
        setFocusable(true);
        setForeground(suggestionsTextColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                replaceWithSuggestedText();

                autoSuggestionsPopUpWindow.setVisible(false);
            }
        });

        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "Enter released");
        getActionMap().put("Enter released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                replaceWithSuggestedText();
                autoSuggestionsPopUpWindow.setVisible(false);
            }
        });
    }

    public void setFocused(boolean focused) {
        if (focused) {
            setBorder(new LineBorder(suggestionBorderColor));
        } else {
            setBorder(null);
        }
        repaint();
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    private void replaceWithSuggestedText() {
        String suggestedWord = getText();
        String text = textComponent.getText();
        String typedWord = autoSuggestor.getCurrentlyTypedWord();
        String t = text.substring(0, text.lastIndexOf(typedWord));
        String tmp = t + text.substring(text.lastIndexOf(typedWord)).replace(typedWord, suggestedWord);
        textComponent.setText(tmp + " ");
    }
}
