package com.dominic.network_apk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.filechooser.FileSystemView;

import processing.core.PApplet;
import processing.core.PFont;
import processing.data.StringList;

public class FileExplorer {
    private int x, y, w, h, stdTs, edgeRad, margin, dark, light, lighter, textCol, textDark, border, btnSize, btnSizeSmall, startXBtns, bs, editBarX, editBarY, editBarW, btnMode = -1;
    private float textYShift;
    private Boolean isParented, isClosed = false, isCanceled = false, finishedListing = false, isListing = false, isSearching = false, finishedSearching = false;
    private String selectedPath = "", searchDir, pathToCopy = "";
    private String[] searchedStr;
    private PFont stdFont;
    private PApplet p;
    private StringList allInPathsDeleteFolder = new StringList();
    private ArrayList<File> allDirsAndFiles = new ArrayList<File>();
    private SpriteAnimation searching_sprAnim;
    private HorizontalList[] horizontalLists = new HorizontalList[5];
    private ImageButton[] fileExplorer_btns = new ImageButton[10];
    private ImageButton rename_btn;
    private EditText rename_et;
    private SearchBar searchBar;
    private FileInteractionHelper fileInteractionHelper;

    public FileExplorer(PApplet p, int x, int y, int w, int h, int stdTs, int edgeRad, int margin, int dark, int light, int lighter, int textCol, int textDark, int border, int btnSize, int btnSizeSmall, float textYShift, String[] PictoPaths, PFont stdFont) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.stdTs = stdTs;
        this.edgeRad = edgeRad;
        this.margin = margin;
        this.dark = dark;
        this.light = light;
        this.lighter = lighter;
        this.textCol = textCol;
        this.textDark = textDark;
        this.btnSize = btnSize;
        this.btnSizeSmall = btnSizeSmall;
        this.textYShift = textYShift;
        this.isParented = isParented;
        this.stdFont = stdFont;

        String home = System.getProperty("user.home");
        String downloads = home + "\\Downloads";
        File desctop = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory();
        File documents = FileSystemView.getFileSystemView().getDefaultDirectory();


        fileInteractionHelper = new FileInteractionHelper(p);

        String[][] l = { {}, {}, {}, {}, {} };
        String[] titles = { "Volumes", "Current path", "Folders", "Files", "Search Results" };
        Boolean[] showSelected = { true, false, false, false, false };
        Boolean[] showMarked = { false, false, true, true, false };

        bs = btnSizeSmall + margin * 2;
        startXBtns = p.width - bs / 2 - margin * 3 - fileExplorer_btns.length * bs;
        editBarY = y + 4 * btnSizeSmall - btnSizeSmall / 2 + margin * 2 + margin / 2;
        editBarW = (startXBtns - bs / 2 - margin * 3);
        editBarX = margin * 2 + editBarW / 2;
        
