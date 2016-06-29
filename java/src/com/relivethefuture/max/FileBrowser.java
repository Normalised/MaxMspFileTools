package com.relivethefuture.max;

import java.io.File;
import java.util.ArrayList;

import com.cycling74.max.DataTypes;
import com.cycling74.max.Executable;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxSystem;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBrowser extends MaxObject implements ChangeHandler, SelectionChangeListener, Executable {

    private final String rootPath;
    Logger logger = LoggerFactory.getLogger(FileBrowser.class);

    private static final int COLUMN_WIDTH = 7;
    private static final String CONTENTPANE_SPRITE = "contentpane";

    private static final int LEFT_MARGIN = 4;
    private static final int TOP_MARGIN = 2;
    private static final int ROW_HEIGHT = 16;

    private static final int EXPANDER_SIZE = 16;

    private static final String[] INLET_ASSIST = new String[]{"messages in"};
    private static final String[] OUTLET_ASSIST = new String[]{"connect to lcd", "filename out", "filename no extension", "ready"};

    private static final int NEXT_KEY = 31;
    private static final int PREV_KEY = 30;
    private static final int OPEN_KEY = 29;
    private static final int CLOSE_KEY = 28;
    private static final int FIRST_KEY = 1;
    private static final int LAST_KEY = 4;

    private int contentPaneXOffset = 0;
    private int contentPaneYOffset = 0;

    private int width = 0;
    private int height = 0;

    private File root;
    private LCDWrapper lcd;
    private TreeModel model;

    // TODO : pull this kind of stuff up into an LCDComponent class
    private RGBColour backgroundColour = new RGBColour(255, 255, 255);
    private RGBColour foregroundColour = new RGBColour(0, 0, 0);
    private RGBColour selectionColour = new RGBColour(160, 200, 255);

    private boolean firstMousedown = true;
    private boolean handleReleaseAsClick;

    private FileTypeFilter filter;
    private Scrollbar scrollbar;
    private int visibleRows;
    private boolean keysEnabled = true;
    private String fontName = "Arial";

    public FileBrowser(String path, int w, int h) {

        BasicConfigurator.configure();
        declareInlets(new int[]{DataTypes.ALL});
        declareOutlets(new int[]{DataTypes.ALL, DataTypes.ALL, DataTypes.ALL, DataTypes.ALL});

        setInletAssist(INLET_ASSIST);
        setOutletAssist(OUTLET_ASSIST);

        filter = new FileTypeFilter("wav");
        lcd = new LCDWrapper(this);

        width = w;
        height = h;
        visibleRows = h / ROW_HEIGHT;

        lcd.init(width, height);

        scrollbar = new Scrollbar(lcd);
        scrollbar.setChangeHandler(this);
        scrollbar.setPosition(width - scrollbar.getWidth(), 0);

        rootPath = path;
        logger.debug("FileBrowser ---- Ready");
        MaxSystem.scheduleDelay(this, 50.);
    }

    public void initialize() {
        logger.debug("Initialize");
        draw();
    }

    private void draw() {

        logger.debug("Draw");
        lcd.setBRGB(backgroundColour);
        lcd.clear();

        lcd.beginSprite(CONTENTPANE_SPRITE);
        lcd.setFont(fontName, 10, null);
        render();
        lcd.endSprite();

        lcd.drawSprite(CONTENTPANE_SPRITE, contentPaneXOffset, 0);

        lcd.spriteToFront(scrollbar.getSpriteName());
    }

    private void render() {
        //Iterator<TreeNode> i = model.nodeList.iterator();

        ArrayList<TreeNode> nodes = model.nodeList;
        int row = 0;

        int offset = scrollbar.getTopRow();
        logger.debug("Render " + model.nodeList.size() + " nodes. Visible " + visibleRows + ". Offset " + offset);

        for(int i=0;i<visibleRows;i++) {
            row = i + offset;
            if(row >= nodes.size()) {
                break;
            }
            if (row == model.getSelectedIndex()) {
                renderSelectionBox(i);
            }
            renderNode(nodes.get(row), i);
        }
    }

    private void renderSelectionBox(int row) {
        lcd.setFRGB(selectionColour);
        lcd.paintRect(0, (row * ROW_HEIGHT) - 1, width, ROW_HEIGHT);
    }

    private void renderNode(TreeNode node, int row) {
        if (node.isFile) {
            drawFile(node, row);
        } else {
            drawDirectory(node, row);
        }
    }

    private void drawDirectory(TreeNode node, int row) {
        Integer x = (node.depth * COLUMN_WIDTH) + LEFT_MARGIN;
        Integer y = (row * ROW_HEIGHT) + TOP_MARGIN;

        lcd.setFRGB(foregroundColour);
        drawExpanderBox(!node.open, x, y);
        lcd.drawString(node.name, x + EXPANDER_SIZE, y + 8);
    }

    private void drawFile(TreeNode node, int row) {
        Integer x = (node.depth * COLUMN_WIDTH) + LEFT_MARGIN;
        Integer y = (row * ROW_HEIGHT) + TOP_MARGIN;

        lcd.setFRGB(foregroundColour);
        lcd.drawString(node.name, x, y + 8);
    }

    private void drawExpanderBox(Boolean canExpand, int x, int y) {
        lcd.frameRect(x, y, 10, 10);

        if (canExpand) {
            // draw plus
            lcd.lineSegment(x + 2, y + 4, x + 6, y + 4);
            lcd.lineSegment(x + 4, y + 6, x + 4, y + 2);

        } else {
            // draw minus
            lcd.lineSegment(x + 2, y + 4, x + 7, y + 4);
        }
    }

    private TreeNode getNodeAtPosition(int x, int y) {
        int row = (y / ROW_HEIGHT) + scrollbar.getTopRow();
        logger.debug("Clicked on row " + row);
        logger.debug("Row Count " + model.rowCount());
        if (row <= model.rowCount()) {
            return model.getNodeAtRow(row);
        } else {
            return null;
        }
    }

    private void mouseClicked(int x, int y) {
        if (scrollbar.isVisible()) {
            scrollbar.onMouseUp(x, y);
        }

        if (x <= (width - scrollbar.getWidth())) {
            selectNodeAt(x, y);
        }
    }

    private void selectNodeAt(int x, int y) {
        logger.debug("Select node at " + x + ", " + y);
        TreeNode node = getNodeAtPosition(x, y);
        if (node != null) {
            model.setSelectedNode(node);
            if (!node.isFile) {
                toggleSelectedNode();
            }
        }
    }

    private void redraw() {
        logger.debug("Redraw");
        lcd.reset();
        draw();
    }

    /* (non-Javadoc)
     * @see ChangeHandler#positionChanged(int)
     *
     * Events from the scrollbar
     */
    public void positionChanged(int position) {
        logger.debug("Scroll Position Changed " + position + " : " + visibleRows + " : " + model.rowCount());
        draw();
    }

    /**
     * Selection change from the model
     */
    public void selectionChanged(TreeNode node) {
        int diff = model.getSelectedIndex() - scrollbar.getTopRow();
        logger.debug("Selection Changed: " + diff + " : " + visibleRows);
        if (diff < 0) {
            scrollbar.scrollBy(diff);
        } else if (diff >= visibleRows) {
            scrollbar.scrollBy(diff - visibleRows + 1);
        }

        if (node.isFile) {
            // outlet if it is a file(terminal node)
            String path = node.file.getAbsolutePath();
            String maxpath = MaxSystem.nameConform(path, MaxSystem.PATH_STYLE_MAX, MaxSystem.PATH_TYPE_ABSOLUTE);
            outlet(1, maxpath);
            outlet(2,FilenameUtils.getBaseName(maxpath));
        }
        redraw();
    }

    private void toggleSelectedNode() {
        model.toggleOpen(model.getSelectedNode());
        scrollbar.setScrollProperties(visibleRows, model.rowCount());
        logger.debug("Toggle selected node");
        redraw();
    }

    // MESSAGES FROM MAX

    public void bang() {
        initialize();
    }

    // Mouse Motion from LCD
    public void mouse(int x, int y) {
        //MaxObject.post("Mouse " + x + ", " + y + " : " + firstMousedown + " : " + handleReleaseAsClick);
        // If the button is released in the same position it was pressed
        if (handleReleaseAsClick) {
            mouseClicked(x, y);
        } else if (firstMousedown) {
            firstMousedown = false;
            if (scrollbar.isVisible()) {
                scrollbar.onMouseDown(x, y);
            }
        } else if (scrollbar.isDragging()) {
            scrollbar.onMouseMove(x, y);
        }
    }

    // Mouse Button from LCD
    public void mouseDown(boolean b) {
        //MaxObject.post("mouseDown " + b + " : " + draggingHandle);
        if (b) {
            handleReleaseAsClick = false;
            firstMousedown = true;
        } else {
            // If the mouse is released and we we'rent dragging the scrollbar
            // then handle the click
            if (!scrollbar.isDragging()) {
                handleReleaseAsClick = true;
            } else {
                handleReleaseAsClick = false;
            }
            scrollbar.stopDrag();
        }
    }

    // Filter for filenames. Matches according to 'endsWith'
    public void setFilter(String filterString) {
        filter = new FileTypeFilter(filterString);
        if(model != null) {
            model.setFilter(filter);
            logger.debug("Set Filter " + filterString);
            resetRoot();
        }
    }

    public void root(String dir) throws Exception {
        if (dir.equals("~")) {
            dir = System.getProperty("user.home");
        }
        logger.debug("Set root to " + dir);

        root = new File(dir);
        if (!root.exists() || !root.isDirectory()) {
            throw new Exception("Invalid root directory");
        }

        if (model == null) {
            model = new TreeModel(root, filter);
        } else {
            model.setRoot(root);
        }

        model.addSelectionListener(this);

        resetRoot();
    }

    private void resetRoot() {
        logger.debug("Reset root");
        model.expandRoot();
        scrollbar.reset();
        scrollbar.setScrollProperties(visibleRows, model.rowCount());
    }

    public void size(int w, int h) {
        width = w;
        height = h;
        visibleRows = h / ROW_HEIGHT;
        lcd.setSize(w, h);
        logger.debug("Set size" + w + ", " + h);
        redraw();
    }

    public void setBackgroundColour(int r, int g, int b) {
        backgroundColour = new RGBColour(r, g, b);
        logger.debug("Set bg colour");
        redraw();
    }

    public void setForegroundColour(int r, int g, int b) {
        foregroundColour = new RGBColour(r, g, b);
        logger.debug("Set fg colour");
        redraw();
    }

    public void next() {
        model.setSelectedIndex(model.getSelectedIndex() + 1);
    }

    public void prev() {
        model.setSelectedIndex(model.getSelectedIndex() - 1);
    }

    public void first() {
        model.setSelectedIndex(0);
    }

    public void last() {
        model.setSelectedIndex(model.rowCount() - 1);
    }

    public void close() {
        if(model == null) return;

        if (!model.getSelectedNode().isFile && model.getSelectedNode().open) {
            toggleSelectedNode();
        }
    }

    public void open() {
        if(model == null) return;
        if (!model.getSelectedNode().isFile && !model.getSelectedNode().open) {
            toggleSelectedNode();
        }
    }

    public void keyDown(int code) {
        if (!keysEnabled) {
            return;
        }
        switch (code) {
            case NEXT_KEY:
                next();
                break;
            case PREV_KEY:
                prev();
                break;
            case OPEN_KEY:
                open();
                break;
            case CLOSE_KEY:
                close();
                break;
            case FIRST_KEY:
                first();
                break;
            case LAST_KEY:
                last();
                break;
        }
    }

    public void enableKeys(int enable) {
        keysEnabled = (enable != 0);
    }

    public void toggleKeys() {
        keysEnabled = !keysEnabled;
    }

    @Override
    protected void notifyDeleted() {
        logger.debug("Notify deleted");
        lcd = null;
        scrollbar = null;
    }

    @Override
    public void execute() {
        logger.debug("Execute");
        // Delayed initialisation
        outlet(3,"bang");

//        try {
//            root(rootPath);
//        } catch (Exception e) {
//            bail(e.getMessage());
//        }
    }
}