    	p.textSize(stdTs);
        for (int i = 0; i < horizontalLists.length - 1; i++) {
            String[] hoLiPictoPaths = new String[3];
            hoLiPictoPaths[0] = PictoPaths[i];
            hoLiPictoPaths[1] = PictoPaths[4];
            hoLiPictoPaths[2] = PictoPaths[5];
            horizontalLists[i] = new HorizontalList(p, x, y - 3 * btnSizeSmall - btnSizeSmall / 2 - margin * 3 + margin / 2 + i * btnSizeSmall + i * (margin * 3), w - margin * 2, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Search Results") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, showSelected[i], showMarked[i], titles[i], hoLiPictoPaths, l[i], stdFont, null);
        }
        String[] hoLiPictoPaths = new String[3];
        hoLiPictoPaths[0] = PictoPaths[7];
        hoLiPictoPaths[1] = PictoPaths[4];
        hoLiPictoPaths[2] = PictoPaths[5];
        int lastHListInd = horizontalLists.length - 1;
        horizontalLists[lastHListInd] = new HorizontalList(p, x, y - 3 * btnSizeSmall - btnSizeSmall / 2 - margin * 3 + margin / 2 + lastHListInd * btnSizeSmall + lastHListInd * (margin * 3), w - margin * 2, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Search Results") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, showSelected[lastHListInd], showMarked[lastHListInd], titles[lastHListInd], hoLiPictoPaths, l[lastHListInd], stdFont, null);

        horizontalLists[0].setList(fileInteractionHelper.getVolumes());

        String[] infoTexts = { "Home", "Copy", "Cut", "Paste", "New folder", "Delete folder", "Delete file", "Help", "", "" };
        for (int i = 0; i < fileExplorer_btns.length; i++) {
            fileExplorer_btns[i] = new ImageButton(p, startXBtns + (i * bs + i * margin), editBarY, bs, bs, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, PictoPaths[i + 8], infoTexts[i], null);
        }

        searchBar = new SearchBar(p, margin * 2 + editBarW / 4 * 3, editBarY, editBarW / 2 - margin * 2, btnSizeSmall, edgeRad, margin, stdTs, textCol, textDark, lighter, textYShift, false, "Search", PictoPaths[7], stdFont, null);
        char[] fChars = { '>', '<', ':', '"', '/', '\\', '|', '?', '*' };
        rename_et = new EditText(p, margin * 2 + editBarW / 4 - btnSizeSmall + margin * 2, editBarY, editBarW / 2 - margin * 3 - btnSizeSmall, btnSizeSmall, stdTs, lighter, textCol, edgeRad, margin, textYShift, true, false, "Rename Selected Folder", fChars, stdFont, null);
        rename_btn = new ImageButton(p, editBarX - btnSizeSmall / 2 - margin, editBarY, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, lighter, PictoPaths[6], "Rename Selected Folder", null);

        searching_sprAnim = new SpriteAnimation(p, searchBar.getButton().getX(), editBarY, btnSizeSmall - margin, btnSizeSmall - margin, 0, 129, textCol, false, "imgs/sprites/loadingGears/", null);
        
        
     // initialize all lists on starup -------------------
        try {
        String[] l0 = horizontalLists[0].getList();
        String[] l1 = { l0[horizontalLists[0].getSelectedInd()] };
        horizontalLists[1].setList(l1);
        horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(l1[0], true));
        horizontalLists[3].setList(fileInteractionHelper.getFoldersAndFiles(l1[0], false));
        horizontalLists[0].isNewSelected = false;
        }catch(Exception e) {
            e.printStackTrace();
        }
        // initialize all lists on starup -------------------
    }

    public void render() {
        p.fill(dark);
        p.stroke(dark);
        p.rect(x, y, w, h, edgeRad);
        p.fill(light);
        p.stroke(light);
        p.rect(editBarX, editBarY, editBarW, bs, edgeRad);
        for (int i = horizontalLists.length - 1; i >= 0; i--) {
            horizontalLists[i].render();
        }

        for (int i = fileExplorer_btns.length - 1; i >= 0; i--) {
            fileExplorer_btns[i].render();
        }
        searchBar.render();
        rename_et.render();
        rename_btn.render();

        handleButtons();
        handleLists();
    }

    private void handleLists() {
        if (horizontalLists[0].isNewSelected == true) {
            String[] l0 = horizontalLists[0].getList();
            String[] l1 = { l0[horizontalLists[0].getSelectedInd()] };
            horizontalLists[1].setList(l1);
            horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(l1[0], true));
            horizontalLists[3].setList(fileInteractionHelper.getFoldersAndFiles(l1[0], false));
            horizontalLists[0].isNewSelected = false;
        }

        if (horizontalLists[2].isNewSelected == true) {
            String[] l2 = horizontalLists[2].getList();
            String[] l1 = horizontalLists[1].getList();
            String[] l1New = new String[l1.length + 1];
            for (int i = 0; i < l1.length; i++) {
                l1New[i] = l1[i];
            }
            l1New[l1New.length - 1] = l2[horizontalLists[2].getSelectedInd()] + "\\\\";
            String path = "";
            for (int i = 0; i < l1New.length; i++) {
                path += l1New[i];
            }

            horizontalLists[1].setList(l1New);
            horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(path, true));
            horizontalLists[3].setList(fileInteractionHelper.getFoldersAndFiles(path, false));
            horizontalLists[2].isNewSelected = false;
        }

        if (horizontalLists[1].isNewSelected == true) {
            String path = "";
            String[] l1 = horizontalLists[1].getList();
            String[] l1New = new String[horizontalLists[1].getSelectedInd() + 1];
            if (l1.length > 0) {
                for (int i = 0; i < l1New.length; i++) {
                    l1New[i] = l1[i];
                    path += l1New[i];
                }

                horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(path, true));
                horizontalLists[3].setList(fileInteractionHelper.getFoldersAndFiles(path, false));
                horizontalLists[1].setList(l1New);
                horizontalLists[1].isNewSelected = false;
            }
        }

        if (horizontalLists[4].isNewSelected == true) {
            setPath(horizontalLists[4].getList()[horizontalLists[4].getSelectedInd()]);
            /*
             * String path = ""; String[] l4 = horizontalLists[4].getList(); String[]
             * splitStr = PApplet.split(l4[horizontalLists[4].getSelectedInd()], "\\");
             * String[] handOverStr = new String[splitStr.length];
             * 
             * if (splitStr.length > 0) { //String[] splitStr2 =
             * PApplet.split(splitStr[splitStr.length - 1], "."); File f=new
             * File(l4[horizontalLists[4].getSelectedInd()]); if (f.isDirectory()) {
             * handOverStr = new String[splitStr.length - 1]; handOverStr =
             * Arrays.copyOf(splitStr, splitStr.length - 1); } else { handOverStr =
             * splitStr; }
             * 
             * for (int i = 0; i < handOverStr.length; i++) { path += handOverStr[i] + "\\";
             * handOverStr[i] = handOverStr[i] + "\\"; }
             * 
             * horizontalLists[1].setList(handOverStr);
             * horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(path,
             * true));
             * horizontalLists[3].setList(fileInteractionHelper.getFoldersAndFiles(path,
             * false)); horizontalLists[4].isNewSelected = false; }
             */
        }

        String[] emptyList = {};
        for (int i = fileExplorer_btns.length - 1; i >= 0; i--) {
            if (fileExplorer_btns[i].getIsClicked() == true) {
                horizontalLists[4].setList(emptyList);
            }
        }
        if (rename_btn.getIsClicked() == true) {
            horizontalLists[4].setList(emptyList);
        }

    }

    public void handleButtons() {

        // rename button -------------------------------------------------

        if (rename_btn.getIsClicked() == true) {
            if (horizontalLists[2].isNewMarked == true && horizontalLists[2].getList().length > 0 && rename_et.getStrList().get(0).length() > 0) {
                File originalFile, newFile;
                String originalFileName = "", newFileName;
                String[] l2 = horizontalLists[2].getList();
                if (l2.length > 0) {
                    String[] l1 = horizontalLists[1].getList();
                    for (int i = 0; i < horizontalLists[1].getList().length; i++) {
                        originalFileName += l1[i];
                    }
                    originalFile = new File(originalFileName + l2[horizontalLists[2].getMarkedInd()]);
                    newFileName = originalFileName + rename_et.getStrList().get(0);
                    newFile = new File(newFileName);
                    originalFile.renameTo(newFile);
                    horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(originalFileName, true));
                    horizontalLists[3].setList(fileInteractionHelper.getFoldersAndFiles(originalFileName, false));
                }
            }
            rename_btn.setIsClicked(false);
        }

        // rename button -------------------------------------------------

        // search button -------------------------------------------------

        if (searchBar.getButton().getIsClicked() == true) {
            if (searchBar.getEditText().getStrList().get(0).length() > 0) {
                for (int i = allDirsAndFiles.size() - 1; i >= 0; i--) {
                    allDirsAndFiles.remove(i);
                }
                finishedListing = false;
                searchDir = "";
                String[] l1 = horizontalLists[1].getList();
                for (int i = 0; i < horizontalLists[1].getList().length; i++) {
                    searchDir += l1[i];
                }

                Thread listFilesThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isListing = true;
                        allDirsAndFiles = fileInteractionHelper.listFilesRecursive(searchDir);
                        finishedListing = true;
                    }
                });
                listFilesThread.start();

            }
            searchBar.getButton().setIsClicked(false);
        }

        if (finishedListing == true) {
            String[] allDirsAndFilesStr = new String[allDirsAndFiles.size()];
            for (int i = 0; i < allDirsAndFiles.size(); i++) {
                allDirsAndFilesStr[i] = allDirsAndFiles.get(i).toString();
            }

            Thread searchInFilesThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    isSearching = true;
                    searchedStr = searchForString(searchBar.getEditText().getStrList().get(0), allDirsAndFilesStr);
                }
            });
            searchInFilesThread.start();
            finishedListing = false;
        }
        if (isSearching == true) {
            if (finishedSearching == true) {
                if (searchedStr != null) {
                    horizontalLists[4].setList(searchedStr);
                }
                isSearching = false;
                isListing = false;
            }
        }

        if (isListing == true || isSearching == true) {
            p.stroke(lighter);
            p.fill(lighter);
            p.rect(searchBar.getButton().getX(), searchBar.getButton().getY(), searchBar.getButton().getW(), searchBar.getButton().getH(), edgeRad);
            searching_sprAnim.render();
        }
        // search button -------------------------------------------------

        // bassline buttons------------------------------------------------
        for (int i = 0; i < fileExplorer_btns.length; i++) {
            if (fileExplorer_btns[i].getIsClicked() == true) {
                switch (i) {
                case 0: //set path to user home
                    String home = System.getProperty("user.home");
                    String path = "";
                    String[] splitStr = PApplet.split(home, "\\");
                    String[] handOverStr = new String[splitStr.length];

                    if (splitStr.length > 0) {
                        // String[] splitStr2 = PApplet.split(splitStr[splitStr.length - 1], ".");
                        File f = new File(home);
                        if (f.isDirectory()) {
                            handOverStr = new String[splitStr.length - 1];
                            handOverStr = Arrays.copyOf(splitStr, splitStr.length - 1);
                        } else {
                            handOverStr = splitStr;
                        }

                        for (int i2 = 0; i2 < handOverStr.length; i2++) {
                            path += handOverStr[i2] + "\\";
                            handOverStr[i2] = handOverStr[i2] + "\\";
                        }

                        String[] hl0 = horizontalLists[0].getList();
                        for (int i2 = 0; i2 < hl0.length; i2++) {
                            String[] splitString2 = p.split(hl0[i2], "\\");
                            if (splitString2[0].equals(splitStr[0])) {
                                horizontalLists[0].setSelectedInd(i);
                                break;
                            }
                        }

                        horizontalLists[1].setList(handOverStr);
                        horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(path, true));
                        horizontalLists[3].setList(fileInteractionHelper.getFoldersAndFiles(path, false));
                    }
                    break;
                case 1: // copy folder
                    pathToCopy = "";
                    String[] c0List1 = horizontalLists[1].getList();
                    for (int i2 = 0; i2 < c0List1.length; i2++) {
                        pathToCopy += c0List1[i2];
                    }

                    String[] c0List2 = horizontalLists[2].getList();
                    if (c0List2.length > 0) {
                        pathToCopy += c0List2[horizontalLists[2].getMarkedInd()];
                    } else {
                        pathToCopy = "";
                    }
                    btnMode = 0;
                    break;

                case 2: // cut folder
                    pathToCopy = "";
                    String[] c1List1 = horizontalLists[1].getList();
                    for (int i2 = 0; i2 < c1List1.length; i2++) {
                        pathToCopy += c1List1[i2];
                    }
                    String[] c1List2 = horizontalLists[2].getList();
                    pathToCopy += c1List2[horizontalLists[2].getMarkedInd()];
                    btnMode = 1;
                    break;

                case 3: // paste folder
                    String destination = "";
                    String[] c2List1 = horizontalLists[1].getList();
                    for (int i2 = 0; i2 < c2List1.length; i2++) {
                        destination += c2List1[i2];
                    }
                    if (pathToCopy.length() > 0 && destination.length() > 0) {
                        Boolean isCopied = false;
                        fileInteractionHelper.copyFolder(pathToCopy, destination);
                        isCopied = true;
                        if (btnMode == 1 && isCopied) {
                            fileInteractionHelper.deleteFolder(pathToCopy);
                        }
                        pathToCopy = "";
                    }
                    pathToCopy = "";
                    for (int i2 = 0; i2 < c2List1.length; i2++) {
                        pathToCopy += c2List1[i2];
                    }
                    horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(pathToCopy, true));
                    horizontalLists[3].setList(fileInteractionHelper.getFoldersAndFiles(pathToCopy, false));
                    pathToCopy = "";

                    btnMode = -1;
                    break;

                case 4: // new folder
                    String c3curPath = "", c3curPath2;
                    String[] c3List1 = horizontalLists[1].getList();
                    for (int i2 = 0; i2 < c3List1.length; i2++) {
                        c3curPath += c3List1[i2];
                    }
                    c3curPath2 = c3curPath;

                    String[] c3List2 = horizontalLists[2].getList();
                    
                    String newFolderName = "";
                    if(c3List2.length>0) {
                    int folderInd = 0;
                    Boolean searchNewName = true;
                    while (searchNewName == true) {
                        Boolean isEqual = false;
                        for (int i2 = 0; i2 < c3List2.length; i2++) {
                            newFolderName = "New folder" + folderInd;
                            if (folderInd < 1) {
                                newFolderName = "New folder";
                            }
                            if (newFolderName.equals(c3List2[i2])) {
                                isEqual = true;
                                break;
                            }
                        }
                        if (isEqual == false) {
                            searchNewName = false;
                        } else {
                            folderInd++;
                        }
                    }
                    }else {
						newFolderName="New folder";
					}
                    c3curPath += "\\" + newFolderName;
                    File f = new File(c3curPath);
                    f.mkdir();
                    p.println(c3List2);
                    p.println("new folder",newFolderName);
                    horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(c3curPath2, true));
                    break;

                case 5: // delete folder
                    String pathToDelete = "";
                    String[] c4List1 = horizontalLists[1].getList();
                    for (int i2 = 0; i2 < c4List1.length; i2++) {
                        pathToDelete += c4List1[i2];
                    }
                    String[] c4List2 = horizontalLists[2].getList();
                    if (c4List2.length > 0) {
                        fileInteractionHelper.deleteFolder(pathToDelete + "\\" + c4List2[horizontalLists[2].getMarkedInd()]);
                        horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(pathToDelete, true));
                    }
                    break;

                case 6: // delete file
                    pathToDelete = "";
                    String[] c5List1 = horizontalLists[1].getList();
                    for (int i2 = 0; i2 < c5List1.length; i2++) {
                        pathToDelete += c5List1[i2] + "\\";
                    }
                    String[] c5List3 = horizontalLists[3].getList();
                    if (c5List3.length > 0) {

                        String fileName = pathToDelete + "\\" + c5List3[horizontalLists[3].getMarkedInd()];
                        File f1 = new File(fileName);
                        if (f1.exists()) {
                            f1.delete();
                        }
                        horizontalLists[3].setList(fileInteractionHelper.getFoldersAndFiles(pathToDelete, false));
                    }
                    break;

                case 7: // questions
                    break;

                case 8: // cancel selection
                    isClosed = true;
                    isCanceled = true;
                    break;

                case 9: // select

                    selectedPath = "";
                    String[] c8List1 = horizontalLists[1].getList();
                    for (int i2 = 0; i2 < c8List1.length; i2++) {
                        selectedPath += c8List1[i2];
                    }
                    String[] c8List3 = horizontalLists[3].getList();
                    if (c8List3.length > 0) {
                        selectedPath += c8List3[horizontalLists[3].getMarkedInd()];
                    }
                    isClosed = true;
                    break;

                }
                fileExplorer_btns[i].setIsClicked(false);
            }
        }
        // bassline buttons------------------------------------------------

    }

    public String[] searchForString(String searchStr, String[] searchArray) {
        ArrayList<String> resultsFiles = new ArrayList<String>();
        ArrayList<String> resultsFolders = new ArrayList<String>();
        int count = 0;

        for (int i = 0; i < searchArray.length; i++) {
            String[] splitStr = PApplet.split(searchArray[i], "\\");
            String[] splitStr2 = PApplet.split(splitStr[splitStr.length - 1], ".");
            if (splitStr2.length > 1) {
                String[] m1 = PApplet.match(splitStr[splitStr.length - 1].toUpperCase(), searchStr.toUpperCase());
                if (m1 != null) {
                    resultsFiles.add(searchArray[i]);
                }
            } else {
                for (int i2 = splitStr.length - 1; i2 >= 0; i2--) {
                    String[] m1 = PApplet.match(splitStr[i2].toUpperCase(), searchStr.toUpperCase());
                    if (m1 != null) {
                        Boolean noOtherMatch = true;
                        for (int i3 = 0; i3 < resultsFolders.size(); i3++) {
                            String[] splitStr3 = PApplet.split(resultsFolders.get(i3), "\\");
                            for (int i4 = splitStr3.length - 1; i4 >= 0; i4--) {
                                String[] m2 = PApplet.match(splitStr3[i4], searchStr);

                                if (m2 != null) {
                                    if (i2 == i4) {
                                        noOtherMatch = false;
                                        break;
                                    }
                                }
                            }
                        }
                        // noOtherMatch = true;
                        if (noOtherMatch == true) {
                            resultsFolders.add(searchArray[i]);
                            count++;
                            break;
                        }
                    }
                }
            }
        }

        // remove unwanted results ----------------------
        if (resultsFolders.size() > 0) {
            for (int i = resultsFolders.size() - 1; i >= 0; i--) {
                String[] m1 = p.match(resultsFolders.get(i), "RECYCLE.BIN");
                if (m1 != null) {
                    resultsFolders.remove(i);
                }
            }
        }

        if (resultsFiles.size() > 0) {
            for (int i = resultsFiles.size() - 1; i >= 0; i--) {
                String[] m1 = p.match(resultsFiles.get(i), "RECYCLE.BIN");
                if (m1 != null) {
                    resultsFiles.remove(i);
                }
            }
        }
        // remove unwanted results ----------------------

        String[] resStr = new String[resultsFolders.size() + resultsFiles.size()];
        for (int i = 0; i < resStr.length; i++) {
            if (i < resultsFolders.size()) {
                resStr[i] = resultsFolders.get(i);
            } else {
                resStr[i] = resultsFiles.get(i - resultsFolders.size());
            }
        }
        finishedSearching = true;
        return resStr;
    }

    public void onMouseReleased(int mouseButton) {
        rename_et.onMouseReleased();
        rename_btn.onMouseReleased();
        searchBar.onMouseReleased();
        for (int i = 0; i < horizontalLists.length; i++) {
            horizontalLists[i].onMouseReleased(mouseButton);

            horizontalLists[i].goLeft_btn.onMouseReleased();
            horizontalLists[i].goRight_btn.onMouseReleased();
        }
        rename_btn.onMouseReleased();
        for (int i = fileExplorer_btns.length - 1; i >= 0; i--) {
            fileExplorer_btns[i].onMouseReleased();
        }
    }

    public void onMousePressed() {
        for (int i = 0; i < horizontalLists.length; i++) {
            // horizontalLists[i].goLeft_btn.onMousePressed();
            // horizontalLists[i].goRight_btn.onMousePressed();
            horizontalLists[i].onMousePressed();
        }
        searchBar.onMousePressed();
        rename_btn.onMousePressed();
        for (int i = fileExplorer_btns.length - 1; i >= 0; i--) {
            fileExplorer_btns[i].onMousePressed();
        }
    }

    public void onKeyPressed(char key) {
        rename_et.onKeyPressed(key);
        searchBar.onKeyPressed(key);
    }

    public void onKeyReleased(char k) {
        for (int i = 0; i < horizontalLists.length; i++) {
            // horizontalLists[i].goLeft_btn.onKeyReleased(k);
            // horizontalLists[i].goRight_btn.onKeyReleased(k);
            horizontalLists[i].onMouseReleased(k);
        }
        searchBar.onKeyReleased(k);
        rename_et.onKeyReleased(k);
        rename_btn.onKeyReleased(k);
        for (int i = fileExplorer_btns.length - 1; i >= 0; i--) {
            fileExplorer_btns[i].onKeyReleased(k);
        }
    }

    public void onScroll(float e) {
        for (int i = 0; i < horizontalLists.length; i++) {
            horizontalLists[i].onScroll(e);
            horizontalLists[i].onScroll(e);
        }
    }

    String[] listFileNames(String dir) {
        File file = new File(dir);
        if (file.isDirectory()) {
            String names[] = file.list();
            return names;
        } else {
            // If it's not a directory
            return null;
        }
    }

    public Boolean getIsCanceled() {
        return isCanceled;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsCanceled(Boolean state) {
        isCanceled = state;
    }

    public void setIsClosed(Boolean state) {
        isClosed = state;
    }

    public void setPath(String setPath) {
        String path = "";
        String[] splitStr = p.split(setPath, "\\");
        String[] handOverStr = new String[splitStr.length];

        if (splitStr.length > 0) {
            // String[] splitStr2 = PApplet.split(splitStr[splitStr.length - 1], ".");
            File f = new File(setPath);
            if (f.isDirectory() == false) {
                File parentFolder = f.getParentFile();
                if (parentFolder != null) {
                    f = parentFolder;
                    handOverStr = new String[splitStr.length - 1];
                    handOverStr = Arrays.copyOf(splitStr, splitStr.length - 1);
                }
            } else {
                handOverStr = new String[splitStr.length];
                handOverStr = Arrays.copyOf(splitStr, splitStr.length);
            }
            if (f != null) {

                for (int i = 0; i < handOverStr.length; i++) {
                    path += handOverStr[i] + "\\";
                    handOverStr[i] = handOverStr[i] + "\\";
                }
                File checkFile= new File(path);
                if(checkFile.exists()) {
                    
                }
                horizontalLists[1].setList(handOverStr);
                horizontalLists[2].setList(fileInteractionHelper.getFoldersAndFiles(path, true));
                horizontalLists[3].setList(fileInteractionHelper.getFoldersAndFiles(path, false));
                horizontalLists[4].isNewSelected = false;
            }
        }
    }

    public String getPath() {
        return selectedPath;
    }

    public SearchBar getSearchBar() {
        return searchBar;
    }
}
